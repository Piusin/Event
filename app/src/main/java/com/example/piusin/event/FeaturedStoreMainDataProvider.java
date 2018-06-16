package com.example.piusin.event;

/**
 * Created by Piusin on 3/1/2018.
 */

//for data from Server
public class FeaturedStoreMainDataProvider {
    private String storeImage;
    private String storeName;
    private String storeOpenTime;
    private String storeLocation;
    private double storeDistance;
    private String storeCloseTime;

    public FeaturedStoreMainDataProvider(String storeImage, String storeName, String storeOpenTime, String storeLocation, double storeDistance, String storeCloseTime) {
        this.storeImage = storeImage;
        this.storeName = storeName;
        this.storeOpenTime = storeOpenTime;
        this.storeLocation = storeLocation;
        this.storeDistance = storeDistance;
        this.storeCloseTime = storeCloseTime;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreOpenTime() {
        return storeOpenTime;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public double getStoreDistance() {
        return storeDistance;
    }

    public String getStoreCloseTime() {
        return storeCloseTime;
    }
}
