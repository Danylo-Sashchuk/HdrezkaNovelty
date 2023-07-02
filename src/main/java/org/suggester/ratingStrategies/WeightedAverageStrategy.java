package org.suggester.ratingStrategies;

import org.suggester.models.Film;

public class WeightedAverageStrategy implements RatingStrategy {
    private static final float ratingWeight = 0.3f;
    private static final float countWeight = 0.7f;

    @Override
    public float getRating(Film film) {
        return film.getRating().getRating() * ratingWeight + film.getRating().getCount() * countWeight;
    }
}
