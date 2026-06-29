package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Seller implements Serializable {
    @SerializedName("user_id")
    private int id;

    @SerializedName("username")
    private String name;

    @SerializedName("foto_profil")
    private String fotoProfil;

    @SerializedName("followers_count")
    private int followersCount;

    // [TAMBAHAN BARU] Menangkap status follow dari backend
    @SerializedName("is_following")
    private boolean isFollowing;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getFotoProfil() { return fotoProfil; }
    public int getFollowersCount() { return followersCount; }

    // Getter & Setter untuk status follow
    public boolean isFollowing() { return isFollowing; }
    public void setFollowing(boolean following) { this.isFollowing = following; }

    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
}
