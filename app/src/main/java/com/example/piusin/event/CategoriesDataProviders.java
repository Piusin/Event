package com.example.piusin.event;

/**
 * Created by Piusin on 2/22/2018.
 */
//for category class fragment data fetched from server

public class CategoriesDataProviders {
    private String categoryName;
    private String categoryImage;
    private String storeName;

    public CategoriesDataProviders(String categoryName, String categoryImage, String storeName) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.storeName = storeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public String getStoreName() {
        return storeName;
    }
}
