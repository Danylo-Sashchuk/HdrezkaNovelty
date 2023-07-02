package org.suggester.models;

import java.net.URL;

public class Film {
    private URL image;
    private String title;
    private int year;
    private String country;
    private String genre;
    private URL link;

    @Override
    public String toString() {
        return "Film{" +
               "image=" + image +
               ", title='" + title + '\'' +
               ", year=" + year +
               ", country='" + country + '\'' +
               ", genre='" + genre + '\'' +
               ", link=" + link +
               '}';
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }
}
