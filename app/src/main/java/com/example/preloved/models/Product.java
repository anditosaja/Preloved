package com.example.preloved.models;

import java.util.List;

public class Product {

    private int product_id;
    private int seller_id;
    private int category_id;

    private String nama_barang;
    private String deskripsi;
    private String harga_jual;
    private String harga_asli;
    private String kondisi;
    private String merek;
    private String warna;
    private String lokasi_kota;
    private String status_barang;
    private String waktu_post;

    private User seller;
    private Category category;
    private List<ProductImage> images;

    public int getProduct_id() {
        return product_id;
    }

    public int getSeller_id() {
        return seller_id;
    }

    public int getCategory_id() {
        return category_id;
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

    public String getStatus_barang() {
        return status_barang;
    }

    public String getWaktu_post() {
        return waktu_post;
    }

    public User getSeller() {
        return seller;
    }

    public Category getCategory() {
        return category;
    }

    public List<ProductImage> getImages() {
        return images;
    }
}