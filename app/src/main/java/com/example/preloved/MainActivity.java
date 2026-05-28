package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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

        TextView tvLihatSemuaKategori = findViewById(R.id.tvLihatSemuaKategori);
        tvLihatSemuaKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KategoriActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout navKategori = findViewById(R.id.navKategori);
        navKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KategoriActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        // ====================================================================
        // TOMBOL NAVIGASI PROFIL (Tambahan Baru)
        // ====================================================================
        LinearLayout btnNavProfil = findViewById(R.id.btn_nav_profil);
        btnNavProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfilActivity.class);
                startActivity(intent);
                // Menghilangkan animasi jeda bawaan Android biar transisinya mulus seperti kategori
                overridePendingTransition(0, 0);
            }
        });
    }
}