package com.example.preloved;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preloved.adapters.ProductAdapter;
import com.example.preloved.models.Product;
import com.example.preloved.utils.FavoriteManager;

import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvProducts;
    private TextView tvEmptyState;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Kita daur ulang layout Daftar Barang!
        setContentView(R.layout.activity_daftar_barang);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Atur Header
        TextView tvTitleKategori = findViewById(R.id.tvTitleKategori);
        tvTitleKategori.setText("Favorit Saya");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvProducts = findViewById(R.id.rvProducts);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        loadDataFavorit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh otomatis kalau user barusan hapus favorit dari ProfilBarangActivity
        loadDataFavorit();
    }

    private void loadDataFavorit() {
        FavoriteManager favManager = new FavoriteManager(this);
        List<Product> favList = favManager.getFavorites();

        if (favList.isEmpty()) {
            rvProducts.setVisibility(View.GONE);
            tvEmptyState.setText("Kamu belum memiliki barang favorit.");
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvProducts.setVisibility(View.VISIBLE);

            adapter = new ProductAdapter(favList);
            rvProducts.setAdapter(adapter);
        }
    }
}
