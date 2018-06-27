package com.example.piusin.event.ManyMapsDataProvidersPackage;

public class CartOptimizationDataProvider {
    private String productName, productDes, storeName;
    private double productCost, transportCost, totalCost, ostoreLatitude, ostoreLongitude, distance;

    public CartOptimizationDataProvider(String productName, String productDes, String storeName, double productCost, double transportCost, double totalCost, double ostoreLatitude, double ostoreLongitude, double distance) {
        this.productName = productName;
        this.productDes = productDes;
        this.storeName = storeName;
        this.productCost = productCost;
        this.transportCost = transportCost;
        this.totalCost = totalCost;
        this.ostoreLatitude = ostoreLatitude;
        this.ostoreLongitude = ostoreLongitude;
        this.distance = distance;
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

    public double getProductCost() {
        return productCost;
    }

    public double getTransportCost() {
        return transportCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getOstoreLatitude() {
        return ostoreLatitude;
    }

    public double getOstoreLongitude() {
        return ostoreLongitude;
    }

    public double getDistance() {
        return distance;
    }
}
