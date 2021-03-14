package org.jk.application.backend.model;


public class Item {
    private int id;
    private int projectId;
    private String name;
    private int quantity;
    private int demand;

    public Item(int id, int projectId, String name, int quantity, int demand) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.quantity = quantity;
        this.demand = demand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
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

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }
}
