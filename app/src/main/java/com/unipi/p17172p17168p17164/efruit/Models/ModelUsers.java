package com.unipi.p17172p17168p17164.efruit.Models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ModelUsers {
    String full_name;
    String tokenId;
    String phone_number;
    GeoPoint location;
    boolean is_admin;

    public ModelUsers() {}

    public ModelUsers(String full_name, String tokenId, String phone_number, GeoPoint location, boolean is_admin) {
        this.full_name = full_name;
        this.tokenId = tokenId;
        this.phone_number = phone_number;
        this.location = location;
        this.is_admin = is_admin;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public boolean getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }
}
