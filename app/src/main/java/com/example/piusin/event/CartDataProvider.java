package com.example.piusin.event;

/**
 * Created by Piusin on 3/11/2018.
 */

public class CartDataProvider {
    private String productName;
    private String productDes;
    private String storeName;

    public CartDataProvider(String productName, String productDes, String storeName) {
        this.productName = productName;
        this.productDes = productDes;
        this.storeName = storeName;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDes() {
        return productDes;
    }

    public String getStoreName() {
        return storeName;
    }
}
