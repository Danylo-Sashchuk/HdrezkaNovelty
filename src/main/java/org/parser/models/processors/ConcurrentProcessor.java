package org.parser.models.processors;

import org.parser.conc.ScrapperThread;
import org.parser.models.Film;
import org.parser.models.sources.WebSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Danylo Sashchuk <p>
 * 11/13/23
 */

public class ConcurrentProcessor extends Processor {
    final private int MAX_NUMBER_OF_FILMS_IN_THREAD = 10;
    private List<Thread> threads = new ArrayList<>();
    private List<Film> result;

    public ConcurrentProcessor(WebSource webSource) {
        super(webSource);
        result = new CopyOnWriteArrayList<>(); // only concurrent collections can be used here
    }

    @Override
    public void process(List<Film> films) {
        List<Film> filmsToProcess = new ArrayList<>();
        for (Film film : films) {
            if (filmsToProcess.size() == MAX_NUMBER_OF_FILMS_IN_THREAD) {
                composeFilms(filmsToProcess);
                filmsToProcess.clear();
            }
            filmsToProcess.add(film);
        }
        if (!filmsToProcess.isEmpty()) {
            composeFilms(filmsToProcess);
        }
    }

    @Override
    public void endProcessing() {
        joinThreads();
    }

    @Override
    public List<Film> getResult() {
        return result;
    }

    private void composeFilms(List<Film> rawFilms) {
        try {
            Thread thread = new Thread(new ScrapperThread(new ArrayList<>(rawFilms), result, webSource));
            threads.add(thread);
            thread.start();
        } catch (RuntimeException e) {
            LOG.severe("Parsing interrupted. " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
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

}
