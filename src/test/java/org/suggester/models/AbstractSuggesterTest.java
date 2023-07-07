package org.suggester.models;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.suggester.util.TestFilmCreator;

import java.util.List;

abstract class AbstractSuggesterTest {
    protected final Suggester suggester;

    public AbstractSuggesterTest(Suggester suggester) {
        this.suggester = suggester;
    }

    @Test
    void suggest_WithDefaultConfig() {
        List<Film> films = suggester.parse();
        Assertions.assertThat(films).isEqualTo(TestFilmCreator.defaultFilmOutput);
    }
}