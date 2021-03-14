package org.jk.application.backend.model.order;

import java.util.List;

public class Order {
    private String id;
    private String date;
    private List<Product> products;

    public Order(String id, String date, List<Product> products) {
        this.id = id;
        this.date = date;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
