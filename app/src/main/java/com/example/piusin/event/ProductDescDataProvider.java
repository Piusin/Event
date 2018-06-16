package com.example.piusin.event;

/**
 * Created by Piusin on 2/19/2018.
 */

public class ProductDescDataProvider {

    private String productDesImage;
    private String productDesName;
    private double productDesCost;
    private String productDescription;

    public ProductDescDataProvider(String productDesImage, String productDesName, double productDesCost, String productDescription) {
        this.productDesImage = productDesImage;
        this.productDesName = productDesName;
        this.productDesCost = productDesCost;
        this.productDescription = productDescription;
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
}
