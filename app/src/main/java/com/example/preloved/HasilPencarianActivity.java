package com.example.preloved;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HasilPencarianActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private TextView tvEmptyState, tvTitleKategori;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // KITA RE-USE LAYOUT DAFTAR BARANG BIAR GAK REPOT DESAIN LAGI!
        setContentView(R.layout.activity_daftar_barang);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        tvTitleKategori = findViewById(R.id.tvTitleKategori);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvProducts = findViewById(R.id.rvProducts);
        ImageView btnBack = findViewById(R.id.btnBack);

        if (rvProducts != null) {
            rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Tangkap keyword ketikan dari Dashboard
        keyword = getIntent().getStringExtra("KEYWORD");

        if (keyword != null) {
            tvTitleKategori.setText("Hasil: \"" + keyword + "\"");
            tarikHasilPencarian(keyword);
        }
    }

    private void tarikHasilPencarian(String kataKunci) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Memanggil API Search Laravel (Kategori kita set null agar mencari di SEMUA kategori)
        Call<List<Product>> call = apiService.searchProducts(kataKunci, null);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    if (products.isEmpty()) {
                        rvProducts.setVisibility(View.GONE);
                        tvEmptyState.setText("Yah, barang \"" + kataKunci + "\" nggak ketemu.");
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        rvProducts.setVisibility(View.VISIBLE);

                        ProductAdapter adapter = new ProductAdapter(products);
                        rvProducts.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(HasilPencarianActivity.this, "Gagal mengambil data pencarian", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("API_FAILURE", "Koneksi mati: " + t.getMessage());
                Toast.makeText(HasilPencarianActivity.this, "Koneksi ke server gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
