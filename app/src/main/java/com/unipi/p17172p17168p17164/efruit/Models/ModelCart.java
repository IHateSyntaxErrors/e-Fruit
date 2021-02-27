package com.unipi.p17172p17168p17164.efruit.Models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ModelCart {
    String shopId;
    String userId;

    int amount;
    double price;
    String productId;

    private ModelCart() {} // Empty constructor for Firebase.

    public ModelCart(String shopId, String userId,
                     int amount, double price, String productId) {
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
}
