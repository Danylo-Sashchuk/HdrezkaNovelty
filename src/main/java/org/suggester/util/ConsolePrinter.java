package org.suggester.util;

import org.suggester.models.Film;

public class ConsolePrinter {
    public static void print(Film film) {
        System.out.println(film.getTitle());
        System.out.println(film.getGenre() + ", " + film.getCountry() + ", " + film.getYear());
        if (film.getRating() != null) {
            System.out.println(film.getRating().getRating() + " : " + film.getRating().getCount());
        }
        System.out.println(film.getLink());
        System.out.println("--------------------");
    }
}
