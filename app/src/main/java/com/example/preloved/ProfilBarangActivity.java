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
import com.example.preloved.network.Config;
import com.example.preloved.utils.FavoriteManager;
import com.example.preloved.utils.SessionManager;

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
        TextView tvCondition = findViewById(R.id.tvCondition);
        TextView tvProductLocation = findViewById(R.id.tvProductLocation);
        TextView tvDescription = findViewById(R.id.tvDescription);

        // Deklarasi Komponen UI Profil Penjual
        ImageView imgSellerAvatar = findViewById(R.id.imgSellerAvatar);
        TextView tvSellerName = findViewById(R.id.tvSellerName);
        TextView tvSellerRating = findViewById(R.id.tvSellerRating);
        TextView tvSellerFollowers = findViewById(R.id.tvSellerFollowers);
        com.google.android.material.button.MaterialButton btnIkuti = findViewById(R.id.btnIkuti);

        com.google.android.material.button.MaterialButton btnBeli = findViewById(R.id.btnBeli);

        // TANGKAP DATA PRODUCT
        if (getIntent() != null && getIntent().hasExtra("PRODUCT")) {
            Product product = (Product) getIntent().getSerializableExtra("PRODUCT");

            if (product != null) {
                String name = product.getNama_barang();

                tvProductName.setText(product.getNama_barang());
                tvCondition.setText(product.getKondisi());
                tvProductLocation.setText(product.getLokasi_kota());
                tvDescription.setText(product.getDeskripsi());

                // LOGIKA FAVORIT
                ImageView btnFavorite = findViewById(R.id.btnFavorite);
                if (btnFavorite != null) {
                    FavoriteManager favManager = new FavoriteManager(this);
                    boolean isFav = favManager.isFavorite(product.getProductId());
                    btnFavorite.setImageResource(isFav ? R.drawable.heart_fill : R.drawable.heart);
                    btnFavorite.setColorFilter(isFav ? android.graphics.Color.RED : android.graphics.Color.parseColor("#BDBDBD"));

                    btnFavorite.setOnClickListener(v -> {
                        favManager.toggleFavorite(product);
                        boolean state = favManager.isFavorite(product.getProductId());
                        btnFavorite.setImageResource(state ? R.drawable.heart_fill : R.drawable.heart);
                        btnFavorite.setColorFilter(state ? android.graphics.Color.RED : android.graphics.Color.parseColor("#BDBDBD"));
                    });
                }

                loadBarangSerupa(product.getProductId(), product.getCategoryId());

                // Harga
                try {
                    double harga = Double.parseDouble(product.getHarga_jual());
                    tvProductPrice.setText("Rp " + new DecimalFormat("#,###").format(harga));
                } catch (Exception e) {
                    tvProductPrice.setText("Rp " + product.getHarga_jual());
                }

                // Status Barang
                if (product.getStatus_barang() != null && !product.getStatus_barang().equalsIgnoreCase("available")) {
                    android.graphics.ColorMatrix matrix = new android.graphics.ColorMatrix();
                    matrix.setSaturation(0f);
                    imgProduct.setColorFilter(new android.graphics.ColorMatrixColorFilter(matrix));
                    if (btnBeli != null) {
                        btnBeli.setText("Habis Terjual (SOLD)");
                        btnBeli.setEnabled(false);
                        btnBeli.setBackgroundColor(android.graphics.Color.parseColor("#9E9E9E"));
                    }
                } else {
                    imgProduct.clearColorFilter();
                    if (btnBeli != null) {
                        btnBeli.setOnClickListener(v -> {
                            Intent intent = new Intent(this, OrderActivity.class);
                            intent.putExtra("PRODUCT", product);
                            startActivity(intent);
                        });
                    }
                }

                // Foto Produk
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    String imgP = product.getImages().get(0).getImage_path();
                    Glide.with(this).load(imgP.startsWith("http") ? imgP : Config.IMAGE_URL + imgP).into(imgProduct);
                }

                // Penjual & Follow
                if (product.getSeller() != null) {
                    Seller penjual = product.getSeller();
                    String namaPenjual = penjual.getName() != null ? penjual.getName() : "Penjual";
                    tvSellerName.setText(namaPenjual);
                    tvSellerFollowers.setText("(" + penjual.getFollowersCount() + ")");

                    if (penjual.getFotoProfil() != null) {
                        String aUrl = penjual.getFotoProfil().startsWith("http") ? penjual.getFotoProfil() : Config.IMAGE_URL + penjual.getFotoProfil();
                        Glide.with(this).load(aUrl).placeholder(android.R.drawable.sym_contact_card).into(imgSellerAvatar);
                    }

                    // =========================================================
                    // LOGIKA TOGGLE FOLLOW / UNFOLLOW
                    // =========================================================
                    btnIkuti.setOnClickListener(v -> {
                        // Kunci tombol sementara biar user gak spam klik berturut-turut
                        btnIkuti.setEnabled(false);

                        ApiService api = RetrofitClient.getClient().create(ApiService.class);
                        String token = new SessionManager(ProfilBarangActivity.this).getToken();

                        if (token == null || token.isEmpty()) {
                            Toast.makeText(ProfilBarangActivity.this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
                            btnIkuti.setEnabled(true);
                            return;
                        }

                        String auth = token.startsWith("Bearer ") ? token : "Bearer " + token;

                        // Cek status tulisan tombol saat ini buat nentuin aksi selanjutnya
                        boolean isCurrentlyFollowing = btnIkuti.getText().toString().equalsIgnoreCase("Mengikuti");

                        if (isCurrentlyFollowing) {
                            // ---------------------------------------------------------
                            // AKSI BERHENTI MENGIKUTI (UNFOLLOW)
                            // ---------------------------------------------------------
                            api.unfollowUser(auth, penjual.getId()).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(ProfilBarangActivity.this, "Berhenti mengikuti " + namaPenjual, Toast.LENGTH_SHORT).show();

                                        // Kembalikan UI ke default (tombol transparan, tulisan ungu)
                                        btnIkuti.setText("Ikuti");
                                        btnIkuti.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                                        btnIkuti.setTextColor(android.graphics.Color.parseColor("#6952D9"));

                                        // Kurangi angka follower secara lokal (mencegah minus)
                                        int updateAngka = penjual.getFollowersCount() - 1;
                                        if (updateAngka < 0) updateAngka = 0;

                                        penjual.setFollowersCount(updateAngka);
                                        tvSellerFollowers.setText("(" + updateAngka + ")");
                                    } else {
                                        Toast.makeText(ProfilBarangActivity.this, "Gagal berhenti mengikuti", Toast.LENGTH_SHORT).show();
                                    }
                                    btnIkuti.setEnabled(true); // Buka kunci tombol
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(ProfilBarangActivity.this, "Koneksi terputus", Toast.LENGTH_SHORT).show();
                                    btnIkuti.setEnabled(true);
                                }
                            });

                        } else {
                            // ---------------------------------------------------------
                            // AKSI MENGIKUTI (FOLLOW)
                            // ---------------------------------------------------------
                            api.followUser(auth, penjual.getId()).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(ProfilBarangActivity.this, "Berhasil mengikuti " + namaPenjual, Toast.LENGTH_SHORT).show();

                                        // Ubah UI jadi status sudah mengikuti (tombol ungu solid)
                                        btnIkuti.setText("Mengikuti");
                                        btnIkuti.setBackgroundColor(android.graphics.Color.parseColor("#6952D9"));
                                        btnIkuti.setTextColor(android.graphics.Color.WHITE);

                                        // Naikkan angka follower secara lokal
                                        int updateAngka = penjual.getFollowersCount() + 1;
                                        penjual.setFollowersCount(updateAngka);
                                        tvSellerFollowers.setText("(" + updateAngka + ")");

                                    } else if (response.code() == 422) {
                                        // Validasi nggak boleh nge-follow lapak sendiri
                                        Toast.makeText(ProfilBarangActivity.this, "Anda tidak bisa mengikuti toko sendiri", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ProfilBarangActivity.this, "Gagal mengikuti seller", Toast.LENGTH_SHORT).show();
                                    }
                                    btnIkuti.setEnabled(true); // Buka kunci tombol
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(ProfilBarangActivity.this, "Koneksi terputus", Toast.LENGTH_SHORT).show();
                                    btnIkuti.setEnabled(true);
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private void loadBarangSerupa(int currentProductId, int categoryId) {
        RecyclerView rv = findViewById(R.id.rvBarangSerupa);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getProductsByCategory(categoryId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> list = new ArrayList<>();
                    for (Product p : response.body()) {
                        if (p.getCategoryId() == categoryId && p.getProductId() != currentProductId) list.add(p);
                    }
                    rv.setAdapter(new ProductAdapter(list));
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {}
        });
    }
}
