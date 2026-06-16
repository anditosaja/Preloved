package com.example.preloved;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.example.preloved.models.Product;
import com.example.preloved.models.Seller;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilBarangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profil_barang);

        // Pengaturan padding agar tidak menabrak bar status HP
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        ImageView btnBackImg = findViewById(R.id.btnBackImg);
        if (btnBackImg != null) {
            btnBackImg.setOnClickListener(v -> finish());
        }

        // Deklarasi Komponen UI Produk
        ImageView imgProduct = findViewById(R.id.imgProduct);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductCategory = findViewById(R.id.tvProductCategory);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvOldPrice = findViewById(R.id.tvOldPrice);
        TextView tvDiscount = findViewById(R.id.tvDiscount);
        TextView tvCondition = findViewById(R.id.tvCondition);
        TextView tvProductLocation = findViewById(R.id.tvProductLocation);
        TextView tvDescription = findViewById(R.id.tvDescription);

        // Deklarasi Komponen UI Barang Serupa
        ImageView imgSerupa1 = findViewById(R.id.imgSerupa1);
        TextView tvNamaSerupa1 = findViewById(R.id.tvNamaSerupa1);
        TextView tvHargaSerupa1 = findViewById(R.id.tvHargaSerupa1);
        ImageView imgSerupa2 = findViewById(R.id.imgSerupa2);
        TextView tvNamaSerupa2 = findViewById(R.id.tvNamaSerupa2);
        TextView tvHargaSerupa2 = findViewById(R.id.tvHargaSerupa2);
        MaterialCardView cardSerupa1 = findViewById(R.id.cardSerupa1);
        MaterialCardView cardSerupa2 = findViewById(R.id.cardSerupa2);
        TextView tvTitleBarangSerupa = findViewById(R.id.tvTitleBarangSerupa);

        // Deklarasi Komponen UI Profil Penjual
        ImageView imgSellerAvatar = findViewById(R.id.imgSellerAvatar);
        TextView tvSellerName = findViewById(R.id.tvSellerName);
        TextView tvSellerRating = findViewById(R.id.tvSellerRating);
        TextView tvSellerFollowers = findViewById(R.id.tvSellerFollowers);
        com.google.android.material.button.MaterialButton btnIkuti = findViewById(R.id.btnIkuti);

        // DEKLARASI TOMBOL BELI/ORDER DARI LAYOUT
        com.google.android.material.button.MaterialButton btnBeli = findViewById(R.id.btnBeli);

        // TANGKAP DATA PRODUCT DARI HALAMAN SEBELUMNYA
        if (getIntent() != null && getIntent().hasExtra("PRODUCT")) {

            Product product = (Product) getIntent().getSerializableExtra("PRODUCT");

            if (product != null) {
                String name = product.getNama_barang();

                tvProductName.setText(product.getNama_barang());
                tvCondition.setText(product.getKondisi());
                tvProductLocation.setText(product.getLokasi_kota());
                tvDescription.setText(product.getDeskripsi());

                // Format Harga Jual
                try {
                    double harga = Double.parseDouble(product.getHarga_jual());
                    tvProductPrice.setText("Rp " + new DecimalFormat("#,###").format(harga));
                } catch (Exception e) {
                    tvProductPrice.setText("Rp " + product.getHarga_jual());
                }

                // Format Harga Asli (Dicoret)
                if (product.getHarga_asli() != null && !product.getHarga_asli().isEmpty()) {
                    try {
                        double hargaAsli = Double.parseDouble(product.getHarga_asli());
                        tvOldPrice.setText("Rp " + new DecimalFormat("#,###").format(hargaAsli));
                    } catch (Exception e) {
                        tvOldPrice.setText("Rp " + product.getHarga_asli());
                    }
                    tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tvOldPrice.setVisibility(View.GONE);
                }

                String category = "Produk";
                if (name != null) {
                    if (name.toLowerCase().contains("shirt")
                        || name.toLowerCase().contains("hoodie")
                        || name.toLowerCase().contains("flannel")) {
                        category = "Pakaian";
                    }
                }
                tvProductCategory.setText("Kategori: " + category);

                // Foto Produk
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    String imagePath = product.getImages().get(0).getImage_path();
                    String imageUrl = imagePath.startsWith("http") ? imagePath : "http://192.168.18.169:8000/storage/" + imagePath;

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(imgProduct);
                }

                // ================= NAVIGASI KE ORDER ACTIVITY =================
                if (btnBeli != null) {
                    btnBeli.setOnClickListener(v -> {
                        Intent intent = new Intent(ProfilBarangActivity.this, OrderActivity.class);
                        // Masukin objek product ke dalam koper intent biar ditangkep sama OrderActivity
                        intent.putExtra("PRODUCT", product);
                        startActivity(intent);
                    });
                }

                // ================= LOGIKA DINAMIS PENJUAL & FOLLOW =================
                if (product.getSeller() != null) {
                    Seller penjual = product.getSeller();

                    tvSellerName.setText(penjual.getName());
                    tvSellerFollowers.setText("(" + penjual.getFollowersCount() + ")");
                    tvSellerRating.setText("4.8");

                    // Load foto profil penjual
                    if (penjual.getFotoProfil() != null && !penjual.getFotoProfil().isEmpty()) {
                        String avatarUrl = penjual.getFotoProfil().startsWith("http") ? penjual.getFotoProfil() : "http://192.168.18.169:8000/storage/" + penjual.getFotoProfil();
                        Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(android.R.drawable.sym_contact_card)
                            .into(imgSellerAvatar);
                    }

                    // Aksi Klik Tombol Ikuti
                    btnIkuti.setOnClickListener(v -> {
                        btnIkuti.setText("Mengikuti");
                        btnIkuti.setTextColor(android.graphics.Color.WHITE);
                        btnIkuti.setBackgroundColor(android.graphics.Color.parseColor("#6952D9"));

                        int currentFollowers = penjual.getFollowersCount();
                        penjual.setFollowersCount(currentFollowers + 1);
                        tvSellerFollowers.setText("(" + penjual.getFollowersCount() + ")");
                        btnIkuti.setEnabled(false);

                        SessionManager sessionManager = new SessionManager(this);
                        String token = sessionManager.getBearerToken();

                        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
                        apiService.followUser(token, penjual.getId()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(ProfilBarangActivity.this, "Berhasil mengikuti " + penjual.getName(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfilBarangActivity.this, "Gagal follow. Coba lagi.", Toast.LENGTH_SHORT).show();
                                    btnIkuti.setText("Ikuti");
                                    btnIkuti.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                                    btnIkuti.setTextColor(android.graphics.Color.parseColor("#6952D9"));
                                    penjual.setFollowersCount(currentFollowers);
                                    tvSellerFollowers.setText("(" + penjual.getFollowersCount() + ")");
                                    btnIkuti.setEnabled(true);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(ProfilBarangActivity.this, "Koneksi terputus: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                btnIkuti.setText("Ikuti");
                                btnIkuti.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                                btnIkuti.setTextColor(android.graphics.Color.parseColor("#6952D9"));
                                penjual.setFollowersCount(currentFollowers);
                                tvSellerFollowers.setText("(" + penjual.getFollowersCount() + ")");
                                btnIkuti.setEnabled(true);
                            }
                        });
                    });
                }

                // ================= BARANG SERUPA (Statis) =================
                if (name != null) {
                    if (name.equalsIgnoreCase("Zaro Cargo Shirt")) {
                        imgSerupa1.setImageResource(R.drawable.flannel);
                        tvNamaSerupa1.setText("Flannel Casual");
                        tvHargaSerupa1.setText("Rp120.000");
                        imgSerupa2.setImageResource(R.drawable.hoodie);
                        tvNamaSerupa2.setText("Streetwear Hoodie");
                        tvHargaSerupa2.setText("Rp185.000");
                    } else if (name.equalsIgnoreCase("Flannel Casual Shirt")) {
                        imgSerupa1.setImageResource(R.drawable.zarocargo_shirt);
                        tvNamaSerupa1.setText("Zaro Cargo");
                        tvHargaSerupa1.setText("Rp150.000");
                        imgSerupa2.setImageResource(R.drawable.hoodie);
                        tvNamaSerupa2.setText("Streetwear Hoodie");
                        tvHargaSerupa2.setText("Rp185.000");
                    } else if (name.equalsIgnoreCase("Oversized Streetwear Hoodie")) {
                        imgSerupa1.setImageResource(R.drawable.zarocargo_shirt);
                        tvNamaSerupa1.setText("Zaro Cargo");
                        tvHargaSerupa1.setText("Rp150.000");
                        imgSerupa2.setImageResource(R.drawable.flannel);
                        tvNamaSerupa2.setText("Flannel Casual");
                        tvHargaSerupa2.setText("Rp120.000");
                    } else {
                        cardSerupa1.setVisibility(View.GONE);
                        cardSerupa2.setVisibility(View.GONE);
                        tvTitleBarangSerupa.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
