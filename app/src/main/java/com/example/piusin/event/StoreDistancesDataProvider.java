package com.example.piusin.event;

public class StoreDistancesDataProvider {
    private String storeName;
    private double storeDistance;

    public StoreDistancesDataProvider(String storeName, double storeDistance) {
        this.storeName = storeName;
        this.storeDistance = storeDistance;
    }

    public String getStoreName() {
        return storeName;
    }

    public double getStoreDistance() {
        return storeDistance;
    }

    public void setStoreDistance(double storeDistance) {
        this.storeDistance = storeDistance;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
