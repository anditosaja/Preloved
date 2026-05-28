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

import com.google.android.material.card.MaterialCardView;

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
        if (tvLihatSemuaKategori != null) {
            tvLihatSemuaKategori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, KategoriActivity.class);
                    startActivity(intent);
                }
            });
        }

        LinearLayout navKategori = findViewById(R.id.navKategori);
        if (navKategori != null) {
            navKategori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, KategoriActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }

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

        // --- LOGIKA KLIK CARD TRENDING KE PROFIL BARANG ---
        MaterialCardView cardTrending1 = findViewById(R.id.cardTrending1);
        MaterialCardView cardTrending2 = findViewById(R.id.cardTrending2);

        // Klik Zaro Cargo Shirt
        if (cardTrending1 != null) {
            cardTrending1.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfilBarangActivity.class);
                intent.putExtra("PROD_NAME", "Zaro Cargo Shirt");
                intent.putExtra("PROD_PRICE", "Rp150.000");
                intent.putExtra("PROD_OLD_PRICE", "Rp300.000");
                intent.putExtra("PROD_DISCOUNT", "-50%");
                intent.putExtra("PROD_LOCATION", "Kota Depok");
                intent.putExtra("PROD_CONDITION", "Baik");
                intent.putExtra("PROD_IMAGE", R.drawable.zarocargo_shirt);
                startActivity(intent);
            });
        }

        // Klik POSH Model Boots
        if (cardTrending2 != null) {
            cardTrending2.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfilBarangActivity.class);
                intent.putExtra("PROD_NAME", "POSH Model Boots");
                intent.putExtra("PROD_PRICE", "Rp500.000");
                intent.putExtra("PROD_OLD_PRICE", "Rp1.000.000");
                intent.putExtra("PROD_DISCOUNT", "-50%");
                intent.putExtra("PROD_LOCATION", "Jakarta Selatan");
                intent.putExtra("PROD_CONDITION", "Sangat Baik");
                intent.putExtra("PROD_IMAGE", R.drawable.poshmodel_boots);
                startActivity(intent);
            });
        }

        // --- LOGIKA BOTTOM NAV CHAT ---
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