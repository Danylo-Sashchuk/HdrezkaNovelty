package org.parser;

import org.parser.models.Film;
import org.parser.models.LiveWebSource;
import org.parser.models.Parser;
import org.parser.util.ConsolePrinter;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    //TODO add constants links
    public static void main(String[] args) throws MalformedURLException {
        Parser parser = new Parser.ParserBuilder(
                new LiveWebSource(new URL("https://hdrezka.website/page/%d/?filter=last&genre=1")))
                .endPage(3)
                .build();

        for (Film film : parser.parse()) {
            ConsolePrinter.print(film);
        }

        //        Parser suggester1 = new Parser.ParserBuilder(
        //                new FileWebSource("src/test/resources/webmap.properties",
        //                        new URL("file:src/test/resources/%d/main_page.html")))
        //                .build();
        //        for (Film film : suggester1.parse()) {
        //            ConsolePrinter.printForCreation(film);
        //        }
    }
}
