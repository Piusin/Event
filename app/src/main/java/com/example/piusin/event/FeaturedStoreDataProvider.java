package com.example.piusin.event;

/**
 * Created by Piusin on 1/21/2018.
 */
//for hand coded data

public class FeaturedStoreDataProvider { //class to store featured store attributes

    private int imageView;
    private String name;
    private String openTime;
    private String location;
    private double distance;

    public FeaturedStoreDataProvider(int imageView, String name, String openTime, String location, double distance) {
        this.imageView = imageView;
        this.name = name;
        this.openTime = openTime;
        this.location = location;
        this.distance = distance;
    }

    public int getImageView() {
        return imageView;
    }

    public String getName() {
        return name;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getLocation() {
        return location;
    }

    public double getDistance() {
        return distance;
    }

    public void setName(String name) {
        this.name = name;
    }

}
