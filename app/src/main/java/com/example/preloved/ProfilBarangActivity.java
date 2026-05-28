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

        // Deklarasi Komponen UI Dinamis
        ImageView imgProduct = findViewById(R.id.imgProduct);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvOldPrice = findViewById(R.id.tvOldPrice);
        TextView tvDiscount = findViewById(R.id.tvDiscount);
        TextView tvCondition = findViewById(R.id.tvCondition);
        TextView tvProductLocation = findViewById(R.id.tvProductLocation);
        TextView tvDescription = findViewById(R.id.tvDescription);

        // TANGKAP DATA INTENT EXTRA DARI HALAMAN SEBELUMNYA
        if (getIntent() != null && getIntent().hasExtra("PROD_NAME")) {
            String name = getIntent().getStringExtra("PROD_NAME");
            String price = getIntent().getStringExtra("PROD_PRICE");
            String oldPrice = getIntent().getStringExtra("PROD_OLD_PRICE");
            String discount = getIntent().getStringExtra("PROD_DISCOUNT");
            String location = getIntent().getStringExtra("PROD_LOCATION");
            String condition = getIntent().getStringExtra("PROD_CONDITION");
            int imageRes = getIntent().getIntExtra("PROD_IMAGE", 0);

            // Set Data ke Komponen UI
            if (tvProductName != null) tvProductName.setText(name);
            if (tvProductPrice != null) tvProductPrice.setText(price);
            if (tvOldPrice != null) tvOldPrice.setText(oldPrice);
            if (tvDiscount != null) tvDiscount.setText(discount);
            if (tvProductLocation != null) tvProductLocation.setText(location);
            if (tvCondition != null) tvCondition.setText(condition);
            if (imgProduct != null && imageRes != 0) imgProduct.setImageResource(imageRes);

            // Modifikasi Deskripsi dummy menyesuaikan nama barang
            if (tvDescription != null) {
                tvDescription.setText("Produk preloved " + name + " berkualitas premium. Kondisi terawat dengan tingkat kemulusan " + condition + ". Sangat cocok digunakan untuk menunjang penampilan kasual sehari-hari.");
            }
        }

        // FUNGSI CORET HARGA (Strikethrough)
        if (tvOldPrice != null) {
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Contoh interaksi tombol
        findViewById(R.id.btnBeli).setOnClickListener(v -> {
            Toast.makeText(this, "Membuka halaman Pembayaran...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnChat).setOnClickListener(v -> {
            Toast.makeText(this, "Membuka ruang Chat...", Toast.LENGTH_SHORT).show();
        });
    }
}