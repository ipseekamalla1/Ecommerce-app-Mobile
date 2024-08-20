package com.example.softnestecommerce;

public class CartItem {
    private String productName;
    private int quantity;

    public CartItem() {
        // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
    }

    public CartItem(String productName, int quantity) {
        this.productName = productName;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
