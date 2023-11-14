package org.parser.models.jumpers;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.parser.models.Film;
import org.parser.models.WebSource;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * @author Danylo Sashchuk <p>
 * 11/13/23
 */

public abstract class Jumper {
    protected static final Logger LOG = Logger.getLogger(Jumper.class.getName());
    final protected WebSource webSource;

    protected Collection<Film> result;

    public Jumper(WebSource webSource) {
        this.webSource = webSource;
    }

    public Collection<Film> getResult() {
        return result;
    }

    final public void parsePage(int pageNumber) throws IOException {
        LOG.info("Parsing website's page " + pageNumber);
        String website = String.format(webSource.getMainPage().toString(), pageNumber);
        try {
            HtmlPage page = webSource.getPage(website);
            doParse(page);
        } catch (RuntimeException e) {
            LOG.severe("Error with " + pageNumber + " page. Skipping the page.");
        }
    }

    protected abstract void doParse(HtmlPage page);
}
