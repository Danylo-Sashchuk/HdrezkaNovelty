package org.suggester;

import org.suggester.models.Film;
import org.suggester.models.LiveWebSource;
import org.suggester.models.Parser;
import org.suggester.util.ConsolePrinter;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    //TODO add constants links
    public static void main(String[] args) throws MalformedURLException {
        Parser suggester = new Parser.SuggesterBuilder(
                new LiveWebSource(new URL("https://hdrezka.website/page/%d/?filter=last&genre=1")))
                .endPage(3)
                .build();

        for (Film film : suggester.parse()) {
            ConsolePrinter.print(film);
        }

        //        Parser suggester1 = new Parser.SuggesterBuilder(
        //                new FileWebSource("src/test/resources/webmap.properties",
        //                        new URL("file:src/test/resources/%d/main_page.html")))
        //                .build();
        //        for (Film film : suggester1.parse()) {
        //            ConsolePrinter.printForCreation(film);
        //        }
    }
}
