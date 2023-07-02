package org.suggester.ratingStrategies;

import org.suggester.models.Film;

public interface RatingStrategy {
    float getRating(Film film);
}
