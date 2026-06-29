package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.preloved.models.ProductRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class JualDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextInputEditText etNamaBarang, etDeskripsi, etHargaJual, etHargaAsli, etLokasiKota, etMerek, etWarna;
    private MaterialButton btnLanjutkan;

    private String passedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jual_detail);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Tangkap URI Gambar dari Step 1
        if (getIntent() != null && getIntent().hasExtra("IMAGE_URI")) {
            passedImageUri = getIntent().getStringExtra("IMAGE_URI");
        } else {
            Toast.makeText(this, "Akses Ilegal: Foto belum dipilih!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inisialisasiUI();

        btnBack.setOnClickListener(v -> finish());

        btnLanjutkan.setOnClickListener(v -> validasiDanLanjutkan());
    }

    private void inisialisasiUI() {
        btnBack = findViewById(R.id.btnBack);
        etNamaBarang = findViewById(R.id.etNamaBarang);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etHargaJual = findViewById(R.id.etHargaJual);
        etHargaAsli = findViewById(R.id.etHargaAsli);
        etLokasiKota = findViewById(R.id.etLokasiKota);
        etMerek = findViewById(R.id.etMerek);
        etWarna = findViewById(R.id.etWarna);
        btnLanjutkan = findViewById(R.id.btnLanjutkan);
    }

    private void validasiDanLanjutkan() {
        String nama = etNamaBarang.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();
        String hJualStr = etHargaJual.getText().toString().trim();
        String hAsliStr = etHargaAsli.getText().toString().trim();
        String lokasi = etLokasiKota.getText().toString().trim();
        String merek = etMerek.getText().toString().trim();
        String warna = etWarna.getText().toString().trim();

        if (nama.isEmpty() || deskripsi.isEmpty() || hJualStr.isEmpty() || lokasi.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi field yang bertanda bintang (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        double hJual = Double.parseDouble(hJualStr);
        Double hAsli = hAsliStr.isEmpty() ? null : Double.parseDouble(hAsliStr);

        // Buat Koper Data (ProductRequest)
        ProductRequest requestData = new ProductRequest();
        requestData.setNamaBarang(nama);
        requestData.setDeskripsi(deskripsi);
        requestData.setHargaJual(hJual);
        requestData.setHargaAsli(hAsli);
        requestData.setLokasiKota(lokasi);
        requestData.setMerek(merek);
        requestData.setWarna(warna);

        // Oper URI Gambar & Koper Data ke Step 3 (Kategori)
        Intent intent = new Intent(this, JualKategoriActivity.class);
        intent.putExtra("IMAGE_URI", passedImageUri);
        intent.putExtra("PRODUCT_DATA", requestData);
        startActivity(intent);
    }
}
