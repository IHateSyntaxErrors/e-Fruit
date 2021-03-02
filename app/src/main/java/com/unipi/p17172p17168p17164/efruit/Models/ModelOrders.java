package com.unipi.p17172p17168p17164.efruit.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class ModelOrders {
    Timestamp created;
    double grand_total;
    boolean is_completed;
    Timestamp pickup_timestamp;
    ArrayList<String> products;
    String shopId;
    String userId;

    private ModelOrders() {} // Empty constructor for Firebase.

    public ModelOrders(Timestamp created, double grand_total, boolean is_completed,
                       Timestamp pickup_timestamp, ArrayList<String> products,
                       String shopId, String userId) {
        this.created = created;
        this.grand_total = grand_total;
        this.is_completed = is_completed;
        this.pickup_timestamp = pickup_timestamp;
        this.products = products;
        this.shopId = shopId;
        this.userId = userId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public double getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(double grand_total) {
        this.grand_total = grand_total;
    }

    public boolean isIs_completed() {
        return is_completed;
    }

    public void setIs_completed(boolean is_completed) {
        this.is_completed = is_completed;
    }

    public Timestamp getPickup_timestamp() {
        return pickup_timestamp;
    }

    public void setPickup_timestamp(Timestamp pickup_timestamp) {
        this.pickup_timestamp = pickup_timestamp;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
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
}
