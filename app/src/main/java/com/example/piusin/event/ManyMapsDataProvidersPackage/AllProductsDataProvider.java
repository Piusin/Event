package com.example.piusin.event.ManyMapsDataProvidersPackage;

public class AllProductsDataProvider {
    private String productName, storeName;
    private double singlePCost, transportCost, totalCost, latitude, longitude;

    public AllProductsDataProvider(String productName, String storeName, double singlePCost, double transportCost, double totalCost, double latitude, double longitude) {
        this.productName = productName;
        this.storeName = storeName;
        this.singlePCost = singlePCost;
        this.transportCost = transportCost;
        this.totalCost = totalCost;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getProductName() {
        return productName;
    }

    public String getStoreName() {
        return storeName;
    }

    public double getSinglePCost() {
        return singlePCost;
    }

    public double getTransportCost() {
        return transportCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}