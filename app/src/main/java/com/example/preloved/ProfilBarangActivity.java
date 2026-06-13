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

import java.text.DecimalFormat;

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

        ImageView btnBackImg = findViewById(R.id.btnBackImg);
        if (btnBackImg != null) {
            btnBackImg.setOnClickListener(v -> finish());
        }

        // Deklarasi Komponen UI
        ImageView imgProduct = findViewById(R.id.imgProduct);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductCategory = findViewById(R.id.tvProductCategory);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvOldPrice = findViewById(R.id.tvOldPrice);
        TextView tvDiscount = findViewById(R.id.tvDiscount);
        TextView tvCondition = findViewById(R.id.tvCondition);
        TextView tvProductLocation = findViewById(R.id.tvProductLocation);
        TextView tvDescription = findViewById(R.id.tvDescription);

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

            Product product = (Product) getIntent().getSerializableExtra("PRODUCT");

            if (product != null) {
                String name = product.getNama_barang();

                tvProductName.setText(product.getNama_barang());
                tvCondition.setText(product.getKondisi());
                tvProductLocation.setText(product.getLokasi_kota());
                tvDescription.setText(product.getDeskripsi());

                // Format Harga Jual
                try {
                    double harga = Double.parseDouble(product.getHarga_jual());
                    tvProductPrice.setText("Rp " + new DecimalFormat("#,###").format(harga));
                } catch (Exception e) {
                    tvProductPrice.setText("Rp " + product.getHarga_jual());
                }

                // Format Harga Asli (Dicoret)
                if (product.getHarga_asli() != null && !product.getHarga_asli().isEmpty()) {
                    try {
                        double hargaAsli = Double.parseDouble(product.getHarga_asli());
                        tvOldPrice.setText("Rp " + new DecimalFormat("#,###").format(hargaAsli));
                    } catch (Exception e) {
                        tvOldPrice.setText("Rp " + product.getHarga_asli());
                    }
                    tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tvOldPrice.setVisibility(View.GONE);
                }

                String category = "Produk";
                if (name != null) {
                    if (name.toLowerCase().contains("shirt")
                        || name.toLowerCase().contains("hoodie")
                        || name.toLowerCase().contains("flannel")) {
                        category = "Pakaian";
                    }
                }
                tvProductCategory.setText("Kategori: " + category);

                // Foto Produk
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    String imagePath = product.getImages().get(0).getImage_url();
                    String imageUrl = imagePath.startsWith("http") ? imagePath : "http://10.255.149.23:8000/storage/" + imagePath;

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(imgProduct);
                }

                // Barang Serupa (Statis)
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
}
