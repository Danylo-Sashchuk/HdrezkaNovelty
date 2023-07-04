package org.suggester.models;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FileSuggester fileSuggester = new FileSuggester.SuggesterBuilder().build();
        for (Film film : fileSuggester.parse()) {
            System.out.println(film);
        }
    }
}
