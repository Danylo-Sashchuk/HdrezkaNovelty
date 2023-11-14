package org.parser.models;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.w3c.dom.Attr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public abstract class WebSource {
    private static final Logger LOG = Logger.getLogger(WebSource.class.getName());
    protected URL mainPage;
    protected WebClient client;

    public com.gargoylesoftware.htmlunit.html.HtmlPage getPage(String link) throws IOException {
        return client.getPage(link);
    }

    public URL getMainPage() {
        return mainPage;
    }

    public String getTitle(DomElement div) {
        return ((DomText) div.getByXPath("./div[@class=\"b-content__inline_item-link\"]/a" + "/text()")
                .get(0)).getWholeText();
    }

    public URL getImageUrl(DomElement div) throws MalformedURLException {
        return getUrl(div, "./div/a/img/@src");
    }

    public URL getMovieUrl(DomElement div) throws MalformedURLException {
        //language=XPath
        return getUrl(div, "./div/a/@href");
    }

    public URL getUrl(DomElement div, String xpathExpr) throws MalformedURLException {
        return new URL(((Attr) div.getByXPath(xpathExpr).get(0)).getValue());
    }

    public String[] getDescription(DomElement div) {
        DomText domText = (DomText) div.getByXPath("./div[2]/div[1]/text()").get(0);
        return domText.getWholeText().split(",");
    }

    public URL getLink(URL url) throws MalformedURLException {
        return url;
    }

    public Film createFilm(Film film) throws IOException {
        HtmlPage moviePage = client.getPage(film.getLink());
        Rating rating = getRating(moviePage);
        String originalTitle = getOriginalTitle(moviePage);
        film.setRating(rating);
        film.setOriginalTitle(originalTitle);
        return film;
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

    private String getOriginalTitle(HtmlPage moviePage) {
        return ((DomText) moviePage.getByXPath("//div[@class=\"b-post__origtitle" + "\"]/text()")
                .get(0)).getWholeText();
    }

    class FilmBuilder {
        private Film buildFilm(DomElement filmElement) {
            Film rawFilm = null;
            try {
                String[] description = getDescription(filmElement);
                String title = getTitle(filmElement);
                if (description.length < 3) {
                    LOG.info("Movie does not have enough information to be taken into consideration.");
                    return null;
                }
                LOG.info("Parsing: %s (%s, %s, %s)".formatted(title, description[0], description[1], description[2]));
                if (!isWatchable(description)) {
                    return null;
                }
                URL image = getImageUrl(filmElement);
                URL link = getLink(getMovieUrl(filmElement));
                rawFilm = new Film(image, title, Integer.parseInt(description[0]), description[1].trim(),
                        description[2].trim(), link);
            } catch (RuntimeException | IOException e) {
                LOG.severe("Error with film. Skipping the film.");
            }
            return rawFilm;
        }
    }
}
