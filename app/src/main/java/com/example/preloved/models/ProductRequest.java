package com.example.preloved.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ProductRequest implements Serializable {

    @SerializedName("category_id")
    private int category_id;

    @SerializedName("nama_barang")
    private String nama_barang;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("harga_jual")
    private double harga_jual;

    @SerializedName("harga_asli")
    private Double harga_asli;

    @SerializedName("kondisi")
    private String kondisi;

    @SerializedName("merek")
    private String merek;

    @SerializedName("warna")
    private String warna;

    @SerializedName("lokasi_kota")
    private String lokasi_kota;

    // Constructor kosong
    public ProductRequest() {}

    // Setters
    public void setNamaBarang(String nama) { this.nama_barang = nama; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setHargaJual(double harga) { this.harga_jual = harga; }
    public void setHargaAsli(Double harga) { this.harga_asli = harga; }
    public void setMerek(String merek) { this.merek = merek; }
    public void setWarna(String warna) { this.warna = warna; }
    public void setLokasiKota(String lokasi) { this.lokasi_kota = lokasi; }
    public void setCategoryId(int categoryId) { this.category_id = categoryId; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }
}
