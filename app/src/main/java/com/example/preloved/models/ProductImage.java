package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ProductImage implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("image_url")
    private String image_url;

    @SerializedName("is_primary")
    private int isPrimary;

    public int getIsPrimary() {
        return isPrimary;
    }

    public int getId() { return id; }
    public int getProductId() { return productId; }

    // Dipakai oleh ProductAdapter
    public String getImage_url() {
        return image_url;
    }

    // Dipakai oleh DaftarBarangActivity / MainActivity (Solusi Error kamu)
    public String getImage_path() {
        return image_url;
    }

}
