package com.example.piusin.event;

/**
 * Created by Piusin on 2/19/2018.
 */

public class ProductDescDataProvider {

    private String productDesImage;
    private String productDesName;
    private double productDesCost;
    private String productDescription;
    private String productDesStoreName;

    public ProductDescDataProvider(String productDesImage, String productDesName, double productDesCost, String productDescription, String productDesStoreName) {
        this.productDesImage = productDesImage;
        this.productDesName = productDesName;
        this.productDesCost = productDesCost;
        this.productDescription = productDescription;
        this.productDesStoreName = productDesStoreName;
    }

    public String getProductDesImage() {
        return productDesImage;
    }

    public String getProductDesName() {
        return productDesName;
    }

    public double getProductDesCost() {
        return productDesCost;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getProductDesStoreName() {
        return productDesStoreName;
    }
}
