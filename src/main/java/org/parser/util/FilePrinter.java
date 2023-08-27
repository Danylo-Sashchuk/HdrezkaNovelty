package org.parser.util;

import org.parser.models.Film;

import java.io.PrintWriter;

public class FilePrinter {
    public static void print(Film film, PrintWriter writer) {
        writer.println(film.getTitle());
        writer.println(film.getGenre() + ", " + film.getCountry() + ", " + film.getYear());
        if (film.getRating() != null) {
            writer.println(film.getRating().rating() + " : " + film.getRating().count());
        }
        writer.println(film.getLink());
        writer.println("--------------------");
    }
}
