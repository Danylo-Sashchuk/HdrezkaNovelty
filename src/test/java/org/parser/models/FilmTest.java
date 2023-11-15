package org.parser.models;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.parser.util.TestFilmCreator;

import java.net.MalformedURLException;
import java.net.URL;

public class FilmTest {
    static private final Film film1 = TestFilmCreator.defaultFilmOutput.get(0);
    static private final Film film2 = TestFilmCreator.defaultFilmOutput.get(1);
    static private final Film film3 = TestFilmCreator.defaultFilmOutput.get(2);


    @Test
    public void testToString() {
        Assertions.assertThat(film1.toString())
                .isEqualTo("Film{image=https://static.hdrezka.ac/i/2023/6/30/o279ba432def0ps78v52k.jpg, title='Italian Best Shorts 6: Дом, милый дом', originalTitle='Italian Best Shorts 6', year=2023, country='Италия', genre='Драмы', link=file:src/test/resources/3/film24.html', rating=null'}");
    }

    @Test
    void testHashCode() throws MalformedURLException {
        URL image = new URL(film1.getImage().toString());
        String title = film1.getTitle();
        String originalTitle = film1.getOriginalTitle();
        int year = film1.getYear();
        String country = film1.getCountry();
        String genre = film1.getGenre();
        URL link = new URL(film1.getLink().toString());

        Film equalFilm = new Film(image, title, originalTitle, year, country, genre, link, null);
        Assertions.assertThat(film1.hashCode()).isEqualTo(equalFilm.hashCode());
    }
}