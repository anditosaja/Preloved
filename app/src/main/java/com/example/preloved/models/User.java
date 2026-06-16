package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user_id")
    private int user_id;

    @SerializedName("nama_lengkap")
    private String nama_lengkap;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String username;

    @SerializedName("foto_profil")
    private String foto_profil;

    @SerializedName("balance")
    private double balance;

    // [TAMBAHAN BARU] Menangkap jumlah follower dari Laravel
    @SerializedName("followers_count")
    private int followersCount;

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

    // Getter untuk Follower
    public int getFollowersCount() {
        return followersCount;
    }

    // Setter (Opsional, buat jaga-jaga kalau mau diubah lokal)
    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }
}
