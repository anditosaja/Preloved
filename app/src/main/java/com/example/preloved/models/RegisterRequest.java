package com.example.preloved.models;

public class RegisterRequest {
    private String nama_lengkap;
    private String username;
    private String email;
    private String password;

    public RegisterRequest(String nama_lengkap, String username, String email, String password) {
        this.nama_lengkap = nama_lengkap;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
