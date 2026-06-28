package com.example.preloved.models;

public class User {

    private int user_id;
    private String nama_lengkap;
    private String username;
    private String email;
    private String foto_profil;
    private String status_akun;
    private String created_at;

    public int getUserId() {
        return user_id;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFoto_profil() {
        return foto_profil;
    }

    public String getStatus_akun() {
        return status_akun;
    }

    public String getCreated_at() {
        return created_at;
    }
}
