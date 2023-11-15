package org.parser.models;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.parser.util.TestFilmCreator;

import java.util.List;

public abstract class AbstractParserTest {
    public final Parser parser;

    public AbstractParserTest(Parser parser) {
        this.parser = parser;
    }

    @Test
    public void parse_WithDefaultConfig() {
        List<Film> films = parser.parse();
        Assertions.assertThat(films).isEqualTo(TestFilmCreator.defaultFilmOutput);
    }
}