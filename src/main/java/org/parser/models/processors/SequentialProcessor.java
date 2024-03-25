package org.parser.models.processors;

import org.parser.models.Film;
import org.parser.models.sources.WebSource;

import java.util.ArrayList;

/**
 * @author Danylo Sashchuk <p>
 * 11/13/23
 */

public class SequentialProcessor extends Processor {
    public SequentialProcessor(WebSource webSource) {
        super(webSource);
        result = new ArrayList<>();
    }

    @Override
    public void endProcessing() {
        for (Film film : result) {
            completeFilm(film);
        }
    }

    private void completeFilm(Film film) {
        LOG.info("Composing " + film.getTitle() + ".");
        try {
            webSource.createFilm(film);
        } catch (IllegalStateException e) {
            LOG.warning("WebClient is not set. Abort the program.");
        } catch (Exception e) {
            LOG.warning("Failed to create " + film.getTitle() + ". " + film.getLink() + " Skipping.");
        }
    }
}
