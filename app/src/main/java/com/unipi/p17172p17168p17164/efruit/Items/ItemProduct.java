package com.unipi.p17172p17168p17164.efruit.Items;

import android.widget.ImageView;

public class ItemProduct {
    int id;
    ImageView productIcon;
    String productName;
    String productPrice;
    String productQuantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ImageView getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(ImageView productIcon) {
        this.productIcon = productIcon;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }
}
