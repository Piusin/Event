package com.example.piusin.event.LoginsPackage;

/**
 * Created by Piusin on 4/10/2018.
 */

public class User {
    private String cust_name, cust_email, cust_phone, cust_county, url;

    public User(String cust_name, String cust_email, String cust_phone, String cust_county, String url) {
        this.cust_name = cust_name;
        this.cust_email = cust_email;
        this.cust_phone = cust_phone;
        this.cust_county = cust_county;
        this.url = url;
    }

    public String getCust_name() {
        return cust_name;
    }

    public String getCust_email() {
        return cust_email;
    }

    public String getCust_phone() {
        return cust_phone;
    }

    public String getCust_county() {
        return cust_county;
    }

    public String getUrl() {
        return url;
    }
}
