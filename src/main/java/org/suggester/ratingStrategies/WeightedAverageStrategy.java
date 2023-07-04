package org.suggester.ratingStrategies;

import org.suggester.models.Film;

public class WeightedAverageStrategy implements RatingStrategy {
    private final float ratingWeight;
    private final float countWeight;

    public WeightedAverageStrategy(float ratingWeight, float countWeight) {
        this.ratingWeight = ratingWeight;
        this.countWeight = countWeight;
    }

    @Override
    public float getRating(Film film) {
        return film.getRating().getRating() * ratingWeight + film.getRating().getCount() * countWeight;
    }
}
