package org.suggester.models;

public class Rating {
    private final float rating;
    private final int count;

    public Rating(float rating, int count) {
        this.rating = rating;
        this.count = count;
    }

    public float getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Rating{" + "rating=" + rating + ", count=" + count + "}";
    }
}
