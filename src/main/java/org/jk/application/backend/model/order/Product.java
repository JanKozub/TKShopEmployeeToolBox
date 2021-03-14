package org.jk.application.backend.model.order;

public class Product {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private double printPrice;
    private double printTime;

    public Product(int id, String name, int quantity, double price, double printPrice, double printTime) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.printPrice = printPrice;
        this.printTime = printTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrintPrice() {
        return printPrice;
    }

    public void setPrintPrice(double printPrice) {
        this.printPrice = printPrice;
    }

    public double getPrintTime() {
        return printTime;
    }

    public void setPrintTime(double printTime) {
        this.printTime = printTime;
    }
}
