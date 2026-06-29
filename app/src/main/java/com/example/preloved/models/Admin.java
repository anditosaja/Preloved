package com.example.preloved.models;

public class Admin {

    private int admin_id;
    private String nama_lengkap;
    private String username;
    private String email;
    private String level;
    private boolean is_active;

    public int getAdmin_id() {
        return admin_id;
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

    public String getLevel() {
        return level;
    }

    public boolean isIs_active() {
        return is_active;
    }
}
