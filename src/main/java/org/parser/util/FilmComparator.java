package org.parser.util;

import org.parser.models.Film;
import org.parser.ratingStrategies.RatingStrategy;

import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;

public class FilmComparator implements Comparator<Film> {
    private RatingStrategy ratingStrategy;
    private final HashMap<URL, Float> hashedGrades = new HashMap<>();

    public FilmComparator(RatingStrategy ratingStrategy) {
        this.ratingStrategy = ratingStrategy;
    }

    public void changeStrategy(RatingStrategy ratingStrategy) {
        this.ratingStrategy = ratingStrategy;
        hashedGrades.clear();
    }

    @Override
    public int compare(Film o1, Film o2) {
        if (o1.getRating() == null) {
            return 1;
        }
        if (o2.getRating() == null) {
            return -1;
        }
        if (!hashedGrades.containsKey(o1.getLink())) {
            hashedGrades.put(o1.getLink(), ratingStrategy.getRating(o1));
        }
        if (!hashedGrades.containsKey(o2.getLink())) {
            hashedGrades.put(o2.getLink(), ratingStrategy.getRating(o2));
        }
        return Float.compare(hashedGrades.get(o2.getLink()), hashedGrades.get(o1.getLink()));
    }
}
