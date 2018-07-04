package com.example.piusin.event.ManyMapsDataProvidersPackage;

public class MapStoreDataProvider {

    private String productName, storeName;
    private double totalCost, transportCost, latitude, longitude;

    public MapStoreDataProvider(String productName, String storeName, double totalCost, double transportCost, double latitude, double longitude) {
        this.productName = productName;
        this.storeName = storeName;
        this.totalCost = totalCost;
        this.transportCost = transportCost;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getProductName() {
        return productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getTransportCost() {
        return transportCost;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
