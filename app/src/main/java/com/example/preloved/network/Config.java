package com.example.preloved.network;

public class Config {
    // Cukup ganti IP di baris ini SAJA kalau pindah Wi-Fi/Jaringan
    public static final String BASE_URL = "http://192.168.1.174:8000/";

    // Ini otomatis ngikutin BASE_URL di atas 
    public static final String IMAGE_URL = BASE_URL + "storage/";
}
