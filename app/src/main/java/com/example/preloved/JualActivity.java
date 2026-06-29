package com.example.preloved;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class JualActivity extends AppCompatActivity {

    private ImageView btnBack;
    private LinearLayout btnPilihFotoArea;
    private MaterialButton btnLanjutkan;

    // Komponen Preview Gambar
    private RelativeLayout containerPreview1;
    private ImageView imgPreview1;
    private ImageView btnRemove1;

    // Variabel untuk menyimpan URI gambar yang dipilih
    private Uri selectedImageUri = null;

    // Launcher untuk membuka Galeri HP
    private final ActivityResultLauncher<String> imagePickerLauncher =
        registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                // Tampilkan gambar ke layar
                imgPreview1.setImageURI(uri);
                containerPreview1.setVisibility(View.VISIBLE);
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jual);

        // Menyesuaikan padding atas dan bawah agar tidak tertutup bar status HP
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inisialisasi ID
        btnBack = findViewById(R.id.btnBack);
        btnPilihFotoArea = findViewById(R.id.btnPilihFotoArea);
        btnLanjutkan = findViewById(R.id.btnLanjutkan);
        containerPreview1 = findViewById(R.id.containerPreview1);
        imgPreview1 = findViewById(R.id.imgPreview1);
        btnRemove1 = findViewById(R.id.btnRemove1);

        // Aksi Tombol Kembali
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Aksi Klik Area Dashed untuk Pilih Foto
        btnPilihFotoArea.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        // Aksi Tombol Silang pada Preview Gambar
        btnRemove1.setOnClickListener(v -> {
            selectedImageUri = null; // Hapus data memori
            imgPreview1.setImageURI(null); // Kosongkan tampilan
            containerPreview1.setVisibility(View.GONE); // Sembunyikan box
        });

        // Aksi Tombol Lanjutkan
        btnLanjutkan.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                Toast.makeText(JualActivity.this, "Pilih foto barang terlebih dahulu!", Toast.LENGTH_SHORT).show();
            } else {
                // Pindah ke Halaman Step 2 (Misalnya: JualDetailActivity)
                // Membawa data gambar (URI) ke halaman selanjutnya
                Intent intent = new Intent(JualActivity.this, JualDetailActivity.class);
                intent.putExtra("IMAGE_URI", selectedImageUri.toString());
                startActivity(intent);
            }
        });
    }
}
