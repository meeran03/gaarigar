package com.gianteyes.gaarigarapp.models;

public class PopularServicesModel {

    int image, price;
    String name;
    float rating;

    public PopularServicesModel(final int image, final int price, final String name, final float rating) {
        this.image = image;
        this.price = price;
        this.name = name;
        this.rating = rating;
    }

    public int getImage() {
        return this.image;
    }

    public void setImage(final int image) {
        this.image = image;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public float getRating() {
        return this.rating;
    }

    public void setRating(final float rating) {
        this.rating = rating;
    }
}
