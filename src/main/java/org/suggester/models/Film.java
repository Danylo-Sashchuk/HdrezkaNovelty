package org.suggester.models;

import java.net.URL;

public class Film implements Comparable<Film> {
    private final URL image;
    private final String title;
    private final String originalTitle;
    private final int year;
    private final String country;
    private final String genre;
    private final URL link;
    private final Rating rating;

    public Film(URL image, String title, String originalTitle, int year, String country, String genre, URL link, Rating rating) {
        this.image = image;
        this.title = title;
        this.originalTitle = originalTitle;
        this.year = year;
        this.country = country;
        this.genre = genre;
        this.link = link;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Film{" +
               "image=" + image +
               ", title='" + title + '\'' +
               ", originalTitle='" + originalTitle + '\'' +
               ", year=" + year +
               ", country='" + country + '\'' +
               ", genre='" + genre + '\'' +
               ", link=" + link + '\'' +
               ", rating=" + rating + '\'' +
               '}';
    }

    @Override
    public int compareTo(Film o) {
        if (o.rating == null) {
            return -1;
        }
        if (this.rating == null) {
            return 1;
        }

        return Float.compare(o.rating.getGrade(), this.rating.getGrade());
    }
}
