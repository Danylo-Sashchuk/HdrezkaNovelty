package org.parser.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.net.URL;

public class Film {
    private final URL image;
    private final String title;
    private final int year;
    private final String country;
    private final String genre;
    private final URL link;
    private String originalTitle;
    private Rating rating;

    public Film(URL image, String title, String originalTitle, int year, String country, String genre, URL link,
                Rating rating) {
        this.image = image;
        this.title = title;
        this.originalTitle = originalTitle;
        this.year = year;
        this.country = country;
        this.genre = genre;
        this.link = link;
        this.rating = rating;
    }

    public Film(URL image, String title, int year, String country, String genre, URL link) {
        this.image = image;
        this.title = title;
        this.year = year;
        this.country = country;
        this.genre = genre;
        this.link = link;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
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

    public URL getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public int getYear() {
        return year;
    }

    public String getCountry() {
        return country;
    }

    public String getGenre() {
        return genre;
    }

    public URL getLink() {
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Film film = (Film) o;

        return new EqualsBuilder().append(year, film.year)
                .append(image, film.image)
                .append(title, film.title)
                .append(originalTitle, film.originalTitle)
                .append(country, film.country)
                .append(genre, film.genre)
                .append(link, film.link)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(image)
                .append(title)
                .append(originalTitle)
                .append(year)
                .append(country)
                .append(genre)
                .append(link)
                .toHashCode();
    }
}
