package org.jk.application.data.model.order;

public class Delivery {
    private String methodName;
    private String phone;
    private String street;
    private String zipcode;
    private String city;
    private String cost;

    public Delivery(String methodName, String phone, String street, String zipcode, String city, String cost) {
        this.methodName = methodName;
        this.phone = phone;
        this.street = street;
        this.zipcode = zipcode;
        this.city = city;
        this.cost = cost;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
