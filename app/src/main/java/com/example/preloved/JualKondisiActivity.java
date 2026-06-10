package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.preloved.models.ProductRequest;
import com.google.android.material.button.MaterialButton;

public class JualKondisiActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RadioGroup rgKondisi;
    private MaterialButton btnLanjutkan;

    private String passedImageUri;
    private ProductRequest requestData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jual_kondisi);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Tangkap Data dari Step 3
        if (getIntent() != null) {
            passedImageUri = getIntent().getStringExtra("IMAGE_URI");
            requestData = (ProductRequest) getIntent().getSerializableExtra("PRODUCT_DATA");
        }

        btnBack = findViewById(R.id.btnBack);
        rgKondisi = findViewById(R.id.rgKondisi);
        btnLanjutkan = findViewById(R.id.btnLanjutkan);

        btnBack.setOnClickListener(v -> finish());

        btnLanjutkan.setOnClickListener(v -> {
            int selectedId = rgKondisi.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Silakan pilih kondisi barang!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Samakan persis dengan ENUM MySQL (huruf kecil semua)
            String kondisi = "baru";
            if (selectedId == R.id.rbSangatBaik) {
                kondisi = "sangat_baik";
            } else if (selectedId == R.id.rbBaik) {
                kondisi = "baik";
            } else if (selectedId == R.id.rbCukup) {
                kondisi = "cukup";
            }

            requestData.setKondisi(kondisi);

            Intent intent = new Intent(this, JualSelesaiActivity.class);
            intent.putExtra("IMAGE_URI", passedImageUri);
            intent.putExtra("PRODUCT_DATA", requestData);
            startActivity(intent);
        });
    }
}
