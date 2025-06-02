package com.mini_erp.model;

public class OrderLine {
    private int productId;
    private int quantity;

    public OrderLine(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters & setters
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
