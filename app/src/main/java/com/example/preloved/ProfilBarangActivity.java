package com.example.preloved;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide; // Pastikan ini di-import
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

        // TANGKAP DATA INTENT EXTRA DARI HALAMAN SEBELUMNYA
        if (getIntent() != null && getIntent().hasExtra("PROD_NAME")) {
            String name = getIntent().getStringExtra("PROD_NAME");
            String price = getIntent().getStringExtra("PROD_PRICE");
            String oldPrice = getIntent().getStringExtra("PROD_OLD_PRICE");
            String discount = getIntent().getStringExtra("PROD_DISCOUNT");
            String location = getIntent().getStringExtra("PROD_LOCATION");
            String condition = getIntent().getStringExtra("PROD_CONDITION");
            String category = getIntent().getStringExtra("PROD_CATEGORY");
            String description = getIntent().getStringExtra("PROD_DESCRIPTION");
            String imagePath = getIntent().getStringExtra("PROD_IMAGE_PATH"); // Gambar dari DB
            int imageRes = getIntent().getIntExtra("PROD_IMAGE", 0); // Gambar lokal dummy

            // Pengaman data kategori otomatis jika dibuka langsung dari MainActivity
            if (category == null || category.isEmpty()) {
                if (name != null && (name.contains("Shirt") || name.contains("Hoodie"))) {
                    category = "Pakaian";
                } else {
                    category = "Sepatu";
                }
            }

            // Set Data Detail Utama
            if (tvProductName != null) tvProductName.setText(name);
            if (tvProductCategory != null) tvProductCategory.setText("Kategori: " + category);
            if (tvProductPrice != null) tvProductPrice.setText(price);
            if (tvOldPrice != null) tvOldPrice.setText(oldPrice != null ? oldPrice : "");
            if (tvDiscount != null) tvDiscount.setText(discount != null ? discount : "");
            if (tvProductLocation != null) tvProductLocation.setText(location);
            if (tvCondition != null) tvCondition.setText(condition);

            // Logika Muat Gambar: Prioritaskan gambar dari Database (Glide), jika kosong pakai gambar Dummy
            if (imagePath != null && !imagePath.isEmpty() && imgProduct != null) {
                String fullUrl = imagePath;
                if (!fullUrl.startsWith("http")) {
                    fullUrl = "http://10.0.2.2:8000/storage/" + imagePath;
                }
                Glide.with(this).load(fullUrl).into(imgProduct);
            } else if (imgProduct != null && imageRes != 0) {
                imgProduct.setImageResource(imageRes);
            }

            // Set Deskripsi dari DB, atau pakai fallback
            if (tvDescription != null) {
                if (description != null && !description.isEmpty()) {
                    tvDescription.setText(description);
                } else {
                    tvDescription.setText("Produk preloved " + name + " berkualitas premium. Kondisi terawat dengan tingkat kemulusan " + condition + ". Sangat cocok digunakan untuk menunjang penampilan kasual sehari-hari.");
                }
            }

            // --- LOGIKA SINKRONISASI BARANG SERUPA (ANTI-DUPLIKAT) ---
            if (name != null) {
                if (name.equals("Zaro Cargo Shirt")) {
                    if (imgSerupa1 != null) imgSerupa1.setImageResource(R.drawable.flannel);
                    if (tvNamaSerupa1 != null) tvNamaSerupa1.setText("Flannel Casual");
                    if (tvHargaSerupa1 != null) tvHargaSerupa1.setText("Rp120.000");

                    if (imgSerupa2 != null) imgSerupa2.setImageResource(R.drawable.hoodie);
                    if (tvNamaSerupa2 != null) tvNamaSerupa2.setText("Streetwear Hoodie");
                    if (tvHargaSerupa2 != null) tvHargaSerupa2.setText("Rp185.000");

                    if (cardSerupa1 != null) cardSerupa1.setVisibility(View.VISIBLE);
                    if (cardSerupa2 != null) cardSerupa2.setVisibility(View.VISIBLE);
                    if (tvTitleBarangSerupa != null) tvTitleBarangSerupa.setVisibility(View.VISIBLE);

                } else if (name.equals("Flannel Casual Shirt")) {
                    if (imgSerupa1 != null) imgSerupa1.setImageResource(R.drawable.zarocargo_shirt);
                    if (tvNamaSerupa1 != null) tvNamaSerupa1.setText("Zaro Cargo");
                    if (tvHargaSerupa1 != null) tvHargaSerupa1.setText("Rp150.000");

                    if (imgSerupa2 != null) imgSerupa2.setImageResource(R.drawable.hoodie);
                    if (tvNamaSerupa2 != null) tvNamaSerupa2.setText("Streetwear Hoodie");
                    if (tvHargaSerupa2 != null) tvHargaSerupa2.setText("Rp185.000");

                    if (cardSerupa1 != null) cardSerupa1.setVisibility(View.VISIBLE);
                    if (cardSerupa2 != null) cardSerupa2.setVisibility(View.VISIBLE);
                    if (tvTitleBarangSerupa != null) tvTitleBarangSerupa.setVisibility(View.VISIBLE);

                } else if (name.equals("Oversized Streetwear Hoodie")) {
                    if (imgSerupa1 != null) imgSerupa1.setImageResource(R.drawable.zarocargo_shirt);
                    if (tvNamaSerupa1 != null) tvNamaSerupa1.setText("Zaro Cargo");
                    if (tvHargaSerupa1 != null) tvHargaSerupa1.setText("Rp150.000");

                    if (imgSerupa2 != null) imgSerupa2.setImageResource(R.drawable.flannel);
                    if (tvNamaSerupa2 != null) tvNamaSerupa2.setText("Flannel Casual");
                    if (tvHargaSerupa2 != null) tvHargaSerupa2.setText("Rp120.000");

                    if (cardSerupa1 != null) cardSerupa1.setVisibility(View.VISIBLE);
                    if (cardSerupa2 != null) cardSerupa2.setVisibility(View.VISIBLE);
                    if (tvTitleBarangSerupa != null) tvTitleBarangSerupa.setVisibility(View.VISIBLE);

                } else {
                    if (cardSerupa1 != null) cardSerupa1.setVisibility(View.GONE);
                    if (cardSerupa2 != null) cardSerupa2.setVisibility(View.GONE);
                    if (tvTitleBarangSerupa != null) tvTitleBarangSerupa.setVisibility(View.GONE);
                }
            }
        }

        // FUNGSI CORET HARGA (Strikethrough)
        if (tvOldPrice != null) {
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Interaksi Tombol Aksi Bawah
        View btnBeli = findViewById(R.id.btnBeli);
        if (btnBeli != null) {
            btnBeli.setOnClickListener(v -> Toast.makeText(this, "Membuka halaman Pembayaran...", Toast.LENGTH_SHORT).show());
        }

        View btnChat = findViewById(R.id.btnChat);
        if (btnChat != null) {
            btnChat.setOnClickListener(v -> Toast.makeText(this, "Membuka ruang Chat...", Toast.LENGTH_SHORT).show());
        }
    }
}
