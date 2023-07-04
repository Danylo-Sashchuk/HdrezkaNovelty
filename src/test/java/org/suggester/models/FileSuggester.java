package org.suggester.models;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.suggester.ratingStrategies.WeightedAverageStrategy;
import org.suggester.util.Config;
import org.suggester.util.FilmComparator;
import org.w3c.dom.Attr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSuggester {
    private static final Logger LOG = Logger.getLogger(Suggester.class.getName());
    private final String hdrezka = "file:src/test/resources/%d/main_page.html";
    private final Set<String> countriesWhitelist;
    private final int startYear;
    private final int startPage;
    private final int endPage;
    private final int endYear;
    private FilmComparator filmComparator;
    private Config config = Config.get();

    private FileSuggester(SuggesterBuilder builder) {
        LOG.log(Level.INFO, "Creating Suggester");
        this.countriesWhitelist = builder.countriesWhitelist;
        this.startYear = builder.startYear;
        this.endYear = builder.endYear;
        this.startPage = builder.startPage;
        this.endPage = builder.endPage;
        this.filmComparator = builder.filmComparator;
        LOG.log(Level.INFO, "Suggester is created");
    }

    public void changeFilmComparator(FilmComparator filmComparator) {
        this.filmComparator = filmComparator;
    }

    public List<Film> parse() throws IOException {
        try (WebClient client = new WebClient()) {
            setupClient(client);
            List<Film> watchableFilms = new ArrayList<>();
            int currentPage = startPage;
            while (currentPage <= endPage) {
                LOG.info("Parsing website's page " + currentPage);
                String website = String.format(hdrezka, currentPage);
                HtmlPage page = client.getPage(website);
                List<DomElement> allFilms = page.getByXPath("//div[@class=\"b-content__inline_item\"]");
                for (DomElement div : allFilms) {
                    String[] description = getDescription(div);
                    LOG.info("Parsing: %s, %s, %s ".formatted(description[0], description[1], description[2]));
                    if (!isWatchable(description)) {
                        continue;
                    }
                    try {
                        URL image = getUrl(div, "./div/a/img/@src");
                        URL link = getUrl(div, "./div/a/@href");
                        String title = getTitle(div);

                        LOG.info("Jumping to %s page".formatted(title));
                        String movie = config.getProperty(URLEncoder.encode(String.valueOf(link),
                                StandardCharsets.UTF_8));
                        HtmlPage moviePage =
                                client.getPage("file:" + "src/test/resources/" + movie);
                        Rating rating = getRating(moviePage);
                        String originalTitle = getOriginalTitle(moviePage);
                        LOG.info("Creating %s film".formatted(title));
                        Film film = new Film(image, title, originalTitle, Integer.parseInt(description[0]),
                                description[1].trim(), description[2].trim(), link, rating);
                        watchableFilms.add(film);
                    } catch (RuntimeException e) {
                        LOG.severe("Parsing interrupted. " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                        System.out.println("Film parsing is interrupted, skip this film.");
                    }
                }
                currentPage++;
            }
            LOG.info("Sorting films");
            watchableFilms.sort(filmComparator);
            return watchableFilms;
        }
    }

    private String getTitle(DomElement div) {
        return ((DomText) div.getByXPath("./div[@class=\"b-content__inline_item-link\"]/a" + "/text()")
                .get(0)).getWholeText();
    }

    private String getOriginalTitle(HtmlPage moviePage) {
        return ((DomText) moviePage.getByXPath("//div[@class=\"b-post__origtitle" + "\"]/text()")
                .get(0)).getWholeText();
    }

    private URL getUrl(DomElement div, String xpathExpr) throws MalformedURLException {
        return new URL(((Attr) div.getByXPath(xpathExpr).get(0)).getValue());
    }

    private Rating getRating(HtmlPage page) {
        List<Object> byXPath = page.getByXPath("//span[@class=\"b-post__info_rates imdb\"]");
        if (byXPath.isEmpty()) {
            return null;
        }
        HtmlSpan ratingRow = (HtmlSpan) byXPath.get(0);
        float rating = Float.parseFloat(((DomText) ratingRow.getByXPath("./span/text()").get(0)).getWholeText());
        String count = ((DomText) ratingRow.getByXPath("./i/text()").get(0)).getWholeText();
        int votes = Integer.parseInt(count.substring(1, count.length() - 1).replaceAll("\\s+", ""));
        return new Rating(rating, votes);
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
        return countriesWhitelist.contains(description[1].trim()) && Integer.parseInt(description[0]) >= startYear;
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

        public FileSuggester build() {
            return new FileSuggester(this);
        }
    }
}
