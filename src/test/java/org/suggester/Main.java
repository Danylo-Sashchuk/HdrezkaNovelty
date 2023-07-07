package org.suggester;

import org.junit.jupiter.api.Test;
import org.suggester.util.TestFilmCreator;

public class Main {
    public static void main(String[] args) {
        String input = TestFilmCreator.defaultFilmOutput.get(0).toString();
        System.out.println(input);
    }
}
