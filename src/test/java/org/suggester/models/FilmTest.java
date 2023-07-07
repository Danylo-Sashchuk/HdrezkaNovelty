package org.suggester.models;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.suggester.models.Film;
import org.suggester.util.TestFilmCreator;

public class FilmTest {
    static private final Film film = TestFilmCreator.defaultFilmOutput.get(0);

    @Test
    public void testToString() {
        Assertions.assertThat(film.toString())
                .isEqualTo("Film{image=https://static.hdrezka.ac/i/2023/6/30/o279ba432def0ps78v52k.jpg, title='Italian Best Shorts 6: Дом, милый дом', originalTitle='Italian Best Shorts 6', year=2023, country='Италия', genre='Драмы', link=file:src/test/resources/3/film24.html', rating=null'}");
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }
}