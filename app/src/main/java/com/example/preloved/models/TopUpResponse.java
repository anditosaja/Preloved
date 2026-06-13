package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;

public class TopUpResponse {
    private boolean success;
    private String message;
    private Data data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("new_balance")
        private int newBalance;

        public int getNewBalance() { return newBalance; }
    }
}
