package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

// Import Model
import com.example.preloved.models.Category;
import com.example.preloved.models.CategoryPopulerResponse;

// Import Network
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KategoriActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kategori);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // --- TOMBOL KEMBALI (BACK) ---
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // --- INTERAKSI PILIHAN MENU KATEGORI (Sesuai ID XML & Database MySQL) ---
        LinearLayout katPakaian = findViewById(R.id.katPakaian);
        LinearLayout katTas = findViewById(R.id.katTas);
        LinearLayout katSepatu = findViewById(R.id.katSepatu);
        LinearLayout katElektronik = findViewById(R.id.katElektronik);
        LinearLayout katBuku = findViewById(R.id.katBuku);
        LinearLayout katHobi = findViewById(R.id.katHobi);
        LinearLayout katKecantikan = findViewById(R.id.katKecantikan);
        LinearLayout katRumah = findViewById(R.id.katRumah);

        if (katPakaian != null) katPakaian.setOnClickListener(v -> bukaDaftarBarang("Pakaian", 1));
        if (katTas != null) katTas.setOnClickListener(v -> bukaDaftarBarang("Tas", 2));
        if (katSepatu != null) katSepatu.setOnClickListener(v -> bukaDaftarBarang("Sepatu", 3));
        if (katElektronik != null) katElektronik.setOnClickListener(v -> bukaDaftarBarang("Elektronik", 4));
        if (katBuku != null) katBuku.setOnClickListener(v -> bukaDaftarBarang("Buku", 5));
        if (katHobi != null) katHobi.setOnClickListener(v -> bukaDaftarBarang("Hobi", 6));
        if (katKecantikan != null) katKecantikan.setOnClickListener(v -> bukaDaftarBarang("Kecantikan", 7));
        if (katRumah != null) katRumah.setOnClickListener(v -> bukaDaftarBarang("Rumah Tangga", 8));

        // ====================================================================
        // BOTTOM NAVIGATION BAR
        // ====================================================================
        LinearLayout navBeranda = findViewById(R.id.navBeranda);
        if (navBeranda != null) {
            navBeranda.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish(); // Tutup KategoriActivity agar kembali bersih ke Beranda
            });
        }

        LinearLayout navKategori = findViewById(R.id.navKategori);
        if (navKategori != null) {
            navKategori.setOnClickListener(v -> {
                // Sedang berada di halaman Kategori, tidak perlu aksi pindah
            });
        }

        LinearLayout navJual = findViewById(R.id.navJual);
        if (navJual != null) {
            navJual.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, JualActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout navChat = findViewById(R.id.navChat);
        if (navChat != null) {
            navChat.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout navProfil = findViewById(R.id.navProfil);
        if (navProfil != null) {
            navProfil.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, ProfilActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        // ====================================================================
        // MEMANGGIL FUNGSI KATEGORI POPULER (RETROFIT) SAAT ACTIVITY DIBUKA
        // ====================================================================
        fetchKategoriPopuler();
    }

    // --- FUNGSI BANTUAN UNTUK MELEMPAR DATA KE DAFTAR BARANG ---
    private void bukaDaftarBarang(String namaKategori, int categoryId) {
        Intent intent = new Intent(KategoriActivity.this, DaftarBarangActivity.class);
        intent.putExtra("NAMA_KATEGORI", namaKategori);
        intent.putExtra("CATEGORY_ID", categoryId);
        startActivity(intent);
    }

    // ====================================================================
    // LOGIKA KATEGORI POPULER (MENGGUNAKAN RETROFIT)
    // ====================================================================

    private void fetchKategoriPopuler() {
        // Menggunakan ApiService dari folder network
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        Call<CategoryPopulerResponse> call = apiService.getKategoriPopuler();
        call.enqueue(new Callback<CategoryPopulerResponse>() {
            @Override
            public void onResponse(Call<CategoryPopulerResponse> call, Response<CategoryPopulerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        // Menggunakan List dengan tipe data Category yang sudah ada di folder models
                        List<Category> listPopuler = response.body().getData();
                        tampilkanKategoriPopuler(listPopuler);
                    }
                } else {
                    Toast.makeText(KategoriActivity.this, "Gagal memuat kategori populer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryPopulerResponse> call, Throwable t) {
                Log.e("KategoriActivity", "Error Retrofit: " + t.getMessage());
                Toast.makeText(KategoriActivity.this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tampilkanKategoriPopuler(List<Category> dataApi) {
        LinearLayout layoutPopuler = findViewById(R.id.layoutKategoriPopuler);

        // Mencegah error jika wadah tidak ditemukan di XML
        if (layoutPopuler == null) return;

        layoutPopuler.removeAllViews(); // Bersihkan wadah sebelum diisi data baru

        try {
            // Looping menggunakan data Category yang didapat dari API
            for (Category kategori : dataApi) {

                // Panggil method getter milik class Category kamu
                int idKategori = kategori.getCategory_id();
                String namaKategori = kategori.getNama_kategori();

                // 1. Ambil template layout item_kategori_populer.xml
                View itemKategori = LayoutInflater.from(this).inflate(R.layout.item_kategori_populer, layoutPopuler, false);

                // 2. Hubungkan ke elemen di dalam item_kategori_populer.xml
                ImageView imgIkon = itemKategori.findViewById(R.id.imgIkonPopuler);
                TextView txtNama = itemKategori.findViewById(R.id.txtNamaPopuler);

                // 3. Set Teks dan Ikon secara dinamis
                txtNama.setText(namaKategori);
                imgIkon.setImageResource(getIkonKategori(namaKategori));

                // 4. Pasang event klik yang melempar data sama seperti kategori biasa
                itemKategori.setOnClickListener(v -> bukaDaftarBarang(namaKategori, idKategori));

                // 5. Tempelkan ke loyang utama (layoutKategoriPopuler)
                layoutPopuler.addView(itemKategori);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Fungsi otomatis untuk mencocokkan nama kategori dengan gambar drawable
    private int getIkonKategori(String nama) {
        if (nama == null) return R.drawable.pakaian; // Mencegah Error NullPointerException

        switch (nama.toLowerCase()) {
            case "pakaian": return R.drawable.pakaian;
            case "tas": return R.drawable.tas;
            case "sepatu": return R.drawable.sepatu;
            case "elektronik": return R.drawable.elektronik;
            case "buku": return R.drawable.elektronik; // Ganti jika punya ikon buku tersendiri di drawable
            case "hobi": return android.R.drawable.ic_menu_camera;
            case "kecantikan": return android.R.drawable.ic_menu_view;
            case "rumah tangga": return android.R.drawable.ic_menu_manage;
            default: return R.drawable.pakaian; // Ikon default
        }
    }
}
