package org.parser.conc;

import org.parser.models.Film;
import org.parser.models.sources.WebSource;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ScrapperThread implements Runnable {
    private static final Logger LOG = Logger.getLogger(ScrapperThread.class.getName());
    private final List<Film> resultFilms;
    private final List<Film> films;
    private final WebSource webSource;

    public ScrapperThread(List<Film> films, List<Film> resultFilms, WebSource webSource) {
        this.resultFilms = resultFilms;
        this.films = films;
        this.webSource = webSource;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        for (Film film : films) {
            sb.append(film.getTitle()).append(", ");
        }
        LOG.info("Creating a new thread for films: " + sb);
        for (Film film : films) {
            try {
                LOG.info("Jumping to the " + film.getTitle());
                webSource.createFilm(film);
                resultFilms.add(film);
            } catch (IOException | RuntimeException e) {
                LOG.severe("Error while parsing film " + film.getTitle());
                throw new RuntimeException(e);
            }
        }
        LOG.info("Thread finished for films: " + sb);
    }
}