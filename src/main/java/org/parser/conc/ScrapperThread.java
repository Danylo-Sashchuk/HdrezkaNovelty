package org.parser.conc;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import org.parser.models.Film;
import org.parser.models.Rating;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

public class ScrapperThread implements Runnable {
    private static final Logger LOG = Logger.getLogger(ScrapperThread.class.getName());
    private final ConcurrentLinkedDeque<Film> resultFilms;
    private final List<Film> films;
    private final WebClient client;

    public ScrapperThread(List<Film> rawFilms, WebClient client, ConcurrentLinkedDeque<Film> resultFilms) {
        this.resultFilms = resultFilms;
        this.films = rawFilms;
        this.client = client;
    }

    @Override
    public void run() {
        LOG.info("Creating a new thread for several films");
        for (Film film : films) {
            try {
                LOG.info("Parsing film " + film.getTitle());
                createFilm(client, film);
            } catch (IOException e) {
                LOG.severe("Error while parsing film " + film.getTitle());
                throw new RuntimeException(e);
            }
        }
        LOG.info("Thread finished");
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