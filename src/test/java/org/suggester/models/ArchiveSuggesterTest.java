package org.suggester.models;

import java.net.MalformedURLException;
import java.net.URL;

public class ArchiveSuggesterTest extends AbstractSuggesterTest {

    public ArchiveSuggesterTest() throws MalformedURLException {
        super(new Suggester.SuggesterBuilder(
                new FileWebSource("src/test/resources/webmap.properties",
                        new URL("file:src/test/resources/%d/main_page.html"))).build()
        );
    }
}
