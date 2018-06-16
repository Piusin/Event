package com.example.piusin.event;

/**
 * Created by Piusin on 1/23/2018.
 */

//for homeFragment Category group
public class CategoriesDataProvider {

    private String categoryName;
    private int categoryImage;

    public CategoriesDataProvider(String categoryName, int categoryImage) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategoryImage() {
        return categoryImage;
    }

}
