package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    // Kita sediakan backup "id" jika Laravel me-return dengan key standar resource
    @SerializedName(value = "product_id", alternate = {"id"})
    private int productId;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("seller_id")
    private int sellerId;

    @SerializedName("nama_barang")
    private String nama_barang;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("harga_jual")
    private String harga_jual;

    @SerializedName("harga_asli")
    private String harga_asli;

    @SerializedName("kondisi")
    private String kondisi;

    @SerializedName("merek")
    private String merek;

    @SerializedName("warna")
    private String warna;

    // [FIX UTAMA]: Diubah ke "seller" agar sinkron dengan relasi model Laravel lu bray!
    @SerializedName("seller")
    private Seller seller;

    @SerializedName("lokasi_kota")
    private String lokasi_kota;

    @SerializedName("status_barang")
    private String status_barang;

    @SerializedName("images")
    private List<ProductImage> images;

    // Getter methods
    public int getProductId() {
        return productId;
    }

    // Backup method untuk activity lama lu jika ada yang terlanjur memanggil getId()
    public int getId() {
        return productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getHarga_jual() {
        return harga_jual;
    }

    public String getHarga_asli() {
        return harga_asli;
    }

    public String getKondisi() {
        return kondisi;
    }

    public String getMerek() {
        return merek;
    }

    public String getWarna() {
        return warna;
    }

    public String getLokasi_kota() {
        return lokasi_kota;
    }

    public Seller getSeller() {
        return seller;
    }

    public String getStatus_barang() {
        return status_barang;
    }

    public List<ProductImage> getImages() {
        return images;
    }
}
