package com.example.piusin.event;

public class AlgorithmProductDataProvider {
    public String productName, storeName;
    public Double productCost, storeLatitude, storeLongitude;
    public int quantityAtHand;

    public AlgorithmProductDataProvider(String productName, String storeName, Double productCost, Double storeLatitude, Double storeLongitude, int quantityAtHand) {
        this.productName = productName;
        this.storeName = storeName;
        this.productCost = productCost;
        this.storeLatitude = storeLatitude;
        this.storeLongitude = storeLongitude;
        this.quantityAtHand = quantityAtHand;
    }

    public String getProductName() {
        return productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public Double getProductCost() {
        return productCost;
    }

    public Double getStoreLatitude() {
        return storeLatitude;
    }

    public Double getStoreLongitude() {
        return storeLongitude;
    }

    public int getQuantityAtHand() {
        return quantityAtHand;
    }
}
