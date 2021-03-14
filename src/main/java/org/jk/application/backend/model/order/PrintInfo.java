package org.jk.application.backend.model.order;

public class PrintInfo {
    private Product product;
    private double printPrice;
    private double printTime;

    public PrintInfo(Product product, double printPrice, double printTime) {
        this.product = product;
        this.printPrice = printPrice;
        this.printTime = printTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
