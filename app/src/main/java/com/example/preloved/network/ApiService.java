package com.example.preloved.network;
import com.example.preloved.models.LoginRequest;
import com.example.preloved.models.LoginResponse;
import com.example.preloved.models.Product;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(
            @Body LoginRequest request
    );

    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products")
    Call<List<Product>> getTrendingProducts();

    @GET("products")
    Call<List<Product>> getProductsByCategory(@Query("category_id") int categoryId);

    @GET("chats")
    Call<ResponseBody> getChatRooms();

    @GET("chats/detail")
    Call<ResponseBody> getChatDetail(@Query("chat_id") int chatId);
}
