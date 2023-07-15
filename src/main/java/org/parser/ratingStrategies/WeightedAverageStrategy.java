package org.parser.ratingStrategies;

import org.parser.models.Film;

public class WeightedAverageStrategy implements RatingStrategy {
    private final float ratingWeight;
    private final float countWeight;

    public WeightedAverageStrategy(float ratingWeight, float countWeight) {
        this.ratingWeight = ratingWeight;
        this.countWeight = countWeight;
    }

    @Override
    public float getRating(Film film) {
        return film.getRating().rating() * ratingWeight + film.getRating().count() * countWeight;
    }
}
