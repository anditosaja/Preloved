package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;

public class SuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_success);

        // Pengaturan padding sistem agar tampilan tidak menabrak status bar
        View mainView = findViewById(android.R.id.content);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // ========================================================
        // MIGRASI KE ON BACK PRESSED DISPATCHER (Cara Modern)
        // ========================================================
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Logika ketika user melakukan gesture back di HP
                keBeranda();
            }
        });

        // Tombol Klik di Layar
        MaterialButton btnKembaliBeranda = findViewById(R.id.btnKembaliBeranda);
        if (btnKembaliBeranda != null) {
            btnKembaliBeranda.setOnClickListener(v -> keBeranda());
        }
    }


    private void keBeranda() {
        Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
        // Bersihkan stack activity agar user tidak bisa 'Back' ke halaman sukses atau orderan lagi
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
