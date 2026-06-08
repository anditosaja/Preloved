package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Import View aman
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

        // --- INTERAKSI PILIHAN MENU KATEGORI ATAS (Sesuai ID XML Asli Kamu) ---
        LinearLayout katHalamanPakaian = findViewById(R.id.katHalamanPakaian);
        LinearLayout katHalamanSepatu = findViewById(R.id.katHalamanSepatu);
        LinearLayout katHalamanTas = findViewById(R.id.katHalamanTas);

        if (katHalamanPakaian != null) {
            katHalamanPakaian.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Pakaian");
                intent.putExtra("CATEGORY_ID", 1); // ID Kategori Pakaian di database MySQL
                startActivity(intent);
            });
        }

        if (katHalamanSepatu != null) {
            katHalamanSepatu.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Sepatu");
                intent.putExtra("CATEGORY_ID", 2); // ID Kategori Sepatu di database MySQL
                startActivity(intent);
            });
        }

        if (katHalamanTas != null) {
            katHalamanTas.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Tas");
                intent.putExtra("CATEGORY_ID", 3); // ID Kategori Tas di database MySQL
                startActivity(intent);
            });
        }

        // ====================================================================
        // BOTTOM NAVIGATION BAR (Sesuai ID Baru yang Ditambahkan di XML)
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
}
