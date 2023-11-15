package org.parser.models.processors;

import org.parser.models.Film;
import org.parser.models.sources.WebSource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Danylo Sashchuk <p>
 * 11/13/23
 */

public abstract class FilmProcessor {
    protected static final Logger LOG = Logger.getLogger(FilmProcessor.class.getName());
    final protected WebSource webSource;
    protected List<Film> result = new ArrayList<>();


    public FilmProcessor(WebSource webSource) {
        this.webSource = webSource;
    }

    public List<Film> getResult() {
        return new ArrayList<>(result);
    }

    public void process(List<Film> films) {
        LOG.info("Adding films");
        result.addAll(films);
    }

    public void endProcessing() {
        LOG.info("Processing is finished");
    }
}
