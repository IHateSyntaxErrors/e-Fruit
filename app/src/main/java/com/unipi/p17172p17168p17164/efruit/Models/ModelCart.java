package com.unipi.p17172p17168p17164.efruit.Models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ModelCart {
    String shopId;
    String userId;


    int amount;
    double price;
    String name;
    String productId;

    private ModelCart() {} // Empty constructor for Firebase.

    public ModelCart(String shopId, String userId, String name,
                     int amount, double price, String productId) {
        this.name = name;
        this.shopId = shopId;
        this.userId = userId;
        this.amount = amount;
        this.price = price;
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
