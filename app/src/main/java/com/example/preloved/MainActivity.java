package com.example.preloved;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main); // Menyambungkan dengan XML Beranda

            // Mencari elemen berdasarkan ID
            TextView tvLihatSemua = findViewById(R.id.tvLihatSemua);

            // Menambahkan interaksi klik
            tvLihatSemua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Berpindah ke halaman Kategori
                    Intent intent = new Intent(MainActivity.this, KategoriActivity.class);
                    startActivity(intent);
                }
            });
        }
    }