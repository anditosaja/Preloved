package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    }

    // --- FUNGSI BANTUAN UNTUK MELEMPAR DATA KE DAFTAR BARANG ---
    private void bukaDaftarBarang(String namaKategori, int categoryId) {
        Intent intent = new Intent(KategoriActivity.this, DaftarBarangActivity.class);
        intent.putExtra("NAMA_KATEGORI", namaKategori);
        intent.putExtra("CATEGORY_ID", categoryId);
        startActivity(intent);
    }
}
