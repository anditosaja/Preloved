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

public class JualKategoriActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RadioGroup rgKategori;
    private MaterialButton btnLanjutkan;

    private String passedImageUri;
    private ProductRequest requestData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jual_kategori);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Tangkap Data dari Step 2
        if (getIntent() != null) {
            passedImageUri = getIntent().getStringExtra("IMAGE_URI");
            requestData = (ProductRequest) getIntent().getSerializableExtra("PRODUCT_DATA");
        }

        btnBack = findViewById(R.id.btnBack);
        rgKategori = findViewById(R.id.rgKategori);
        btnLanjutkan = findViewById(R.id.btnLanjutkan);

        btnLanjutkan.setOnClickListener(v -> {
            int selectedId = rgKategori.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Silakan pilih kategori barang!", Toast.LENGTH_SHORT).show();
                return;
            }

            int categoryId = 1; // Default Pakaian

            if (selectedId == R.id.rbTas) categoryId = 2;
            else if (selectedId == R.id.rbSepatu) categoryId = 3;
            else if (selectedId == R.id.rbElektronik) categoryId = 4;
            else if (selectedId == R.id.rbBuku) categoryId = 5;
            else if (selectedId == R.id.rbHobi) categoryId = 6;
            else if (selectedId == R.id.rbKecantikan) categoryId = 7;
            else if (selectedId == R.id.rbRumah) categoryId = 8;
            else if (selectedId == R.id.rbLainnya) categoryId = 9;

            requestData.setCategoryId(categoryId);

            Intent intent = new Intent(this, JualKondisiActivity.class);
            intent.putExtra("IMAGE_URI", passedImageUri);
            intent.putExtra("PRODUCT_DATA", requestData);
            startActivity(intent);
        });
    }
}
