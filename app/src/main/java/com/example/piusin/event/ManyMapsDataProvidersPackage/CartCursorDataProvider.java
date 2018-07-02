package com.example.piusin.event.ManyMapsDataProvidersPackage;

public class CartCursorDataProvider {
    private String productName, productDes, productCost, quantityAtHand,
                   storeName, productCount;
    private double storeLatitude, storeLongitude;

    public CartCursorDataProvider(String productName, String productDes, String productCost, String quantityAtHand, String storeName, String productCount, double storeLatitude, double storeLongitude) {
        this.productName = productName;
        this.productDes = productDes;
        this.productCost = productCost;
        this.quantityAtHand = quantityAtHand;
        this.storeName = storeName;
        this.productCount = productCount;
        this.storeLatitude = storeLatitude;
        this.storeLongitude = storeLongitude;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDes() {
        return productDes;
    }

    public String getProductCost() {
        return productCost;
    }

    public String getQuantityAtHand() {
        return quantityAtHand;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getProductCount() {
        return productCount;
    }

    public double getStoreLatitude() {
        return storeLatitude;
    }

    public double getStoreLongitude() {
        return storeLongitude;
    }
}
