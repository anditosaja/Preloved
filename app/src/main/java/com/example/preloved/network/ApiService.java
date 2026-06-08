package com.example.preloved.network;
import com.example.preloved.models.LoginRequest;
import com.example.preloved.models.LoginResponse;
import com.example.preloved.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );

    @GET("products")
    Call<List<Product>> getProducts();
}