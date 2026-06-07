package com.example.preloved;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    private TextView tvMinPrice, tvMaxPrice;
    private RangeSlider rangeSliderHarga;
    private ChipGroup chipGroupKondisi;
    private TextView btnReset;
    private MaterialButton btnTerapkanFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Deklarasi Komponen
        tvMinPrice = findViewById(R.id.tvMinPrice);
        tvMaxPrice = findViewById(R.id.tvMaxPrice);
        rangeSliderHarga = findViewById(R.id.rangeSliderHarga);
        chipGroupKondisi = findViewById(R.id.chipGroupKondisi);
        btnReset = findViewById(R.id.btnReset);
        btnTerapkanFilter = findViewById(R.id.btnTerapkanFilter);

        // 1. Tombol Kembali
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 2. Set Nilai Awal untuk Slider Harga
        rangeSliderHarga.setValues(0f, 5000000f);

        // Listener saat Bar Harga ditarik
        rangeSliderHarga.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                List<Float> values = slider.getValues();
                float minPrice = values.get(0);
                float maxPrice = values.get(1);

                // Format angka jadi mata uang Rupiah
                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

                // Hilangkan simbol 'Rp' bawaan dan koma dua nol di belakang agar pas
                String strMin = formatRupiah.format(minPrice).replace(",00", "").replace("Rp", "Rp");
                String strMax = formatRupiah.format(maxPrice).replace(",00", "").replace("Rp", "Rp");

                tvMinPrice.setText(strMin);
                tvMaxPrice.setText(strMax);
            }
        });

        // 3. Tombol Reset Ditekan
        btnReset.setOnClickListener(v -> {
            // Kembalikan slider ke awal
            rangeSliderHarga.setValues(0f, 5000000f);

            // Kembalikan opsi Kondisi ke "Semua"
            Chip chipSemua = findViewById(R.id.chipSemua);
            chipSemua.setChecked(true);

            Toast.makeText(FilterActivity.this, "Filter direset", Toast.LENGTH_SHORT).show();
        });

        // 4. Tombol Terapkan Ditekan
        btnTerapkanFilter.setOnClickListener(v -> {
            // Di sini nanti kamu bisa mengatur fungsi untuk mengirim filter ke server API
            Toast.makeText(FilterActivity.this, "Filter Diterapkan!", Toast.LENGTH_SHORT).show();
            finish(); // Tutup halaman filter setelah diterapkan
        });
    }
}