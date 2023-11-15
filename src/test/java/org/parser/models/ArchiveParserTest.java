package org.parser.models;

import org.parser.models.sources.FileWebSource;

import java.net.MalformedURLException;
import java.net.URL;

public class ArchiveParserTest extends AbstractParserTest {

    public ArchiveParserTest() throws MalformedURLException {
        super(new Parser.ParserBuilder(
                new FileWebSource("src/test/resources/webmap.properties",
                        new URL("file:src/test/resources/%d/main_page.html"))).build()
        );
    }
}
