package com.example.preloved.models;

public class RegisterResponse {
    private String message;
    private String token;
    private User user;

    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; }
}
