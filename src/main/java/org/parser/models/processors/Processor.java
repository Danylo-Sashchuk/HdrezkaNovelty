package org.parser.models.processors;

import org.parser.models.Film;
import org.parser.models.sources.WebSource;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Danylo Sashchuk <p>
 * 11/13/23
 * <p></p>
 * This class represents a processor that is used to process raw films from that are added.
 */

public abstract class Processor {
    protected static final Logger LOG = Logger.getLogger(Processor.class.getName());
    final protected WebSource webSource;
    protected List<Film> result;

    public Processor(WebSource webSource) {
        this.webSource = webSource;
    }

    public List<Film> getResult() {
        return result;
    }

    public void process(List<Film> films) {
        LOG.info("Adding films");
        result.addAll(films);
    }

    public void endProcessing() {
        LOG.info("Processing is finished");
    }
}
