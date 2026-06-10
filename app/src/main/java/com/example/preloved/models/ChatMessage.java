package com.example.preloved.models;

public class ChatMessage {
    private int senderId; // ID orang yang mengirim pesan
    private String message;
    private String time;

    public ChatMessage(int senderId, String message, String time) {
        this.senderId = senderId;
        this.message = message;
        this.time = time;
    }

    public int getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
}
