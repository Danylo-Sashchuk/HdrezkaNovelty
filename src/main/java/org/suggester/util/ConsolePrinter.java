package org.suggester.util;

import org.suggester.models.Film;

public class ConsolePrinter {
    public static void print(Film film) {
        System.out.println(film.getTitle());
        System.out.println(film.getGenre() + ", " + film.getCountry() + ", " + film.getYear());
        if (film.getRating() != null) {
            System.out.println(film.getRating().rating() + " : " + film.getRating().count());
        }
        System.out.println(film.getLink());
        System.out.println("--------------------");
    }

    public static void printForCreation(Film f) {
        System.out.printf("""
                        new Film(
                                 new URL("%s"),
                                 "%s",
                                 "%s",
                                 %s,
                                 "%s",
                                 "%s",
                                 new URL("%s"),
                                 null),
                        """, f.getImage(),
                f.getTitle(),
                f.getOriginalTitle(),
                f.getYear(),
                f.getCountry(),
                f.getGenre(),
                f.getLink()
        );
    }
}
