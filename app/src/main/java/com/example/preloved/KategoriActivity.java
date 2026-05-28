package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class KategoriActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kategori);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        LinearLayout navBeranda = findViewById(R.id.navBeranda);
        if (navBeranda != null) {
            navBeranda.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }

        // --- FITUR LOGIKA KLIK KATEGORI ---
        LinearLayout katHalamanPakaian = findViewById(R.id.katHalamanPakaian);
        LinearLayout katHalamanSepatu = findViewById(R.id.katHalamanSepatu);
        LinearLayout katHalamanTas = findViewById(R.id.katHalamanTas);

        if (katHalamanPakaian != null) {
            katHalamanPakaian.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Pakaian");
                startActivity(intent);
            });
        }

        if (katHalamanSepatu != null) {
            katHalamanSepatu.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Sepatu");
                startActivity(intent);
            });
        }

        if (katHalamanTas != null) {
            katHalamanTas.setOnClickListener(v -> {
                Intent intent = new Intent(KategoriActivity.this, DaftarBarangActivity.class);
                intent.putExtra("NAMA_KATEGORI", "Tas");
                startActivity(intent);
            });
        }
    }
}