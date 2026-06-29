package com.example.preloved.network;

import com.example.preloved.models.AdminDashboardSummary;
import com.example.preloved.models.AdminLoginResponse;
import com.example.preloved.models.ApiResponse;
import com.example.preloved.models.CategoryPopulerResponse;
import com.example.preloved.models.ChartItem;
import com.example.preloved.models.Complaint;
import com.example.preloved.models.HomeResponse;
import com.example.preloved.models.ImageResponse;
import com.example.preloved.models.LoginRequest;
import com.example.preloved.models.LoginResponse;
import com.example.preloved.models.Order;
import com.example.preloved.models.Product;
import com.example.preloved.models.ProductRequest;
import com.example.preloved.models.ProductResponse;
import com.example.preloved.models.RegisterRequest;
import com.example.preloved.models.RegisterResponse;
import com.example.preloved.models.Review;
import com.example.preloved.models.Category;
import com.example.preloved.models.TopUpRequest;
import com.example.preloved.models.TopUpResponse;
import com.example.preloved.models.User;
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
    Call<List<Product>>getProducts();

    @GET("categories")
    Call<List<Category>> getCategories();

    // Ambil daftar barang yang dipesan user (Sebagai Pembeli)
    @Headers("Accept: application/json")
    @GET("orders/purchases") //
    Call<ResponseBody> getMyOrders(
        @Header("Authorization") String token
    );

    // Ambil daftar barang yang laku (Sebagai Penjual)
    @Headers("Accept: application/json")
    @GET("orders/sales") // <--- PASTIIN INI SAMA PERSIS
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

    @GET("admin/dashboard/chart")
    Call<ApiResponse<List<ChartItem>>> getAdminChartData(
        @Header("Authorization") String bearerToken,
        @Query("type") String type
    );


    // =====================================
    // API KOMPLAIN ADMIN
    // =====================================

    @GET("admin/complaints")
    Call<ApiResponse<List<Complaint>>> getAdminComplaints(
        @Header("Authorization") String token,
        @Query("status") String status
    );

    @FormUrlEncoded
    @PUT("admin/complaints/{id}/status")
    Call<ApiResponse<Complaint>> updateComplaintStatus(
        @Header("Authorization") String token,
        @Path("id") int id,
        @Field("status") String status
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
        @Body RequestBody body
    );

    @GET("categories/populer")
    Call<CategoryPopulerResponse> getKategoriPopuler();

    // ===================== ADMIN: AUTH =====================

    @POST("admin/login")
    Call<AdminLoginResponse> adminLogin(@Body LoginRequest request);

    @POST("admin/logout")
    Call<ApiResponse<Object>> adminLogout(@Header("Authorization") String bearerToken);

    // ===================== ADMIN: DASHBOARD =====================

    @GET("admin/dashboard/summary")
    Call<ApiResponse<AdminDashboardSummary>> getAdminDashboardSummary(
        @Header("Authorization") String bearerToken
    );

    // ===================== ADMIN: KELOLA PENGGUNA =====================

    @GET("admin/users")
    Call<ApiResponse<List<User>>> getAdminUsers(
        @Header("Authorization") String bearerToken,
        @Query("status") String status,
        @Query("q") String keyword
    );

    @PUT("admin/users/{id}/block")
    Call<ApiResponse<User>> blockUser(
        @Header("Authorization") String bearerToken,
        @Path("id") int userId
    );

    @PUT("admin/users/{id}/unblock")
    Call<ApiResponse<User>> unblockUser(
        @Header("Authorization") String bearerToken,
        @Path("id") int userId
    );

    @DELETE("admin/users/{id}")
    Call<ApiResponse<Object>> deleteUser(
        @Header("Authorization") String bearerToken,
        @Path("id") int userId
    );

    // ===================== ADMIN: KELOLA PRODUK =====================

    @GET("admin/products")
    Call<ApiResponse<List<Product>>> getAdminProducts(
        @Header("Authorization") String bearerToken,
        @Query("status") String status,
        @Query("q") String keyword
    );

    @PUT("admin/products/{id}/approve")
    Call<ApiResponse<Product>> approveProduct(
        @Header("Authorization") String bearerToken,
        @Path("id") int productId
    );

    @FormUrlEncoded
    @PUT("admin/products/{id}/suspend")
    Call<ApiResponse<Product>> suspendProduct(
        @Header("Authorization") String bearerToken,
        @Path("id") int productId,
        @Field("catatan_admin") String catatanAdmin
    );

    @FormUrlEncoded
    @PUT("admin/products/{id}/reject")
    Call<ApiResponse<Product>> rejectProduct(
        @Header("Authorization") String bearerToken,
        @Path("id") int productId,
        @Field("catatan_admin") String catatanAdmin
    );

    @DELETE("admin/products/{id}")
    Call<ApiResponse<Object>> deleteProduct(
        @Header("Authorization") String bearerToken,
        @Path("id") int productId
    );

    // ===================== ADMIN: KELOLA TRANSAKSI =====================

    @GET("admin/orders")
    Call<ApiResponse<List<Order>>> getAdminOrders(
        @Header("Authorization") String bearerToken,
        @Query("status") String status
    );

    @FormUrlEncoded
    @PUT("admin/orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
        @Header("Authorization") String bearerToken,
        @Path("id") int orderId,
        @Field("status") String status
    );
}
