package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HomeResponse {

    @SerializedName("trending")
    private List<Product> trending;

    @SerializedName("recommended")
    private List<Product> recommended;

    // Kamu bisa tambahkan banner atau kategori di sini nanti jika butuh


    public List<Product> getTrending() {
        return trending;
    }

    public List<Product> getRecommended() {
        return recommended;
    }
}
