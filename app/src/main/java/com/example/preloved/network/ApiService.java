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
import com.example.preloved.models.Review;
import com.example.preloved.models.TopUpRequest;
import com.example.preloved.models.TopUpResponse;
import com.example.preloved.models.UserChatResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    // Ambil daftar barang yang dipesan user (Sebagai Pembeli)
    @Headers("Accept: application/json")
    @GET("my-orders")
    Call<ResponseBody> getMyOrders(
        @Header("Authorization") String token
    );

    // Ambil daftar barang yang laku (Sebagai Penjual)
    @Headers("Accept: application/json")
    @GET("my-sales")
    Call<ResponseBody> getMySales(
        @Header("Authorization") String token
    );


    // Endpoint untuk Penjual meng-ACC pesanan
    @Headers("Accept: application/json")
    @PUT("orders/{id}/accept")
    Call<ResponseBody> acceptOrder(
        @Header("Authorization") String token,
        @Path("id") int orderId
    );

    @PUT("orders/{id}/complete")
    Call<ResponseBody> completeOrder(
        @Header("Authorization") String token,
        @Path("id") int orderId
    );

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

    @Headers("Accept: application/json")
    @POST("follow/{sellerId}")
    Call<ResponseBody> followUser(
        @Header("Authorization") String token,
        @Path("sellerId") int sellerId
    );

    @Headers("Accept: application/json")
    @DELETE("follow/{sellerId}")
    Call<ResponseBody> unfollowUser(
        @Header("Authorization") String token,
        @Path("sellerId") int sellerId
    );

    // ========================================================
    // [BARU] ENDPOINT AMBIL DATA PROFIL USER YANG SEDANG LOGIN
    // ========================================================
    @GET("profile")
    Call<ResponseBody> getMyProfile(
        @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("reviews")
    Call<ResponseBody> submitReview(
        @Header("Authorization") String token,
        @Field("order_id") int orderId,
        @Field("rating") int rating,
        @Field("comment") String comment
    );

    // 2. Endpoint untuk mengambil List Review milik Seller
    // Endpoint untuk mengambil daftar ulasan sebuah toko
    @GET("reviews/seller/{sellerId}")
    Call<java.util.List<com.example.preloved.models.Review>> getSellerReviews(
        @Header("Authorization") String token, // [TAMBAHAN BARU]
        @Path("sellerId") int sellerId
    );
    @GET("products/search")
    Call<List<Product>> searchProducts(
        @Query("q") String keyword,
        @Query("category_id") Integer categoryId
    );

    // Endpoint untuk melakukan pembelian barang (orders)
    @POST("orders")
    Call<ResponseBody> prosesOrderBarang(
        @Header("Authorization") String token,
        @Body RequestBody body // Kita kirim raw JSON biar dibaca mulus sama Laravel
    );
}
