package org.suggester;

import org.suggester.models.FileWebSource;
import org.suggester.models.Film;
import org.suggester.models.Suggester;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
        //        Suggester suggester = new Suggester.SuggesterBuilder(new LiveWebSource(new URL("https://hdrezka" +
        //                                                                                       "
        //                                                                                       .website/page/%d/?filter=last" +
        //                                                                                       "&genre=1"
        //        ))).endPage(2).build();
        //        for (Film film : suggester.parse()) {
        //            System.out.println(film);
        //        }

        Suggester suggester = new Suggester.SuggesterBuilder(new FileWebSource("src/test/resources/webmap.properties"
                , new URL("https://hdrezka.website/page/%d/?filter=last&genre=1"))).endPage(2)
                .build();
        for (Film film : suggester.parse()) {
            System.out.println(film);
        }
    }
}
