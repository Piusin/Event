package com.example.piusin.event;

/**
 * Created by Piusin on 1/29/2018.
 */

public class ProductDataProvider {
    private String categoryName;
    private String productDescription;
    private String storeName;
    private String productImage;
    private String productName;
    private double productPrice;

    public ProductDataProvider(String categoryName, String productDescription, String storeName, String productImage, String productName, double productPrice) {
        this.categoryName = categoryName;
        this.productDescription = productDescription;
        this.storeName = storeName;
        this.productImage = productImage;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }
}
