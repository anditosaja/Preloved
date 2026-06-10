package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    @SerializedName("product_id")
    private int productId;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("seller_id")
    private int sellerId;

    @SerializedName("nama_barang")
    private String nama_barang;

    @SerializedName("deskripsi")
    private String deskripsi;

    // Di adapter kamu memanggil Double.parseDouble(product.getHarga_jual()),
    // sehingga tipe datanya dideklarasikan sebagai String.
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

    @SerializedName("lokasi_kota")
    private String lokasi_kota;

    @SerializedName("status_barang")
    private String status_barang;

    // Relasi tabel gambar dari Laravel
    @SerializedName("images")
    private List<ProductImage> images;

    // Getter methods
    public int getProductId() { return productId; }
    public int getCategoryId() { return categoryId; }
    public int getSellerId() { return sellerId; }
    public String getNama_barang() { return nama_barang; }
    public String getDeskripsi() { return deskripsi; }
    public String getHarga_jual() { return harga_jual; }
    public String getHarga_asli() { return harga_asli; }
    public String getKondisi() { return kondisi; }
    public String getMerek() { return merek; }
    public String getWarna() { return warna; }
    public String getLokasi_kota() { return lokasi_kota; }
    public String getStatus_barang() { return status_barang; }
    public List<ProductImage> getImages() { return images; }
}
