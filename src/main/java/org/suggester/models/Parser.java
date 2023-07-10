package org.suggester.models;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.suggester.conc.ScrapperThread;
import org.suggester.ratingStrategies.WeightedAverageStrategy;
import org.suggester.util.Config;
import org.suggester.util.FilmComparator;
import org.w3c.dom.Attr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private static final Logger LOG = Logger.getLogger(Parser.class.getName());
    private final Set<String> countriesWhitelist;
    private final int startYear;
    private final int endYear;
    private final int startPage;
    private final int endPage;
    private final WebSource webSource;
    private FilmComparator filmComparator;
    private final ConcurrentLinkedDeque<Film> watchableFilms = new ConcurrentLinkedDeque<>();
    private final List<Thread> threads = new ArrayList<>();

    private Parser(SuggesterBuilder builder) {
        LOG.log(Level.INFO, "Creating Parser");
        this.countriesWhitelist = builder.countriesWhitelist;
        this.startYear = builder.startYear;
        this.endYear = builder.endYear;
        this.startPage = builder.startPage;
        this.endPage = builder.endPage;
        this.filmComparator = builder.filmComparator;
        this.webSource = builder.webSource;
        LOG.log(Level.INFO, "Parser is created");
    }

    public void changeFilmComparator(FilmComparator filmComparator) {
        this.filmComparator = filmComparator;
    }

    public List<Film> parse() {
        try (WebClient client = new WebClient()) {
            setupClient(client);
            int currentPage = startPage;
            while (currentPage <= endPage) {
                parsePage(client, currentPage);
                currentPage++;
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LOG.info("Sorting films");
            List<Film> result = new ArrayList<>(watchableFilms);
            result.sort(filmComparator);
            return result;
        } catch (IOException e) {
            LOG.severe("Severe error, parsing cannot be continued." + e);
            throw new RuntimeException(e);
        }
    }

    private void parsePage(WebClient client, int currentPage) throws IOException {
        LOG.info("Parsing website's page " + currentPage);
        String website = String.format(webSource.getMainPage().toString(), currentPage);
        try {
            HtmlPage page = client.getPage(website);
            List<DomElement> allFilms = page.getByXPath("//div[@class=\"b-content__inline_item\"]");
            int counter = 0;
            List<Film> unfinishedFilms = new ArrayList<>();
            for (DomElement div : allFilms) {
                String[] description = getDescription(div);
                if (description.length < 3) {
                    LOG.info("Movie does not have enough information to be taken into consideration.");
                    return;
                }
                LOG.info("Parsing: %s, %s, %s ".formatted(description[0], description[1], description[2]));
                if (!isWatchable(description)) {
                    continue;
                }
                URL image = getUrl(div, "./div/a/img/@src");
                URL link = webSource.getPage(getUrl(div, "./div/a/@href"));
                String title = getTitle(div);
                Film newFilm = new Film(image, title, Integer.parseInt(description[0]), description[1].trim(),
                        description[2].trim(), link);
                counter++;
                unfinishedFilms.add(newFilm);
                if (counter == 5) {
                    parseFilms(unfinishedFilms);
                    counter = 0;
                    unfinishedFilms.clear();
                }
            }
        } catch (IOException e) {
            LOG.severe("Error with " + currentPage + " page. Skipping the page.");
        }
    }

    private void parseFilms(List<Film> unfinishedFilms) throws IOException {
        try {
            Thread thread = new Thread(new ScrapperThread(unfinishedFilms, watchableFilms));
            threads.add(thread);
            thread.start();
        } catch (RuntimeException e) {
            LOG.severe("Parsing interrupted. " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            System.out.println("Film parsing is interrupted, skip this film.");
        }
    }

    private String getTitle(DomElement div) {
        return ((DomText) div.getByXPath("./div[@class=\"b-content__inline_item-link\"]/a" + "/text()")
                .get(0)).getWholeText();
    }

    private URL getUrl(DomElement div, String xpathExpr) throws MalformedURLException {
        return new URL(((Attr) div.getByXPath(xpathExpr).get(0)).getValue());
    }

    private String[] getDescription(DomElement div) {
        DomText domText = (DomText) div.getByXPath("./div[2]/div[1]/text()").get(0);
        return domText.getWholeText().split(",");
    }

    private void setupClient(WebClient client) {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
    }

    private boolean isWatchable(String[] description) {
        int year = Integer.parseInt(description[0]);
        String country = description[1].trim();
        return countriesWhitelist.contains(country) && year >= startYear && year <= endYear;
    }

    public static class SuggesterBuilder {
        private final Config config = Config.get();
        private final float weightedAverageRatingWeight = Float.parseFloat(config.getProperty(
                "weightedAverageRatingWeight"));
        private final float weightedAverageVotesWeight = Float.parseFloat(config.getProperty(
                "weightedAverageVotesWeight"));
        private FilmComparator filmComparator =
                new FilmComparator(new WeightedAverageStrategy(weightedAverageRatingWeight,
                        weightedAverageVotesWeight));
        private Set<String> countriesWhitelist = new HashSet<>(List.of(Config.get()
                .getProperty("countriesWhitelist")
                .split(",")));
        private int startYear = Integer.parseInt(config.getProperty("startYear"));
        private int endYear = Integer.parseInt(config.getProperty("endYear"));
        private int startPage = Integer.parseInt(config.getProperty("startPage"));
        private int endPage = Integer.parseInt(config.getProperty("endPage"));
        private WebSource webSource;

        public SuggesterBuilder(WebSource webSource) {
            this.webSource = webSource;
        }

        public SuggesterBuilder countriesWhitelist(Set<String> countriesWhitelist) {
            this.countriesWhitelist = countriesWhitelist;
            return this;
        }

        public SuggesterBuilder startYear(int startYear) {
            this.startYear = startYear;
            return this;
        }

        public SuggesterBuilder endYear(int endYear) {
            this.endYear = endYear;
            return this;
        }

        public SuggesterBuilder startPage(int startPage) {
            this.startPage = startPage;
            return this;
        }

        public SuggesterBuilder endPage(int endPage) {
            this.endPage = endPage;
            return this;
        }

        public SuggesterBuilder filmComparator(FilmComparator filmComparator) {
            this.filmComparator = filmComparator;
            return this;
        }

        public Parser build() {
            if (endYear < startYear) {
                LOG.severe("Parser cannot be created. End year for searching is less than start year");
                throw new
                        IllegalArgumentException("Parser cannot be created. End year for searching is less than start" +
                                                 " year");
            }
            if (endPage < startPage) {
                LOG.severe("Parser cannot be created. End page for parsing is less than start page");
                throw new
                        IllegalArgumentException("Parser cannot be created. End page for parsing is less than start " +
                                                 "page");
            }
            return new Parser(this);
        }

        public SuggesterBuilder webSource(WebSource webSource) {
            this.webSource = webSource;
            return this;
        }
    }
}

