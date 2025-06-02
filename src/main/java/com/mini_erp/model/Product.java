package com.mini_erp.model;

public class Product {
    private final int prodId;
    private final int category;
    private final String title;
    private final String actor;
    private final double price;
    private final short special;
    private final int commonProdId;

    public Product(int prodId, int category, String title, String actor, double price, short special, int commonProdId) {
        this.prodId = prodId;
        this.category = category;
        this.title = title;
        this.actor = actor;
        this.price = price;
        this.special = special;
        this.commonProdId = commonProdId;
    }

    // Getters
    public int getProdId() {
        return prodId;
    }

    public int getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getActor() {
        return actor;
    }

    public double getPrice() {
        return price;
    }

    public short getSpecial() {
        return special;
    }

    public int getCommonProdId() {
        return commonProdId;
    }

    @Override
    public String toString() {
        return title;
    }
}
