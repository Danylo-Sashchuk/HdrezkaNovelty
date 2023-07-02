package org.suggester.models;

public class Rating {
    private final float rating;
    private final int count;
    private final float grade;
    private static final float ratingWeight = 0.3f;
    private static final float countWeight = 0.7f;

    public Rating(float rating, int count) {
        this.rating = rating;
        this.count = count;
        grade = rating * ratingWeight + count * countWeight;
     }

    public float getRating() {
        return rating;
    }

    public int getCount() {
        return count;
    }

    public float getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return "Rating{" + "rating=" + rating + ", count=" + count + ", grade=" + grade + "}";
    }
}
