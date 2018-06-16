package com.example.piusin.event;

/**
 * Created by Piusin on 3/11/2018.
 */

public class CartDataProvider {
    private String productName;
    private String productDes;

    public CartDataProvider(String productName, String productDes) {
        this.productName = productName;
        this.productDes = productDes;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDes() {
        return productDes;
    }
}
