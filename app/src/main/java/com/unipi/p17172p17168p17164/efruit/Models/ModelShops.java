package com.unipi.p17172p17168p17164.efruit.Models;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class ModelShops {
    String name;
    String address;
    GeoPoint coords;
    String phone;
    String region;
    String zip;
    HashMap<Object, ModelProducts> products;

    private ModelShops() {} // Empty constructor for Firebase.

    private ModelShops(String address, GeoPoint coords, String phone, String region, String zip, String name) { // Constructor to read data from Firebase.
        this.address = address;
        this.coords = coords;
        this.phone = phone;
        this.region = region;
        this.zip = zip;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getCoords() {
        return coords;
    }

    public void setCoords(GeoPoint coords) {
        this.coords = coords;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
