package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class DaftarBarangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daftar_barang);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Deklarasi Komponen
        TextView tvTitleKategori = findViewById(R.id.tvTitleKategori);
        ImageView btnBack = findViewById(R.id.btnBack);
        MaterialCardView cardDummyBaju = findViewById(R.id.cardDummyBaju);
        MaterialCardView cardDummyBaju2 = findViewById(R.id.cardDummyBaju2);
        MaterialCardView cardDummyBaju3 = findViewById(R.id.cardDummyBaju3);
        MaterialCardView cardDummySepatu = findViewById(R.id.cardDummySepatu);
        TextView tvEmptyState = findViewById(R.id.tvEmptyState);

        // Fungsi Tombol Kembali
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Tangkap data nama kategori dari KategoriActivity
        String namaKategori = getIntent().getStringExtra("NAMA_KATEGORI");

        if (namaKategori != null) {
            tvTitleKategori.setText(namaKategori);

            // Logika filter visual item dummy
            if (namaKategori.equals("Pakaian")) {
                cardDummyBaju.setVisibility(View.VISIBLE);
                cardDummyBaju2.setVisibility(View.VISIBLE);
                cardDummyBaju3.setVisibility(View.VISIBLE);
                cardDummySepatu.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.GONE);
            } else if (namaKategori.equals("Sepatu")) {
                cardDummyBaju.setVisibility(View.GONE);
                cardDummyBaju2.setVisibility(View.GONE);
                cardDummyBaju3.setVisibility(View.GONE);
                cardDummySepatu.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
            } else {
                cardDummyBaju.setVisibility(View.GONE);
                cardDummyBaju2.setVisibility(View.GONE);
                cardDummyBaju3.setVisibility(View.GONE);
                cardDummySepatu.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        }

        // Klik baju 1 masuk ke Detail Produk
        cardDummyBaju.setOnClickListener(v -> {
            Intent intent = new Intent(DaftarBarangActivity.this, ProfilBarangActivity.class);
            intent.putExtra("PROD_NAME", "Zaro Cargo Shirt");
            intent.putExtra("PROD_PRICE", "Rp150.000");
            intent.putExtra("PROD_OLD_PRICE", "Rp300.000");
            intent.putExtra("PROD_DISCOUNT", "-50%");
            intent.putExtra("PROD_LOCATION", "Kota Depok");
            intent.putExtra("PROD_CONDITION", "Baik");
            intent.putExtra("PROD_IMAGE", R.drawable.zarocargo_shirt);
            intent.putExtra("PROD_CATEGORY", "Pakaian");
            startActivity(intent);
        });

        // Klik baju 2 masuk ke Detail Produk
        cardDummyBaju2.setOnClickListener(v -> {
            Intent intent = new Intent(DaftarBarangActivity.this, ProfilBarangActivity.class);
            intent.putExtra("PROD_NAME", "Flannel Casual Shirt");
            intent.putExtra("PROD_PRICE", "Rp120.000");
            intent.putExtra("PROD_OLD_PRICE", "Rp250.000");
            intent.putExtra("PROD_DISCOUNT", "-52%");
            intent.putExtra("PROD_LOCATION", "Kota Bekasi");
            intent.putExtra("PROD_CONDITION", "Sangat Baik");
            intent.putExtra("PROD_IMAGE", R.drawable.flannel);
            intent.putExtra("PROD_CATEGORY", "Pakaian");
            startActivity(intent);
        });

        // Klik baju 3 masuk ke Detail Produk
        cardDummyBaju3.setOnClickListener(v -> {
            Intent intent = new Intent(DaftarBarangActivity.this, ProfilBarangActivity.class);
            intent.putExtra("PROD_NAME", "Oversized Streetwear Hoodie");
            intent.putExtra("PROD_PRICE", "Rp185.000");
            intent.putExtra("PROD_OLD_PRICE", "Rp350.000");
            intent.putExtra("PROD_DISCOUNT", "-47%");
            intent.putExtra("PROD_LOCATION", "Jakarta Timur");
            intent.putExtra("PROD_CONDITION", "Baik");
            intent.putExtra("PROD_IMAGE", R.drawable.hoodie);
            intent.putExtra("PROD_CATEGORY", "Pakaian");
            startActivity(intent);
        });

        // Klik sepatu masuk ke Detail Produk
        cardDummySepatu.setOnClickListener(v -> {
            Intent intent = new Intent(DaftarBarangActivity.this, ProfilBarangActivity.class);
            intent.putExtra("PROD_NAME", "POSH Model Boots");
            intent.putExtra("PROD_PRICE", "Rp500.000");
            intent.putExtra("PROD_OLD_PRICE", "Rp1.000.000");
            intent.putExtra("PROD_DISCOUNT", "-50%");
            intent.putExtra("PROD_LOCATION", "Jakarta Selatan");
            intent.putExtra("PROD_CONDITION", "Sangat Baik");
            intent.putExtra("PROD_IMAGE", R.drawable.poshmodel_boots);
            intent.putExtra("PROD_CATEGORY", "Sepatu");
            startActivity(intent);
        });
    }
}