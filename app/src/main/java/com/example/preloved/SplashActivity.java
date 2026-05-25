package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Menjalankan fungsi delay untuk Splash Screen
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Berpindah ke halaman Login setelah waktu habis
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);

                // Menutup SplashActivity agar pengguna tidak bisa kembali ke layar ini saat menekan tombol back
                finish();
            }
        }, 2500); // 2500 = 2,5 detik
    }
}