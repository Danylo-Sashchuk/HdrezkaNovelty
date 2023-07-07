package org.suggester;

import org.suggester.models.FileWebSource;
import org.suggester.models.Film;
import org.suggester.models.LiveWebSource;
import org.suggester.models.Suggester;
import org.suggester.util.ConsolePrinter;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    //TODO add constants links
    public static void main(String[] args) throws MalformedURLException {
//                Suggester suggester = new Suggester.SuggesterBuilder(
//                        new LiveWebSource(new URL("https://hdrezka.website/page/%d/?filter=last&genre=1")))
//                        .endPage(2)
//                        .build();
//                for (Film film : suggester.parse()) {
//                    ConsolePrinter.print(film);
//                }

//        Suggester suggester1 = new Suggester.SuggesterBuilder(
//                new FileWebSource("src/test/resources/webmap.properties",
//                        new URL("file:src/test/resources/%d/main_page.html")))
//                .build();
//        for (Film film : suggester1.parse()) {
//            ConsolePrinter.printForCreation(film);
//        }
    }
}
