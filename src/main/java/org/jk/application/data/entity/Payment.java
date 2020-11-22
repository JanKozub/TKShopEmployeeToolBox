package org.jk.application.data.entity;

public class Payment {
    private String date;
    private String provider;
    private String amount;

    public Payment(String date, String provider, String amount) {
        this.date = date;
        this.provider = provider;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
