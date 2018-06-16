package com.example.piusin.event;

/**
 * Created by Piusin on 3/19/2018.
 */

public class DiscountMainDataProvider {
    private String discountName;
    private int dicountImage;

    public DiscountMainDataProvider(String discountName, int dicountImage) {
        this.discountName = discountName;
        this.dicountImage = dicountImage;
    }

    public String getDiscountName() {
        return discountName;
    }

    public int getDicountImage() {
        return dicountImage;
    }
}
