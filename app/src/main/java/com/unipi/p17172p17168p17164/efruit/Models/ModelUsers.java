package com.unipi.p17172p17168p17164.efruit.Models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ModelUsers {
    String full_name;
    String tokenId;
    String address;
    String phone_number;

    public ModelUsers() {}

    public ModelUsers(String full_name, String tokenId, String address, String phone_number) {
        this.full_name = full_name;
        this.tokenId = tokenId;
        this.address = address;
        this.phone_number = phone_number;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
