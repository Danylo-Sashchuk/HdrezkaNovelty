package org.parser.models;

public record Rating(float rating, int count) {

    @Override
    public String toString() {
        return "Rating{" + "rating=" + rating + ", count=" + count + "}";
    }
}
