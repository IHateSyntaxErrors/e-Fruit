package com.unipi.p17172p17168p17164.efruit.Models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.sql.Timestamp;
import java.util.Map;

@IgnoreExtraProperties
public class ModelCart {
    Map<String, String> created_at;
    String shopId;
    String userId;

    int amount;
    String imgUrl;
    String name;
    double price;
    DocumentReference quantity;
    String productId;

    private ModelCart() {} // Empty constructor for Firebase.

    public ModelCart(Map<String, String> created_at, String shopId, String userId,
                     int amount, String imgUrl, String name, double price,
                     DocumentReference quantity, String productId) {
        this.created_at = created_at;
        this.shopId = shopId;
        this.userId = userId;
        this.amount = amount;
        this.imgUrl = imgUrl;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.productId = productId;
    }

    public Map<String, String> getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Map<String, String> created_at) {
        this.created_at = created_at;
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

    public DocumentReference getQuantity() {
        return quantity;
    }

    public void setQuantity(DocumentReference quantity) {
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
