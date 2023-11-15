package org.parser.models.sources;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.parser.models.Film;
import org.parser.models.Rating;
import org.w3c.dom.Attr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public abstract class WebSource {
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

    public void createFilm(Film film) throws IOException {
        if (client == null) {
            throw new IllegalStateException("WebClient is not set.");
        }
        HtmlPage moviePage = client.getPage(film.getLink());
        Rating rating = getRating(moviePage);
        String originalTitle = getOriginalTitle(moviePage);
        film.setRating(rating);
        film.setOriginalTitle(originalTitle);
    }

    public void setClient(WebClient client) {
        this.client = client;
    }

    public void configureClient() {
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.addRequestHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) " +
                                              "AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 " +
                                              "Mobile/10A5376e" + " Safari/8536.25");
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
}
