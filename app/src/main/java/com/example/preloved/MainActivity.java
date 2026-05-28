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

        // --- LOGIKA KLIK KATEGORI HOMEPAGE MENUJU DAFTAR BARANG ---
        LinearLayout btnKatPakaian = findViewById(R.id.btnKatPakaian);
        LinearLayout btnKatSepatu = findViewById(R.id.btnKatSepatu);
        LinearLayout btnKatTas = findViewById(R.id.btnKatTas);

        if (btnKatPakaian != null) {
            btnKatPakaian.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Pakaian");
                startActivity(intent);
            });
        }

        if (btnKatSepatu != null) {
            btnKatSepatu.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Sepatu");
                startActivity(intent);
            });
        }

        if (btnKatTas != null) {
            btnKatTas.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Tas");
                startActivity(intent);
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
    }
}