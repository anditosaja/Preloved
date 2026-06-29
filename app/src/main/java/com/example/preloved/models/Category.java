package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName(value="id", alternate={"category_id"})
    private int category_id;

    @SerializedName("nama_kategori")
    private String nama_kategori;

    @SerializedName("icon_kategori")
    private String icon_kategori;

    public int getCategory_id() {
        return category_id;
    }

    public String getNama_kategori() {
        return nama_kategori;
    }

    public String getIcon_kategori() {
        return icon_kategori;
    }
}
