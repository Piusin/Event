package com.example.piusin.event.ManyMapsDataProvidersPackage;

public class CartItemDataProvider {
    private String productName, productDes, storeName, productCount;

    public CartItemDataProvider(String productName, String productDes, String storeName, String productCount) {
        this.productName = productName;
        this.productDes = productDes;
        this.storeName = storeName;
        this.productCount = productCount;
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

    public String getProductCount() {
        return productCount;
    }
}
