package org.parser.ratingStrategies;

import org.parser.models.Film;

public interface RatingStrategy {
    float getRating(Film film);
}
