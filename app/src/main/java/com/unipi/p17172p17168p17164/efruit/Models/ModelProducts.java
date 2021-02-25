package com.unipi.p17172p17168p17164.efruit.Models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class ModelProducts {
    String imgUrl;
    String name;
    double price;
    int quantity;
    String productId;
    DocumentReference shopId;

    private ModelProducts() {} // Empty constructor for Firebase.

    private ModelProducts(String imgUrl, String name, long price, int quantity, String productId,
                          DocumentReference shopId) { // Constructor to read data from Firebase.
        this.imgUrl = imgUrl;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.productId = productId;
        this.shopId = shopId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public DocumentReference getShopId() {
        return shopId;
    }

    public void setShopId(DocumentReference shopId) {
        this.shopId = shopId;
    }
}
