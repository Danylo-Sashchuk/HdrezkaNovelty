package org.parser.models;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.parser.conc.ScrapperThread;
import org.parser.ratingStrategies.WeightedAverageStrategy;
import org.parser.util.Config;
import org.parser.util.FilmComparator;
import org.parser.util.WebHelper;
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
    private final ConcurrentLinkedDeque<Film> watchableFilms = new ConcurrentLinkedDeque<>();
    private final List<Thread> threads = new ArrayList<>();
    private FilmComparator filmComparator;

    private Parser(ParserBuilder builder) {
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
            WebHelper.setClientSettings(client);
            client.addRequestHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) " +
                                                  "AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e" +
                                                  " Safari/8536.25");
            for (int i = startPage; i <= endPage; i++) {
                parsePage(client, i);
            }
            joinThreads();
            LOG.info("Sorting films");
            List<Film> result = new ArrayList<>(watchableFilms);
            result.sort(filmComparator);
            return result;
        } catch (IOException e) {
            LOG.severe("Severe error, parsing cannot be continued." + e);
            throw new RuntimeException(e);
        }
    }

    private void joinThreads() {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void parsePage(WebClient client, int currentPage) throws IOException {
        LOG.info("Parsing website's page " + currentPage);
        String website = String.format(webSource.getMainPage().toString(), currentPage);
        try {

            HtmlPage page = client.getPage(website);
            List<DomElement> allFilms = page.getByXPath("//div[@class=\"b-content__inline_item\"]");
            for (DomElement div : allFilms) {
                try {
                    parseFilm(div);
                } catch (RuntimeException e) {
                    LOG.severe("Error with film. Skipping the page.");
                }
            }
        } catch (IOException e) {
            LOG.severe("Error with " + currentPage + " page. Skipping the page.");
        }
    }

    private void parseFilm(DomElement div) throws IOException {
        String[] description = getDescription(div);
        if (description.length < 3) {
            LOG.info("Movie does not have enough information to be taken into consideration.");
            return;
        }
        LOG.info("Parsing: %s, %s, %s ".formatted(description[0], description[1], description[2]));
        if (!isWatchable(description)) {
            return;
        }
        URL image = getImageUrl(div);
        URL link = webSource.getPage(getMovieUrl(div));
        String title = getTitle(div);
        Film rawFilm = new Film(image, title, Integer.parseInt(description[0]), description[1].trim(),
                description[2].trim(), link);
        composeFilm(rawFilm);
    }

    private void composeFilm(Film rawFilm) {
        try {
            Thread thread = new Thread(new ScrapperThread(rawFilm, watchableFilms));
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

    private URL getImageUrl(DomElement div) throws MalformedURLException {
        return getUrl(div, "./div/a/img/@src");
    }

    private URL getMovieUrl(DomElement div) throws MalformedURLException {
        //language=XPath
        return getUrl(div, "./div/a/@href");
    }

    private URL getUrl(DomElement div, String xpathExpr) throws MalformedURLException {
        return new URL(((Attr) div.getByXPath(xpathExpr).get(0)).getValue());
    }

    private String[] getDescription(DomElement div) {
        DomText domText = (DomText) div.getByXPath("./div[2]/div[1]/text()").get(0);
        return domText.getWholeText().split(",");
    }

    private boolean isWatchable(String[] description) {
        int year = Integer.parseInt(description[0]);
        String country = description[1].trim();
        return countriesWhitelist.contains(country) && year >= startYear && year <= endYear;
    }

    public static class ParserBuilder {
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

        public ParserBuilder(WebSource webSource) {
            this.webSource = webSource;
        }

        public ParserBuilder countriesWhitelist(Set<String> countriesWhitelist) {
            this.countriesWhitelist = countriesWhitelist;
            return this;
        }

        public ParserBuilder startYear(int startYear) {
            this.startYear = startYear;
            return this;
        }

        public ParserBuilder endYear(int endYear) {
            this.endYear = endYear;
            return this;
        }

        public ParserBuilder startPage(int startPage) {
            this.startPage = startPage;
            return this;
        }

        public ParserBuilder endPage(int endPage) {
            this.endPage = endPage;
            return this;
        }

        public ParserBuilder filmComparator(FilmComparator filmComparator) {
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

        public ParserBuilder webSource(WebSource webSource) {
            this.webSource = webSource;
            return this;
        }
    }
}

