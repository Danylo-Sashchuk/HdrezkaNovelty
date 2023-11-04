package org.parser.conc;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.parser.models.Film;
import org.parser.models.Rating;
import org.parser.util.WebHelper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

public class ScrapperThread implements Runnable {
    private static final Logger LOG = Logger.getLogger(ScrapperThread.class.getName());
    private final ConcurrentLinkedDeque<Film> resultFilms;
    private final Film film;

    public ScrapperThread(Film film, ConcurrentLinkedDeque<Film> resultFilms) {
        this.resultFilms = resultFilms;
        this.film = film;
    }

    @Override
    public void run() {
        try (WebClient client = new WebClient()) {
            LOG.info("Created a new thread for " + film.getTitle());
            WebHelper.setClientSettings(client);
            client.addRequestHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) " +
                                                  "AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e" +
                                                  " Safari/8536.25");
            createFilm(client, film);
            LOG.info("Thread finished for " + film.getTitle());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFilm(WebClient client, Film film) throws IOException {
        LOG.info("Jumping on page - " + film.getTitle());
        HtmlPage moviePage = client.getPage(film.getLink());
        Rating rating = getRating(moviePage);
        String originalTitle = getOriginalTitle(moviePage);
        film.setRating(rating);
        film.setOriginalTitle(originalTitle);
        resultFilms.add(film);
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