package com.example.preloved.models;

public class TopUpRequest {
    private int amount;

    public TopUpRequest(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
