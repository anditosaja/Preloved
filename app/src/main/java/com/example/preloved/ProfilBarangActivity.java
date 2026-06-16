package com.example.preloved;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.example.preloved.adapters.ProductAdapter;
import com.example.preloved.models.Product;
import com.example.preloved.models.Seller;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.FavoriteManager;
import com.example.preloved.utils.SessionManager;
import com.example.preloved.network.Config;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

        // Deklarasi Komponen UI Profil Penjual
        ImageView imgSellerAvatar = findViewById(R.id.imgSellerAvatar);
        TextView tvSellerName = findViewById(R.id.tvSellerName);
        TextView tvSellerRating = findViewById(R.id.tvSellerRating);
        TextView tvSellerFollowers = findViewById(R.id.tvSellerFollowers);
        com.google.android.material.button.MaterialButton btnIkuti = findViewById(R.id.btnIkuti);

        // DEKLARASI TOMBOL BELI DARI LAYOUT BARU
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

                // =======================================================
                // LOGIKA FAVORIT DINAMIS DI HALAMAN DETAIL
                // =======================================================
                ImageView btnFavorite = findViewById(R.id.btnFavorite);
                if (btnFavorite != null) {
                    FavoriteManager favManager = new FavoriteManager(ProfilBarangActivity.this);

                    // Set status nyala/mati awal saat masuk halaman
                    boolean isFav = favManager.isFavorite(product.getProductId());
                    btnFavorite.setImageResource(isFav ? R.drawable.heart_fill : R.drawable.heart);
                    btnFavorite.setColorFilter(isFav ? android.graphics.Color.RED : android.graphics.Color.parseColor("#BDBDBD"));

                    // Saat tombol love dipencet
                    btnFavorite.setOnClickListener(v -> {
                        favManager.toggleFavorite(product);
                        boolean newFavState = favManager.isFavorite(product.getProductId());

                        btnFavorite.setImageResource(newFavState ? R.drawable.heart_fill : R.drawable.heart);
                        btnFavorite.setColorFilter(newFavState ? android.graphics.Color.RED : android.graphics.Color.parseColor("#BDBDBD"));

                        Toast.makeText(ProfilBarangActivity.this, newFavState ? "Ditambahkan ke Favorit" : "Dihapus dari Favorit", Toast.LENGTH_SHORT).show();
                    });
                }

                // =========================================================
                // PANGGIL FUNGSI LOAD BARANG SERUPA
                // =========================================================
                loadBarangSerupa(product.getProductId(), product.getCategoryId());

                // Format Harga Jual
                try {
                    double harga = Double.parseDouble(product.getHarga_jual());
                    tvProductPrice.setText("Rp " + new DecimalFormat("#,###").format(harga));
                } catch (Exception e) {
                    tvProductPrice.setText("Rp " + product.getHarga_jual());
                }

                // =========================================================
                // LOGIKA DINAMIS STATUS BARANG DI DETAIL (TERJUAL / TERSEDIA)
                // =========================================================
                if (product.getStatus_barang() != null && !product.getStatus_barang().equalsIgnoreCase("available")) {
                    // 1. Buat gambar utama menjadi abu-abu
                    android.graphics.ColorMatrix matrix = new android.graphics.ColorMatrix();
                    matrix.setSaturation(0f);
                    android.graphics.ColorMatrixColorFilter filter = new android.graphics.ColorMatrixColorFilter(matrix);
                    imgProduct.setColorFilter(filter);

                    // 2. Ubah fungsi dan tampilan tombol beli
                    if (btnBeli != null) {
                        btnBeli.setText("Habis Terjual (SOLD)");
                        btnBeli.setEnabled(false); // Nonaktifkan tombol agar tidak bisa diklik
                        btnBeli.setBackgroundColor(android.graphics.Color.parseColor("#9E9E9E")); // Ubah jadi abu-abu
                    }

                    Toast.makeText(this, "Produk ini sudah terjual", Toast.LENGTH_SHORT).show();
                } else {
                    // Jika masih tersedia
                    imgProduct.clearColorFilter();
                    if (btnBeli != null) {
                        btnBeli.setEnabled(true);
                        btnBeli.setOnClickListener(v -> {
                            Intent intent = new Intent(ProfilBarangActivity.this, OrderActivity.class);
                            intent.putExtra("PRODUCT", product);
                            startActivity(intent);
                        });
                    }
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
                    String imageUrl = imagePath.startsWith("http") ? imagePath : Config.IMAGE_URL + imagePath;

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(imgProduct);
                }

                // ================= LOGIKA DINAMIS PENJUAL & FOLLOW =================
                if (product.getSeller() != null) {
                    Seller penjual = product.getSeller();

                    tvSellerName.setText(penjual.getName());
                    tvSellerFollowers.setText("(" + penjual.getFollowersCount() + ")");
                    tvSellerRating.setText("4.8");

                    // Load foto profil penjual
                    if (penjual.getFotoProfil() != null && !penjual.getFotoProfil().isEmpty()) {
                        String avatarUrl = penjual.getFotoProfil().startsWith("http") ? penjual.getFotoProfil() : Config.IMAGE_URL + penjual.getFotoProfil();
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
                                    kembalikanTombolFollow(btnIkuti, penjual, currentFollowers);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(ProfilBarangActivity.this, "Koneksi terputus: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                kembalikanTombolFollow(btnIkuti, penjual, currentFollowers);
                            }
                        });
                    });
                }
            }
        }
    }

    private void kembalikanTombolFollow(com.google.android.material.button.MaterialButton btnIkuti, Seller penjual, int currentFollowers) {
        btnIkuti.setText("Ikuti");
        btnIkuti.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnIkuti.setTextColor(android.graphics.Color.parseColor("#6952D9"));
        penjual.setFollowersCount(currentFollowers);
        TextView tvSellerFollowers = findViewById(R.id.tvSellerFollowers);
        tvSellerFollowers.setText("(" + penjual.getFollowersCount() + ")");
        btnIkuti.setEnabled(true);
    }

    // =========================================================================
    // FUNGSI LOAD BARANG SERUPA (Berada di luar onCreate)
    // =========================================================================
    private void loadBarangSerupa(int currentProductId, int categoryId) {
        RecyclerView rvBarangSerupa = findViewById(R.id.rvBarangSerupa);
        TextView tvEmptyBarangSerupa = findViewById(R.id.tvEmptyBarangSerupa);

        // Atur agar list menyamping (Horizontal)
        rvBarangSerupa.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Product>> call = apiService.getProductsByCategory(categoryId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> semuaProduk = response.body();
                    List<Product> produkSerupa = new ArrayList<>();

                    // Looping data yang didapat
                    for (Product p : semuaProduk) {
                        // Masukkan produk jika kategorinya sama, TAPI kecualikan produk yang sedang dilihat saat ini
                        if (p.getCategoryId() == categoryId && p.getProductId() != currentProductId) {
                            produkSerupa.add(p);
                        }
                    }

                    // Tampilkan atau sembunyikan state kosong
                    if (produkSerupa.isEmpty()) {
                        rvBarangSerupa.setVisibility(View.GONE);
                        tvEmptyBarangSerupa.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyBarangSerupa.setVisibility(View.GONE);
                        rvBarangSerupa.setVisibility(View.VISIBLE);

                        // Gunakan ProductAdapter yang sama dengan yang dipakai di Home/Kategori
                        ProductAdapter adapter = new ProductAdapter(produkSerupa);
                        rvBarangSerupa.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProfilBarangActivity.this, "Gagal memuat barang serupa", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
