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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Suggester {
    private static final Logger LOG = Logger.getLogger(Suggester.class.getName());
    private final String hdrezka = "https://hdrezka.website/page/%d/?filter=last&genre=1";
    private final Set<String> countriesWhitelist;
    private final int startYear;
    private final int startPage;
    private final int endPage;
    private final int endYear;
    private FilmComparator filmComparator;

    public Suggester(Set<String> countriesWhitelist, int startYear, int endYear, int startPage, int endPage,
                     FilmComparator filmComparator) {

        countriesWhitelist = new HashSet<>(List.of(Config.get().getProperty("countriesWhitelist").split(",")));
        startYear = Integer.parseInt(Config.get().getProperty("startYear"));
        endYear = Integer.parseInt(Config.get().getProperty("endYear"));
        startPage = Integer.parseInt(Config.get().getProperty("startPage"));
        endPage = Integer.parseInt(Config.get().getProperty("endPage"));
        LOG.log(Level.INFO, "Created Suggester");
        filmComparator = new FilmComparator(new WeightedAverageStrategy());
    }

    public void setFilmComparator(FilmComparator filmComparator) {
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
                        HtmlPage moviePage = client.getPage(link);
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
        return ((DomText) div.getByXPath("./div[@class=\"b-content__inline_item-link\"]/a" +
                                         "/text()")
                .get(0)).getWholeText();
    }

    private String getOriginalTitle(HtmlPage moviePage) {
        return ((DomText) moviePage.getByXPath("//div[@class=\"b-post__origtitle" +
                                               "\"]/text()")
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
}
