package com.unipi.p17172p17168p17164.efruit.Models;

public class ItemShops {
    String address;
    String coords;
    String phone;
    String region;
    String zip;

    private ItemShops() {} // Empty constructor for Firebase.

    private ItemShops(String address, String coords, String phone, String region, String zip) { // Constructor to read data from Firebase.
        this.address = address;
        this.coords = coords;
        this.phone = phone;
        this.region = region;
        this.zip = zip;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
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
}
