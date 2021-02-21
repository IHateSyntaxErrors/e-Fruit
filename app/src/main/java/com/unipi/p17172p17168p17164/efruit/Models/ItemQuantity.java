package com.unipi.p17172p17168p17164.efruit.Models;

public class ItemQuantity {
    String shop1;

    private ItemQuantity() {} // Empty constructor for Firebase.

    public ItemQuantity(String shop1) {
        this.shop1 = shop1;
    }

    public String getShop1() {
        return shop1;
    }

    public void setShop1(String shop1) {
        this.shop1 = shop1;
    }
}
