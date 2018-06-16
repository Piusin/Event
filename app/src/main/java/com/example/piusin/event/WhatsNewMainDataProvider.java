package com.example.piusin.event;

/**
 * Created by Piusin on 3/29/2018.
 */

//for data from server using whatsnewmainadapter
public class WhatsNewMainDataProvider {

    private String categoryName;
    private String productDescription;
    private String storeName;
    private String storeLocation;
    private String productImage;
    private String productName;
    private double productPrice;

    public WhatsNewMainDataProvider(String categoryName, String productDescription, String storeName, String storeLocation, String productImage, String productName, double productPrice) {
        this.categoryName = categoryName;
        this.productDescription = productDescription;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
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

    public String getStoreLocation() {
        return storeLocation;
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
