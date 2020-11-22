package org.jk.application.data.entity;

import java.util.List;

public class Order {
    private String Id;
    private String date;
    private Buyer buyer;
    private List<Item> items;
    private Payment payment;
    private Delivery delivery;

    public Order(String id, String date, Buyer buyer, List<Item> items, Payment payment, Delivery delivery) {
        Id = id;
        this.date = date;
        this.buyer = buyer;
        this.items = items;
        this.payment = payment;
        this.delivery = delivery;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("Order:" + '\n' +
                "Id=" + Id + '\n' +
                "Date=" + date + '\n' +
                "----------------------" + '\n' +
                "Buyer:" + '\n' +
                "name:" + buyer.getName() + '\n' +
                "email:" + buyer.getEmail() + '\n' +
                "phone:" + buyer.getPhone() + '\n' +
                "----------------------" + '\n' +
                "Items:" + '\n');

        for (Item item : items) {
            output.append("Id:").append(item.getId()).append('\n')
                    .append("Name:").append(item.getName()).append('\n')
                    .append("Quantity:").append(item.getQuantity()).append('\n')
                    .append("Price:").append(item.getPrice()).append('\n')
                    .append("----------------------").append('\n');
        }

        output.append("Payment:").append('\n')
                .append("provider:").append(payment.getProvider()).append('\n')
                .append("amount:").append(payment.getAmount()).append('\n')
                .append("date:").append(payment.getDate()).append('\n')
                .append("----------------------").append('\n')
                .append("Delivery:").append('\n')
                .append("methodName:").append(delivery.getMethodName()).append('\n')
                .append("phone:").append(delivery.getPhone()).append('\n')
                .append("street:").append(delivery.getStreet()).append('\n')
                .append("zipcode:").append(delivery.getZipcode()).append('\n')
                .append("city:").append(delivery.getCity()).append('\n')
                .append("cost:").append(delivery.getCost()).append('\n')
                .append("-------------------");
        return output.toString();
    }
}
