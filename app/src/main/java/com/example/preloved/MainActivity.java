package com.example.preloved;

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
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // --- DEKLARASI UI TRENDING (Menampilkan Data Produk Terbaru) ---
    private MaterialCardView cardTrending1, cardTrending2;
    private TextView tvProdName1, tvProdPrice1, tvProdLoc1;
    private TextView tvProdName2, tvProdPrice2, tvProdLoc2;
    private ImageView ivTrending1, ivTrending2;

    // --- DEKLARASI UI REKOMENDASI (Menampilkan Data Trending Terbanyak) ---
    private MaterialCardView cardRekomendasi1, cardRekomendasi2, cardRekomendasi3;
    private ImageView ivRekomendasi1, ivRekomendasi2, ivRekomendasi3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ================= WINDOW INSET =================
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // Inisialisasi komponen UI XML ke Java
        inisialisasiUI();

        // Atur interaksi tombol klik navigasi dan kategori
        aturNavigasi();

        // Jalankan trigger penarikan data dari API Laravel
        ambilDataDariLaravel();
    }

    private void inisialisasiUI() {
        // UI Bagian Trending Card
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

        // UI Bagian Rekomendasi Card
        cardRekomendasi1 = findViewById(R.id.cardRekomendasi1);
        cardRekomendasi2 = findViewById(R.id.cardRekomendasi2);
        cardRekomendasi3 = findViewById(R.id.cardRekomendasi3);
        ivRekomendasi1 = findViewById(R.id.ivRekomendasi1);
        ivRekomendasi2 = findViewById(R.id.ivRekomendasi2);
        ivRekomendasi3 = findViewById(R.id.ivRekomendasi3);
    }

    private void aturNavigasi() {
        // ================= KATEGORI TEXT & LIHAT SEMUA =================
        TextView tvLihatSemuaKategori = findViewById(R.id.tvLihatSemuaKategori);
        if (tvLihatSemuaKategori != null) {
            tvLihatSemuaKategori.setOnClickListener(v -> pindahHalaman(KategoriActivity.class, false));
        }

        // ================= KATEGORI GRID BUTTON (SINKRON KE DAFTARBARANG) =================
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

        // ================= BOTTOM NAVIGATION BAR ACTIONS =================
        LinearLayout navBeranda = findViewById(R.id.navBeranda);
        if (navBeranda != null) {
            navBeranda.setOnClickListener(v -> ambilDataDariLaravel()); // Refresh data halaman depan
        }

        LinearLayout navKategori = findViewById(R.id.navKategori);
        if (navKategori != null) {
            navKategori.setOnClickListener(v -> pindahHalaman(KategoriActivity.class, true));
        }

        LinearLayout navJual = findViewById(R.id.navJual);
        if (navJual != null) {
            navJual.setOnClickListener(v -> pindahHalaman(JualActivity.class, false));
        }

        LinearLayout navChat = findViewById(R.id.navChat);
        if (navChat != null) {
            navChat.setOnClickListener(v -> pindahHalaman(ChatActivity.class, true));
        }

        LinearLayout navProfil = findViewById(R.id.navProfil);
        if (navProfil != null) {
            navProfil.setOnClickListener(v -> pindahHalaman(ProfilActivity.class, true));
        }
    }

    private void ambilDataDariLaravel() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<HomeResponse> call = apiService.getTrendingProducts();

        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Ambil array data dari object response Laravel
                    List<Product> dataTerbaru = response.body().getRecommended();
                    List<Product> dataTrending = response.body().getTrending();

                    // SILANG DATA: Produk baru masuk langsung ditampilkan di baris KARTU TRENDING
                    if (dataTerbaru != null && !dataTerbaru.isEmpty()) {
                        if (dataTerbaru.size() >= 1) setupCardTrending(1, dataTerbaru.get(0));
                        if (dataTerbaru.size() >= 2) setupCardTrending(2, dataTerbaru.get(1));
                    }

                    // SILANG DATA: Produk trending lama di database digeser ke baris REKOMENDASI bawah
                    if (dataTrending != null && !dataTrending.isEmpty()) {
                        if (dataTrending.size() >= 1) setupCardRekomendasi(cardRekomendasi1, ivRekomendasi1, dataTrending.get(0));
                        if (dataTrending.size() >= 2) setupCardRekomendasi(cardRekomendasi2, ivRekomendasi2, dataTrending.get(1));
                        if (dataTrending.size() >= 3) setupCardRekomendasi(cardRekomendasi3, ivRekomendasi3, dataTrending.get(2));
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Gagal memproses struktur produk", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal konek API: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

        // Tarik Gambar Pertama Produk dari Server Laravel via Glide
        if (product.getImages() != null && !product.getImages().isEmpty() && ivImage != null) {
            String imageUrl = "http://172.25.23.211:8000/storage/" + product.getImages().get(0).getImage_path();
            Glide.with(this).load(imageUrl).into(ivImage);
        }

        if (card != null) {
            card.setOnClickListener(v -> bukaDetailProduk(product));
        }
    }

    private void setupCardRekomendasi(MaterialCardView card, ImageView ivImage, Product product) {
        if (product.getImages() != null && !product.getImages().isEmpty() && ivImage != null) {
            String imageUrl = "http://172.25.23.211:8000/storage/" + product.getImages().get(0).getImage_path();
            Glide.with(this).load(imageUrl).into(ivImage);
        }

        if (card != null) {
            card.setOnClickListener(v -> bukaDetailProduk(product));
        }
    }

    private void bukaDetailProduk(Product product) {
        Intent intent = new Intent(MainActivity.this, ProfilBarangActivity.class);
        intent.putExtra("PRODUCT", product);
        startActivity(intent);
    }

    // --- FUNGSI PEMBANTU UNTUK MENGOPRASI ROUTE DAN ID FILTER ---
    private void keDaftarBarang(String namaKategori, int id) {
        Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
        intent.putExtra("NAMA_KATEGORI", namaKategori);
        intent.putExtra("CATEGORY_ID", id);
        startActivity(intent);
    }

    private void pindahHalaman(Class<?> targetActivity, boolean tanpaAnimasi) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        startActivity(intent);
        if (tanpaAnimasi) {
            overridePendingTransition(0, 0);
        }
    }
}
