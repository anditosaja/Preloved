package com.example.preloved.network;

import com.example.preloved.models.LoginRequest;
import com.example.preloved.models.LoginResponse;
import com.example.preloved.models.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @POST("login")
    Call<LoginResponse> login(
        @Body LoginRequest request
    );

    @GET("products")
    Call<List<Product>> getProducts();

    // Tambahan endpoint untuk fitur JUAL (Upload Data + Gambar)
    @Multipart
    @POST("products")
    Call<ResponseBody> uploadProduct(
        @Part("seller_id") RequestBody sellerId,
        @Part("category_id") RequestBody categoryId,
        @Part("nama_barang") RequestBody namaBarang,
        @Part("deskripsi") RequestBody deskripsi,
        @Part("harga_jual") RequestBody hargaJual,
        @Part("kondisi") RequestBody kondisi,
        @Part("lokasi_kota") RequestBody lokasiKota,
        @Part MultipartBody.Part foto
    );
}
