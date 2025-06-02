package com.mini_erp.model;

import java.time.LocalDate;
import java.util.List;

public class Order {
    private int id;
    private int customerId;
    private LocalDate orderDate;
    private double netAmount;
    private double tax;
    private double totalAmount;
    private List<OrderLine> lines;

    public Order(int id, int customerId, LocalDate orderDate, double netAmount, double tax, double totalAmount, List<OrderLine> lines) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.netAmount = netAmount;
        this.tax = tax;
        this.totalAmount = totalAmount;
        this.lines = lines;
    }

    // Constructeur utilisé lors de la création d’une nouvelle commande
    public Order(int customerId, List<OrderLine> lines) {
        this(-1, customerId, LocalDate.now(), 0, 0, 0, lines);
    }

    // Getters & setters

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderLine> getLines() {
        return lines;
    }
}
