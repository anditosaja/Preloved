package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.preloved.models.Product;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.network.ApiService;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarBarangActivity extends AppCompatActivity {

    private TextView tvTitleToolbar;
    private int categoryId;
    private String namaKategori;

    // Deklarasi Komponen UI Card Sesuai XML di atas
    private MaterialCardView cardItem1, cardItem2;
    private TextView tvItemName1, tvItemPrice1, tvItemLoc1;
    private TextView tvItemName2, tvItemPrice2, tvItemLoc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_barang);

        // 1. Ambil kiriman data filter dari Intent halaman Beranda/Kategori
        namaKategori = getIntent().getStringExtra("NAMA_KATEGORI");
        categoryId = getIntent().getIntExtra("CATEGORY_ID", 0);

        // 2. Pasang judul toolbar sesuai kategori yang dipilih (misal: "Sepatu")
        tvTitleToolbar = findViewById(R.id.tvTitleToolbar);
        if (tvTitleToolbar != null && namaKategori != null) {
            tvTitleToolbar.setText(namaKategori);
        }

        // Fungsi tombol back toolbar
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 3. Inisialisasi ID komponen penampung data
        cardItem1 = findViewById(R.id.cardItem1);
        cardItem2 = findViewById(R.id.cardItem2);

        tvItemName1 = findViewById(R.id.tvItemName1);
        tvItemPrice1 = findViewById(R.id.tvItemPrice1);
        tvItemLoc1 = findViewById(R.id.tvItemLoc1);

        tvItemName2 = findViewById(R.id.tvItemName2);
        tvItemPrice2 = findViewById(R.id.tvItemPrice2);
        tvItemLoc2 = findViewById(R.id.tvItemLoc2);

        // 4. Jalankan request ke API Laravel
        ambilDataTerfilter();
    }

    private void ambilDataTerfilter() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Memanggil URL: http://10.0.2.2:8000/api/products?category_id=ID_YANG_DIKLIK
        Call<List<Product>> call = apiService.getProductsByCategory(categoryId);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> filteredProducts = response.body();

                    if (filteredProducts.isEmpty()) {
                        Toast.makeText(DaftarBarangActivity.this, "Belum ada barang di kategori ini", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tempel data produk index ke-0 ke Card ke-1
                    if (filteredProducts.size() >= 1) {
                        setupCardItem1(filteredProducts.get(0));
                    }
                    // Tempel data produk index ke-1 ke Card ke-2
                    if (filteredProducts.size() >= 2) {
                        setupCardItem2(filteredProducts.get(1));
                    }
                } else {
                    Toast.makeText(DaftarBarangActivity.this, "Gagal memproses filter database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(DaftarBarangActivity.this, "Koneksi Error Backend: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCardItem1(Product product) {
        if (tvItemName1 != null) tvItemName1.setText(product.getNama_barang());
        if (tvItemPrice1 != null) tvItemPrice1.setText("Rp" + product.getHarga_jual());
        if (tvItemLoc1 != null) tvItemLoc1.setText("📍 " + product.getLokasi_kota());

        if (cardItem1 != null) {
            cardItem1.setOnClickListener(v -> openDetail(product));
        }
    }

    private void setupCardItem2(Product product) {
        if (tvItemName2 != null) tvItemName2.setText(product.getNama_barang());
        if (tvItemPrice2 != null) tvItemPrice2.setText("Rp" + product.getHarga_jual());
        if (tvItemLoc2 != null) tvItemLoc2.setText("📍 " + product.getLokasi_kota());

        if (cardItem2 != null) {
            cardItem2.setOnClickListener(v -> openDetail(product));
        }
    }

    // Fungsi klik oper data barang ke halaman detail (ProfilBarangActivity)
    private void openDetail(Product product) {
        Intent intent = new Intent(DaftarBarangActivity.this, ProfilBarangActivity.class);
        intent.putExtra("PROD_NAME", product.getNama_barang());
        intent.putExtra("PROD_PRICE", "Rp" + product.getHarga_jual());
        intent.putExtra("PROD_OLD_PRICE", "Rp" + product.getHarga_asli());
        intent.putExtra("PROD_LOCATION", product.getLokasi_kota());
        intent.putExtra("PROD_CONDITION", product.getKondisi());
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            intent.putExtra("PROD_IMAGE_URL", product.getImages().get(0).getImage_path());
        }
        startActivity(intent);
    }
}
