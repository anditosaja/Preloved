package com.example.preloved;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChatActivityDetail extends AppCompatActivity { // Nama class disesuaikan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Pastikan mengarah ke layout detail chat, bukan activity_chat biasa!
        setContentView(R.layout.activity_chat_detail);

        View mainView = findViewById(R.id.main);
        if (mainView == null) {
            // Mengambil root layout jika id @+id/main tidak dipasang di root activity_chat_detail.xml
            mainView = findViewById(android.R.id.content);
        }

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // Khusus detail chat, beri padding bottom agar tidak tertutup navigation bar bawaan HP
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Fungsionalitas Tombol Kembali ke List Chat
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}