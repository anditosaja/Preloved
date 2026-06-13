package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preloved.adapters.ProductAdapter;
import com.example.preloved.models.Product;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarBarangActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private TextView tvEmptyState, tvTitleKategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar_barang);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Deklarasi Komponen
        tvTitleKategori = findViewById(R.id.tvTitleKategori);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvProducts = findViewById(R.id.rvProducts);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Atur RecyclerView menjadi Grid (2 kolom) layaknya e-commerce sungguhan
        if (rvProducts != null) {
            rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Tangkap data kategori dari Halaman Sebelumnya
        String namaKategori = getIntent().getStringExtra("NAMA_KATEGORI");
        int categoryId = getIntent().getIntExtra("CATEGORY_ID", -1);

        if (namaKategori != null) {
            tvTitleKategori.setText(namaKategori);
        }

        // Jika ID valid, tarik data dari Laravel
        if (categoryId != -1) {
            tarikDataKategori(categoryId);
        } else {
            Toast.makeText(this, "Kategori tidak valid", Toast.LENGTH_SHORT).show();
            tvEmptyState.setVisibility(View.VISIBLE);
        }
    }

    private void tarikDataKategori(int categoryId) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        // Memanggil endpoint: /api/products/search?category_id=X
        Call<List<Product>> call = apiService.getProductsByCategory(categoryId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Product> semuaProduk = response.body();
                    List<Product> produkSesuaiKategori = new ArrayList<>();

                    // =========================================================
                    // FILTERING MANUAL: Seleksi ketat berdasarkan categoryId
                    // =========================================================
                    for (Product p : semuaProduk) {
                        // Jika id kategori produk sama dengan id kategori yang diklik user
                        if (p.getCategoryId() == categoryId) {
                            produkSesuaiKategori.add(p);
                        }
                    }

                    if (produkSesuaiKategori.isEmpty()) {
                        // Jika hasil seleksi kosong (tidak ada barang di kategori ini)
                        rvProducts.setVisibility(View.GONE);
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        // Jika barang ada, masukkan ke adapter dan tampilkan
                        tvEmptyState.setVisibility(View.GONE);
                        rvProducts.setVisibility(View.VISIBLE);

                        ProductAdapter adapter = new ProductAdapter(produkSesuaiKategori);
                        rvProducts.setAdapter(adapter);
                    }
                } else {
                    Log.e("API_ERROR", "Response gagal: " + response.code());
                    Toast.makeText(DaftarBarangActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_FAILURE", "Koneksi mati: " + t.getMessage());
                Toast.makeText(DaftarBarangActivity.this, "Koneksi ke server gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
