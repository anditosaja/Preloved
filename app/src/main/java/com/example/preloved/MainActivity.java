package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.preloved.models.Product;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.network.ApiService;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // Deklarasi Komponen UI Sesuai ID di activity_main.xml kamu
    private MaterialCardView cardTrending1, cardTrending2;
    private TextView tvProdName1, tvProdPrice1, tvProdLoc1;
    private TextView tvProdName2, tvProdPrice2, tvProdLoc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // --- SAMAKAN INISIALISASI DENGAN ID XML ASLI KAMU ---
        cardTrending1 = findViewById(R.id.cardTrending1);
        cardTrending2 = findViewById(R.id.cardTrending2);

        tvProdName1 = findViewById(R.id.tvProdName1);
        tvProdPrice1 = findViewById(R.id.tvProdPrice1); // Ini ID baru yang tadi kita tambahkan
        tvProdLoc1 = findViewById(R.id.tvProdLoc1);

        tvProdName2 = findViewById(R.id.tvProdName2);
        tvProdPrice2 = findViewById(R.id.tvProdPrice2); // Ini ID baru yang tadi kita tambahkan
        tvProdLoc2 = findViewById(R.id.tvProdLoc2);

        // --- JALANKAN TRIGER KONEKSI KE API LARAVEL ---
        ambilDataDariLaravel();

        // --- HEADER KATEGORI ---
        TextView tvLihatSemuaKategori = findViewById(R.id.tvLihatSemuaKategori);
        if (tvLihatSemuaKategori != null) {
            tvLihatSemuaKategori.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, KategoriActivity.class);
                startActivity(intent);
            });
        }

        // --- KATEGORI HOMEPAGE MENUJU DAFTAR BARANG ---
        LinearLayout btnKatPakaian = findViewById(R.id.btnKatPakaian);
        LinearLayout btnKatSepatu = findViewById(R.id.btnKatSepatu);
        LinearLayout btnKatTas = findViewById(R.id.btnKatTas);

        if (btnKatPakaian != null) {
            btnKatPakaian.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Pakaian");
                intent.putExtra("CATEGORY_ID", 1);
                startActivity(intent);
            });
        }

        if (btnKatSepatu != null) {
            btnKatSepatu.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Sepatu");
                intent.putExtra("CATEGORY_ID", 2);
                startActivity(intent);
            });
        }

        if (btnKatTas != null) {
            btnKatTas.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Tas");
                intent.putExtra("CATEGORY_ID", 3);
                startActivity(intent);
            });
        }

        // ====================================================================
        // BOTTOM NAVIGATION BAR
        // ====================================================================
        LinearLayout navBeranda = findViewById(R.id.navBeranda);
        if (navBeranda != null) {
            navBeranda.setOnClickListener(v -> ambilDataDariLaravel());
        }

        LinearLayout navKategori = findViewById(R.id.navKategori);
        if (navKategori != null) {
            navKategori.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, KategoriActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout navJual = findViewById(R.id.navJual);
        if (navJual != null) {
            navJual.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, JualActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout navChat = findViewById(R.id.navChat);
        if (navChat != null) {
            navChat.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout navProfil = findViewById(R.id.navProfil);
        if (navProfil != null) {
            navProfil.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfilActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void ambilDataDariLaravel() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Product>> call = apiService.getTrendingProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> products = response.body();

                    if (products.size() >= 1) {
                        setupCardTrending1(products.get(0));
                    }
                    if (products.size() >= 2) {
                        setupCardTrending2(products.get(1));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Gagal memproses data produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal konek API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupCardTrending1(Product product) {
        if (tvProdName1 != null) tvProdName1.setText(product.getNama_barang());
        if (tvProdPrice1 != null) tvProdPrice1.setText("Rp" + product.getHarga_jual());
        if (tvProdLoc1 != null) tvProdLoc1.setText("📍 " + product.getLokasi_kota());

        if (cardTrending1 != null) {
            cardTrending1.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfilBarangActivity.class);
                intent.putExtra("PROD_NAME", product.getNama_barang());
                intent.putExtra("PROD_PRICE", "Rp" + product.getHarga_jual());
                intent.putExtra("PROD_OLD_PRICE", "Rp" + product.getHarga_asli());
                intent.putExtra("PROD_LOCATION", product.getLokasi_kota());
                intent.putExtra("PROD_CONDITION", product.getKondisi());
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    intent.putExtra("PROD_IMAGE_URL", product.getImages().get(0).getImage_path());
                }
                startActivity(intent);
            });
        }
    }

    private void setupCardTrending2(Product product) {
        if (tvProdName2 != null) tvProdName2.setText(product.getNama_barang());
        if (tvProdPrice2 != null) tvProdPrice2.setText("Rp" + product.getHarga_jual());
        if (tvProdLoc2 != null) tvProdLoc2.setText("📍 " + product.getLokasi_kota());

        if (cardTrending2 != null) {
            cardTrending2.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfilBarangActivity.class);
                intent.putExtra("PROD_NAME", product.getNama_barang());
                intent.putExtra("PROD_PRICE", "Rp" + product.getHarga_jual());
                intent.putExtra("PROD_OLD_PRICE", "Rp" + product.getHarga_asli());
                intent.putExtra("PROD_LOCATION", product.getLokasi_kota());
                intent.putExtra("PROD_CONDITION", product.getKondisi());
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    intent.putExtra("PROD_IMAGE_URL", product.getImages().get(0).getImage_path());
                }
                startActivity(intent);
            });
        }
    }
}
