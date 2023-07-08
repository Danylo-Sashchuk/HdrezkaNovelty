package org.suggester.models;

import java.net.MalformedURLException;
import java.net.URL;

public class ArchiveParserTest extends AbstractParserTest {

    public ArchiveParserTest() throws MalformedURLException {
        super(new Parser.SuggesterBuilder(
                new FileWebSource("src/test/resources/webmap.properties",
                        new URL("file:src/test/resources/%d/main_page.html"))).build()
        );
    }
}