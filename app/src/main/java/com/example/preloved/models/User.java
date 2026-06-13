package com.example.preloved.models;

public class User {
    private int user_id;
    private String nama_lengkap;
    private String email;
    private String username;
    private String foto_profil;

    private double balance;

    public int getUserId() {
        return user_id;
    }

    public String getNamaLengkap() {
        return nama_lengkap;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFoto_profil() {
        return foto_profil;
    }

    public double getBalance() {
        return balance;
    }
}
