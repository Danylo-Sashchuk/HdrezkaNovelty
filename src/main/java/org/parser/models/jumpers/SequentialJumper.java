package org.parser.models.jumpers;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.parser.conc.ScrapperThread;
import org.parser.models.Film;
import org.parser.models.WebSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Danylo Sashchuk <p>
 * 11/13/23
 */

public class SequentialJumper extends Jumper {
    private final List<Thread> threads;

    public SequentialJumper(WebSource webSource) {
        super(webSource);
        result = new ArrayList<>();
        threads = new ArrayList<>();
    }

    @Override
    protected void doParse(HtmlPage page) {
        List<DomElement> allFilms = page.getByXPath("//div[@class=\"b-content__inline_item\"]");
        List<DomElement> tempStorage = new ArrayList<>();
        for (DomElement div : allFilms) {
            Film rawFilm = parseFilm(div);
            if (rawFilm != null) {
                completeFilm(rawFilm);
            }
        }
    }

    //TODO consider renaming of filmElement
    private Film parseFilm(DomElement filmElement) {

    }

    private void completeFilm(Film film) {
        LOG.info("Jumping on page - " + film.getTitle());

    }

    private boolean isWatchable(String[] description) {
        int year = Integer.parseInt(description[0]);
        String country = description[1].trim();
        return countriesWhitelist.contains(country) && year >= startYear && year <= endYear;
    }
}
