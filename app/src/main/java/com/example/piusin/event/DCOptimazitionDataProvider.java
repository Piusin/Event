package com.example.piusin.event;

public class DCOptimazitionDataProvider {
    public double dcTotalCost, dcProductCosts, dcTransportCost;
    public String dcStoreName;
    public double dcStoreLatitude, dcStoreLongitude;

    public DCOptimazitionDataProvider(double dcTotalCost, double dcProductCosts, double dcTransportCost, String dcStoreName, double dcStoreLatitude, double dcStoreLongitude) {
        this.dcTotalCost = dcTotalCost;
        this.dcProductCosts = dcProductCosts;
        this.dcTransportCost = dcTransportCost;
        this.dcStoreName = dcStoreName;
        this.dcStoreLatitude = dcStoreLatitude;
        this.dcStoreLongitude = dcStoreLongitude;
    }

    public double getDcTotalCost() {
        return dcTotalCost;
    }

    public double getDcProductCosts() {
        return dcProductCosts;
    }

    public double getDcTransportCost() {
        return dcTransportCost;
    }

    public String getDcStoreName() {
        return dcStoreName;
    }

    public double getDcStoreLatitude() {
        return dcStoreLatitude;
    }

    public double getDcStoreLongitude() {
        return dcStoreLongitude;
    }
}
