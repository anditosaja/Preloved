package com.example.preloved;

import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.preloved.models.HomeResponse;
import com.example.preloved.models.Product;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private MaterialCardView cardTrending1, cardTrending2;
    private TextView tvProdName1, tvProdPrice1, tvProdLoc1;
    private TextView tvProdName2, tvProdPrice2, tvProdLoc2;
    private ImageView ivTrending1, ivTrending2;
    private ImageView ivFavorite1, ivFavorite2;
    private MaterialCardView cardRekomendasi1, cardRekomendasi2, cardRekomendasi3;
    private ImageView ivRekomendasi1, ivRekomendasi2, ivRekomendasi3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.activity_main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        inisialisasiUI();
        aturNavigasi();
        ambilDataDariLaravel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Setiap kali user balik ke Beranda, update data otomatis
        ambilDataDariLaravel();
    }

    private void inisialisasiUI() {
        cardTrending1 = findViewById(R.id.cardTrending1);
        cardTrending2 = findViewById(R.id.cardTrending2);
        tvProdName1 = findViewById(R.id.tvProdName1);
        tvProdPrice1 = findViewById(R.id.tvProdPrice1);
        tvProdLoc1 = findViewById(R.id.tvProdLoc1);
        tvProdName2 = findViewById(R.id.tvProdName2);
        tvProdPrice2 = findViewById(R.id.tvProdPrice2);
        tvProdLoc2 = findViewById(R.id.tvProdLoc2);
        ivTrending1 = findViewById(R.id.ivTrending1);
        ivTrending2 = findViewById(R.id.ivTrending2);
        ivFavorite1 = findViewById(R.id.ivFavorite1);
        ivFavorite2 = findViewById(R.id.ivFavorite2);
        cardRekomendasi1 = findViewById(R.id.cardRekomendasi1);
        cardRekomendasi2 = findViewById(R.id.cardRekomendasi2);
        cardRekomendasi3 = findViewById(R.id.cardRekomendasi3);
        ivRekomendasi1 = findViewById(R.id.ivRekomendasi1);
        ivRekomendasi2 = findViewById(R.id.ivRekomendasi2);
        ivRekomendasi3 = findViewById(R.id.ivRekomendasi3);

        // ================= LOGIKA SEARCH BAR =================
        EditText etSearchDashboard = findViewById(R.id.etSearchDashboard);
        if (etSearchDashboard != null) {
            etSearchDashboard.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keywordPencarian = etSearchDashboard.getText().toString().trim();

                    if (!keywordPencarian.isEmpty()) {
                        Intent intent = new Intent(MainActivity.this, HasilPencarianActivity.class);
                        intent.putExtra("KEYWORD", keywordPencarian);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Ketik nama barang dulu!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });
        }
    }

    private void aturNavigasi() {
        TextView tvLihatSemuaKategori = findViewById(R.id.tvLihatSemuaKategori);
        if (tvLihatSemuaKategori != null) tvLihatSemuaKategori.setOnClickListener(v -> pindahHalaman(KategoriActivity.class, false));

        TextView tvLihatSemuaTrending = findViewById(R.id.tvLihatSemuaTrending);
        if (tvLihatSemuaTrending != null) {
            tvLihatSemuaTrending.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
                intent.putExtra("TIPE_DAFTAR", "trending");
                startActivity(intent);
            });
        }

        TextView tvLihatSemuaRekomendasi = findViewById(R.id.tvLihatSemuaRekomendasi);
        if (tvLihatSemuaRekomendasi != null) {
            tvLihatSemuaRekomendasi.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
                intent.putExtra("TIPE_DAFTAR", "rekomendasi");
                startActivity(intent);
            });
        }

        // Kategori Grid
        LinearLayout btnKatPakaian = findViewById(R.id.btnKatPakaian);
        LinearLayout btnKatTas = findViewById(R.id.btnKatTas);
        LinearLayout btnKatSepatu = findViewById(R.id.btnKatSepatu);
        LinearLayout btnKatElektronik = findViewById(R.id.btnKatElektronik);
        LinearLayout btnKatLainnya = findViewById(R.id.btnKatLainnya);

        if (btnKatPakaian != null) btnKatPakaian.setOnClickListener(v -> keDaftarBarang("Pakaian", 1));
        if (btnKatTas != null) btnKatTas.setOnClickListener(v -> keDaftarBarang("Tas", 2));
        if (btnKatSepatu != null) btnKatSepatu.setOnClickListener(v -> keDaftarBarang("Sepatu", 3));
        if (btnKatElektronik != null) btnKatElektronik.setOnClickListener(v -> keDaftarBarang("Elektronik", 4));
        if (btnKatLainnya != null) btnKatLainnya.setOnClickListener(v -> pindahHalaman(KategoriActivity.class, false));

        // Bottom Navigation
        findViewById(R.id.navBeranda).setOnClickListener(v -> ambilDataDariLaravel());
        findViewById(R.id.navKategori).setOnClickListener(v -> pindahHalaman(KategoriActivity.class, true));
        findViewById(R.id.navJual).setOnClickListener(v -> pindahHalaman(JualActivity.class, false));
        findViewById(R.id.navChat).setOnClickListener(v -> pindahHalaman(ChatActivity.class, true));
        findViewById(R.id.navProfil).setOnClickListener(v -> pindahHalaman(ProfilActivity.class, true));
    }

    private void ambilDataDariLaravel() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<HomeResponse> call = apiService.getTrendingProducts();

        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {

                if (response.code() == 401) {
                    Toast.makeText(MainActivity.this, "Sesi berakhir, silakan login kembali", Toast.LENGTH_SHORT).show();
                    SessionManager sessionManager = new SessionManager(MainActivity.this);
                    sessionManager.clearSession();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> dataTerbaru = response.body().getRecommended();
                    List<Product> dataTrending = response.body().getTrending();

                    if (dataTerbaru != null && !dataTerbaru.isEmpty()) {
                        if (dataTerbaru.size() >= 1) setupCardTrending(1, dataTerbaru.get(0));
                        if (dataTerbaru.size() >= 2) setupCardTrending(2, dataTerbaru.get(1));
                    }
                    if (dataTrending != null && !dataTrending.isEmpty()) {
                        if (dataTrending.size() >= 1) setupCardRekomendasi(cardRekomendasi1, ivRekomendasi1, dataTrending.get(0));
                        if (dataTrending.size() >= 2) setupCardRekomendasi(cardRekomendasi2, ivRekomendasi2, dataTrending.get(1));
                        if (dataTrending.size() >= 3) setupCardRekomendasi(cardRekomendasi3, ivRekomendasi3, dataTrending.get(2));
                    }
                }
            }
            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal konek: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCardTrending(int urutan, Product product) {
        TextView tvName = (urutan == 1) ? tvProdName1 : tvProdName2;
        TextView tvPrice = (urutan == 1) ? tvProdPrice1 : tvProdPrice2;
        TextView tvLoc = (urutan == 1) ? tvProdLoc1 : tvProdLoc2;
        ImageView ivImage = (urutan == 1) ? ivTrending1 : ivTrending2;
        MaterialCardView card = (urutan == 1) ? cardTrending1 : cardTrending2;

        if (tvName != null) tvName.setText(product.getNama_barang());
        if (tvPrice != null) tvPrice.setText("Rp" + product.getHarga_jual());
        if (tvLoc != null) tvLoc.setText("📍 " + product.getLokasi_kota());

        if (product.getImages() != null && !product.getImages().isEmpty() && ivImage != null) {
            String imageUrl = "http://192.168.18.169:8000/storage/" + product.getImages().get(0).getImage_path();
            Glide.with(this).load(imageUrl).into(ivImage);
        }

        // =======================================================
        // LOGIKA FAVORIT DINAMIS
        // =======================================================
        ImageView ivFav = (urutan == 1) ? ivFavorite1 : ivFavorite2;

        if (ivFav != null) {
            com.example.preloved.utils.FavoriteManager favManager = new com.example.preloved.utils.FavoriteManager(this);

            // Cek status saat pertama kali dirender
            boolean isFav = favManager.isFavorite(product.getProductId());
            ivFav.setImageResource(isFav ? R.drawable.heart_fill : R.drawable.heart);
            ivFav.setColorFilter(isFav ? android.graphics.Color.RED : android.graphics.Color.parseColor("#BDBDBD"));

            // Aksi saat love dipencet
            ivFav.setOnClickListener(v -> {
                favManager.toggleFavorite(product);
                boolean newFavState = favManager.isFavorite(product.getProductId());

                ivFav.setImageResource(newFavState ? R.drawable.heart_fill : R.drawable.heart);
                ivFav.setColorFilter(newFavState ? android.graphics.Color.RED : android.graphics.Color.parseColor("#BDBDBD"));
            });
        }

        if (card != null) card.setOnClickListener(v -> bukaDetailProduk(product));
    }

    private void setupCardRekomendasi(MaterialCardView card, ImageView ivImage, Product product) {
        if (product.getImages() != null && !product.getImages().isEmpty() && ivImage != null) {
            String imageUrl = "http://192.168.18.169:8000/storage/" + product.getImages().get(0).getImage_path();
            Glide.with(this).load(imageUrl).into(ivImage);
        }
        if (card != null) card.setOnClickListener(v -> bukaDetailProduk(product));
    }

    private void bukaDetailProduk(Product product) {
        Intent intent = new Intent(MainActivity.this, ProfilBarangActivity.class);
        intent.putExtra("PRODUCT", product);
        startActivity(intent);
    }

    private void keDaftarBarang(String namaKategori, int id) {
        Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
        intent.putExtra("NAMA_KATEGORI", namaKategori);
        intent.putExtra("CATEGORY_ID", id);
        startActivity(intent);
    }

    private void pindahHalaman(Class<?> targetActivity, boolean tanpaAnimasi) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        startActivity(intent);
        if (tanpaAnimasi) overridePendingTransition(0, 0);
    }
}
