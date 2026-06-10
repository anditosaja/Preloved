package com.example.preloved;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.preloved.models.Product;
import com.example.preloved.models.ProductImage;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class ProfilBarangActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profil_barang);

        // Pengaturan padding agar tidak menabrak bar status HP
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Fungsionalitas Tombol Kembali pada gambar
        ImageView btnBackImg = findViewById(R.id.btnBackImg);
        if (btnBackImg != null) {
            btnBackImg.setOnClickListener(v -> finish());
        }

        // Deklarasi Komponen UI Detail Utama
        ImageView imgProduct = findViewById(R.id.imgProduct);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductCategory = findViewById(R.id.tvProductCategory);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvOldPrice = findViewById(R.id.tvOldPrice);
        TextView tvDiscount = findViewById(R.id.tvDiscount);
        TextView tvCondition = findViewById(R.id.tvCondition);
        TextView tvProductLocation = findViewById(R.id.tvProductLocation);
        TextView tvDescription = findViewById(R.id.tvDescription);

        // Deklarasi Komponen UI Section Barang Serupa dengan Pengaman Null
        ImageView imgSerupa1 = findViewById(R.id.imgSerupa1);
        TextView tvNamaSerupa1 = findViewById(R.id.tvNamaSerupa1);
        TextView tvHargaSerupa1 = findViewById(R.id.tvHargaSerupa1);

        ImageView imgSerupa2 = findViewById(R.id.imgSerupa2);
        TextView tvNamaSerupa2 = findViewById(R.id.tvNamaSerupa2);
        TextView tvHargaSerupa2 = findViewById(R.id.tvHargaSerupa2);

        MaterialCardView cardSerupa1 = findViewById(R.id.cardSerupa1);
        MaterialCardView cardSerupa2 = findViewById(R.id.cardSerupa2);
        TextView tvTitleBarangSerupa = findViewById(R.id.tvTitleBarangSerupa);

        // TANGKAP DATA PRODUCT DARI HALAMAN SEBELUMNYA
        if (getIntent() != null && getIntent().hasExtra("PRODUCT")) {

            Product product =
                (Product) getIntent().getSerializableExtra("PRODUCT");

            if (product != null) {

                String name = product.getNama_barang();

                // Data Utama
                tvProductName.setText(product.getNama_barang());
                tvProductPrice.setText("Rp " + product.getHarga_jual());
                tvCondition.setText(product.getKondisi());
                tvProductLocation.setText(product.getLokasi_kota());
                tvDescription.setText(product.getDeskripsi());

                if (product.getHarga_asli() != null &&
                    !product.getHarga_asli().isEmpty()) {

                    tvOldPrice.setText("Rp " + product.getHarga_asli());
                }

                // Kategori
                String category = "Produk";

                if (name != null) {
                    if (name.toLowerCase().contains("shirt")
                        || name.toLowerCase().contains("hoodie")
                        || name.toLowerCase().contains("flannel")) {

                        category = "Pakaian";
                    }
                }

                tvProductCategory.setText("Kategori: " + category);

                // Foto Produk dari Laravel
                if (product.getImages() != null
                    && !product.getImages().isEmpty()) {

                    String imageUrl =
                        "http://172.25.23.211:8000/storage/" +
                            product.getImages().get(0).getImage_path();

                    Glide.with(this)
                        .load(imageUrl)
                        .into(imgProduct);
                }

                // Barang Serupa (sementara)
                if (name != null) {

                    if (name.equalsIgnoreCase("Zaro Cargo Shirt")) {

                        imgSerupa1.setImageResource(R.drawable.flannel);
                        tvNamaSerupa1.setText("Flannel Casual");
                        tvHargaSerupa1.setText("Rp120.000");

                        imgSerupa2.setImageResource(R.drawable.hoodie);
                        tvNamaSerupa2.setText("Streetwear Hoodie");
                        tvHargaSerupa2.setText("Rp185.000");

                    } else if (name.equalsIgnoreCase("Flannel Casual Shirt")) {

                        imgSerupa1.setImageResource(R.drawable.zarocargo_shirt);
                        tvNamaSerupa1.setText("Zaro Cargo");
                        tvHargaSerupa1.setText("Rp150.000");

                        imgSerupa2.setImageResource(R.drawable.hoodie);
                        tvNamaSerupa2.setText("Streetwear Hoodie");
                        tvHargaSerupa2.setText("Rp185.000");

                    } else if (name.equalsIgnoreCase("Oversized Streetwear Hoodie")) {

                        imgSerupa1.setImageResource(R.drawable.zarocargo_shirt);
                        tvNamaSerupa1.setText("Zaro Cargo");
                        tvHargaSerupa1.setText("Rp150.000");

                        imgSerupa2.setImageResource(R.drawable.flannel);
                        tvNamaSerupa2.setText("Flannel Casual");
                        tvHargaSerupa2.setText("Rp120.000");

                    } else {

                        cardSerupa1.setVisibility(View.GONE);
                        cardSerupa2.setVisibility(View.GONE);
                        tvTitleBarangSerupa.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
};
