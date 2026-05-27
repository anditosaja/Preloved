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

        // FUNGSI CORET HARGA (Strikethrough)
        TextView tvOldPrice = findViewById(R.id.tvOldPrice);
        if (tvOldPrice != null) {
            // Ini akan memberikan garis tengah horizontal pada tulisan Rp300.000
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Contoh interaksi tombol (bisa dihapus/diganti nantinya)
        findViewById(R.id.btnBeli).setOnClickListener(v -> {
            Toast.makeText(this, "Membuka halaman Pembayaran...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnChat).setOnClickListener(v -> {
            Toast.makeText(this, "Membuka ruang Chat...", Toast.LENGTH_SHORT).show();
        });
    }
}