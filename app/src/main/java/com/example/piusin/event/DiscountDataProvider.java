package com.example.piusin.event;

/**
 * Created by Piusin on 2/10/2018.
 */
//for data fetched from server

public class DiscountDataProvider {
    private String discountName;
    private String discountImage;
    private double oldCost;
    private double newCost;
    private String storeName;
    private String discountDescription;
    private String categoryName;

    public DiscountDataProvider(String discountName, String discountImage, double oldCost, double newCost, String storeName, String discountDescription, String categoryName) {
        this.discountName = discountName;
        this.discountImage = discountImage;
        this.oldCost = oldCost;
        this.newCost = newCost;
        this.storeName = storeName;
        this.discountDescription = discountDescription;
        this.categoryName = categoryName;
    }

    public String getDiscountName() {
        return discountName;
    }

    public String getDiscountImage() {
        return discountImage;
    }

    public double getOldCost() {
        return oldCost;
    }

    public double getNewCost() {
        return newCost;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getDiscountDescription() {
        return discountDescription;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
