package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Menyamakan dengan KategoriActivity
        setContentView(R.layout.activity_main);

        // Mengatur padding untuk EdgeToEdge agar tidak tertutup status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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