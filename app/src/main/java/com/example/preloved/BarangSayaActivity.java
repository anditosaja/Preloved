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
import com.example.preloved.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarangSayaActivity extends AppCompatActivity {

    private RecyclerView rvMyProducts;
    private TextView tvEmptyState;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barang_saya);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Ambil Token Login
        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getToken();

        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvMyProducts = findViewById(R.id.rvMyProducts);
        ImageView btnBack = findViewById(R.id.btnBack);

        if (rvMyProducts != null) {
            rvMyProducts.setLayoutManager(new GridLayoutManager(this, 2));
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tarikBarangSaya();
    }

    private void tarikBarangSaya() {
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Product>> call = apiService.getMyProducts("Bearer " + token);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    if (products.isEmpty()) {
                        rvMyProducts.setVisibility(View.GONE);
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        rvMyProducts.setVisibility(View.VISIBLE);

                        ProductAdapter adapter = new ProductAdapter(products);
                        rvMyProducts.setAdapter(adapter);
                    }
                } else {
                    // --- BONGKAR PESAN ERROR ASLI DARI LARAVEL ---
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error Tidak Diketahui";
                        int errorCode = response.code();

                        // Munculin di Logcat warna merah
                        Log.e("API_ERROR_PRELOVED", "Error Code: " + errorCode + " | Detail: " + errorBody);

                        // Munculin pop-up panjang di layar HP
                        Toast.makeText(BarangSayaActivity.this, "Gagal (Code " + errorCode + "). Cek Logcat!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BarangSayaActivity.this, "Gagal mengambil data toko", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(BarangSayaActivity.this, "Koneksi ke server gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
