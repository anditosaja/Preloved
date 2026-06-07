package com.example.preloved;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // ====================================================================
        // TOMBOL BACK (Tambahan Baru)
        // ====================================================================
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Menutup halaman profil saat ini dan otomatis balik ke Beranda
                finish();
                // Menghilangkan animasi jeda biar transisinya mulus lancar jaya
                overridePendingTransition(0, 0);
            }
        });

        // Aksi klik untuk menu Barang Saya
        findViewById(R.id.ic1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfilActivity.this, "Membuka Barang Saya", Toast.LENGTH_SHORT).show();
            }
        });
    }
}