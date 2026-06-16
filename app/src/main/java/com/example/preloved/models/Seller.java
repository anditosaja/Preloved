package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Seller implements Serializable {
    @SerializedName("user_id")
    private int id;

    @SerializedName("username")
    private String name;

    @SerializedName("foto_profil") // Sesuaikan dengan field foto profil di tabel users Laravel lu
    private String fotoProfil;

    @SerializedName("followers_count") // Asumsi lu punya kolom/appends ini di Laravel
    private int followersCount;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getFotoProfil() { return fotoProfil; }
    public int getFollowersCount() { return followersCount; }

    // Setter untuk nambah follower secara lokal di layar
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
}
