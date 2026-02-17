package com.example.lab3;

import java.util.UUID;

public class Order {
    public String id;
    public String flowerName;
    public String customerName;
    public String email;
    public String notes;
    public int colorId;
    public int priceId;
    public String resultText;

    public Order(String flower, String customer, String email, String notes, int colorId, int priceId, String resultText) {
        this.id = UUID.randomUUID().toString();
        this.flowerName = flower;
        this.customerName = customer;
        this.email = email;
        this.notes = notes;
        this.colorId = colorId;
        this.priceId = priceId;
        this.resultText = resultText;
    }
}