package org.suggester;

import org.suggester.models.Film;
import org.suggester.models.Suggester;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Suggester suggester = new Suggester();
        for (Film film : suggester.parse()) {
            System.out.println(film);
        }
    }
}
