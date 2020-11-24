package org.jk.application.data.model.product;

public class Product {
    private int num;
    private String name;
    private double price;
    private double printPrice;
    private double printTime;

    public Product(int num, String name, double price, double printPrice, double printTime) {
        this.num = num;
        this.name = name;
        this.price = price;
        this.printPrice = printPrice;
        this.printTime = printTime;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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
