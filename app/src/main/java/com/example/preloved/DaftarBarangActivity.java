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
                cardDummySepatu.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.GONE);
            } else if (namaKategori.equals("Sepatu")) {
                cardDummyBaju.setVisibility(View.GONE);
                cardDummySepatu.setVisibility(View.VISIBLE);
                tvEmptyState.setVisibility(View.GONE);
            } else {
                cardDummyBaju.setVisibility(View.GONE);
                cardDummySepatu.setVisibility(View.GONE);
                tvEmptyState.setVisibility(View.VISIBLE);
            }
        }

        // Klik baju masuk ke Detail Produk dengan membawa data dinamis
        cardDummyBaju.setOnClickListener(v -> {
            Intent intent = new Intent(DaftarBarangActivity.this, ProfilBarangActivity.class);
            intent.putExtra("PROD_NAME", "Zaro Cargo Shirt");
            intent.putExtra("PROD_PRICE", "Rp150.000");
            intent.putExtra("PROD_OLD_PRICE", "Rp300.000");
            intent.putExtra("PROD_DISCOUNT", "-50%");
            intent.putExtra("PROD_LOCATION", "Kota Depok");
            intent.putExtra("PROD_CONDITION", "Baik");
            intent.putExtra("PROD_IMAGE", R.drawable.zarocargo_shirt);
            startActivity(intent);
        });

        // Klik sepatu masuk ke Detail Produk dengan membawa data dinamis
        cardDummySepatu.setOnClickListener(v -> {
            Intent intent = new Intent(DaftarBarangActivity.this, ProfilBarangActivity.class);
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
}