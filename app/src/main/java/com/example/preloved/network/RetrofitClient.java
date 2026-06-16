package com.example.preloved.network;

import com.example.preloved.network.Config; // Otomatis import kelas Config

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                // Memanggil BASE_URL dari kelas Config dan ditambah "api/"
                .baseUrl(Config.BASE_URL + "api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }

        return retrofit;
    }
}
