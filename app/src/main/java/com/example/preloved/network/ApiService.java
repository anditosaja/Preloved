package com.example.preloved.network;
import com.example.preloved.models.HomeResponse;
import com.example.preloved.models.ImageResponse;
import com.example.preloved.models.LoginRequest;
import com.example.preloved.models.LoginResponse;
import com.example.preloved.models.Product;
import com.example.preloved.models.ProductRequest;
import com.example.preloved.models.ProductResponse;
import com.example.preloved.models.RegisterRequest;
import com.example.preloved.models.RegisterResponse;
import com.example.preloved.models.TopUpRequest;
import com.example.preloved.models.TopUpResponse;
import com.example.preloved.models.UserChatResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.GET;

import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(
        @Body LoginRequest request
    );

    @GET("products")
    Call<List<Product>> getProducts();


    @GET("products")
    Call<List<Product>> getProductsByCategory(
        @Query("category_id") int categoryId
    );

    @GET("users-chat")
    Call<List<UserChatResponse>> getGlobalUsers(
        @Header("Authorization") String token
    );

    @GET("chats/{userId}")
    Call<ResponseBody> getChatDetail(
        @Header("Authorization") String token,
        @Path("userId") int userId
    );

    @FormUrlEncoded
    @POST("chats")
    Call<ResponseBody> kirimPesan(
        @Header("Authorization") String token,
        @Field("product_id") int productId,
        @Field("receiver_id") int receiverId,
        @Field("message") String message
    );

    @POST("products")
    Call<ProductResponse> createProduct(
        @Header("Authorization") String token,
        @Body ProductRequest request
    );

    @Multipart
    @POST("products/{id}/images")
    Call<ImageResponse> uploadProductImage(
        @Header("Authorization") String token,
        @Path("id") int productId,
        @Part MultipartBody.Part image
    );

    // --- Ubah tipe datanya menjadi HomeResponse agar tidak "Incompatible types" ---
    @GET("home")
    Call<HomeResponse> getTrendingProducts();
    @Multipart
    @POST("profile/photo")
    Call<ResponseBody> uploadFotoProfil(
        @Header("Authorization") String token,
        @Part MultipartBody.Part photo
    );

    @POST("register")
    Call<RegisterResponse> register(
        @Body RegisterRequest request
    );

    @GET("my-products")
    Call<List<Product>> getMyProducts(@Header("Authorization") String token);

    @POST("topup")
    Call<TopUpResponse> submitTopUp(
        @Header("Authorization") String token,
        @Body TopUpRequest request
    );
}
