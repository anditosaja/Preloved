package com.example.preloved;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.Config;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananSayaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyData;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pesanan_saya);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inisialisasi UI
        ImageView btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewOrders);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyData = findViewById(R.id.tvEmptyData);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getToken();

        loadPesananSaya();
    }

    private void loadPesananSaya() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyData.setVisibility(View.GONE);

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getMyOrders(authHeader).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonResponse);

                        if (jsonArray.length() == 0) {
                            tvEmptyData.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);

                            // Pasang data JSON dinamis ke Adapter Pembeli
                            BuyerOrderAdapter adapter = new BuyerOrderAdapter(jsonArray);
                            recyclerView.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        Log.e("PESANAN_SAYA", "Error parsing JSON", e);
                        Toast.makeText(PesananSayaActivity.this, "Gagal membaca data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PesananSayaActivity.this, "Gagal memuat pesanan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(PesananSayaActivity.this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi konfirmasi pesanan diterima untuk mencairkan saldo ke penjual
    private void eksekusiPesananDiterima(int orderId) {
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.completeOrder(authHeader, orderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PesananSayaActivity.this, "Transaksi Selesai! Dana diteruskan ke penjual.", Toast.LENGTH_SHORT).show();
                    loadPesananSaya(); // Refresh otomatis list pesanan pembeli
                } else {
                    Toast.makeText(PesananSayaActivity.this, "Gagal menyelesaikan pesanan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PesananSayaActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========================================================
    // ADAPTER DINAMIS SISI PEMBELI (PESANAN SAYA)
    // ========================================================
    private class BuyerOrderAdapter extends RecyclerView.Adapter<BuyerOrderAdapter.ViewHolder> {
        private final JSONArray ordersArray;

        public BuyerOrderAdapter(JSONArray ordersArray) {
            this.ordersArray = ordersArray;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            try {
                JSONObject orderObj = ordersArray.getJSONObject(position);
                int orderId = orderObj.getInt("order_id");
                String status = orderObj.optString("status", "pending");
                double hargaFinal = orderObj.optDouble("harga_final", 0);

                JSONObject productObj = orderObj.getJSONObject("product");
                String namaBarang = productObj.optString("nama_barang", "Produk");
                int categoryId = productObj.optInt("category_id", 0);

                // Set teks secara dinamis menggantikan teks mentah XML
                holder.tvProductName.setText(namaBarang);
                holder.tvCategory.setText("Kategori ID: " + categoryId + " | Total: Rp " + new DecimalFormat("#,###").format(hargaFinal));

                // Logika Status Dinamis Sisi Pembeli
                if (status.equalsIgnoreCase("paid")) {
                    holder.tvStatus.setText("Status: Menunggu Penjual Mengirim");
                    holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                    holder.btnAction.setVisibility(View.GONE); // Belum dikirim, tombol sembunyi
                } else if (status.equalsIgnoreCase("shipped")) {
                    holder.tvStatus.setText("Status: Barang Sedang Dikirim");
                    holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#2196F3"));
                    holder.btnAction.setVisibility(View.VISIBLE); // Sudah dikirim, tombol muncul
                    holder.btnAction.setText("Pesanan Diterima");
                    holder.btnAction.setOnClickListener(v -> eksekusiPesananDiterima(orderId));
                } else if (status.equalsIgnoreCase("completed")) {
                    holder.tvStatus.setText("Status: Transaksi Selesai");
                    holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                    holder.btnAction.setVisibility(View.GONE); // Selesai, tombol hilang
                } else {
                    holder.tvStatus.setText("Status: " + status.toUpperCase());
                    holder.btnAction.setVisibility(View.GONE);
                }

                // Load Gambar Dinamis
                JSONArray imagesArray = productObj.optJSONArray("images");
                if (imagesArray != null && imagesArray.length() > 0) {
                    JSONObject imageObj = imagesArray.getJSONObject(0);
                    String imagePath = imageObj.optString("image_url", "");
                    if (imagePath.isEmpty()) {
                        imagePath = imageObj.optString("image_path", "");
                    }

                    String imageUrl = imagePath.startsWith("http") ? imagePath : Config.IMAGE_URL + imagePath;

                    Glide.with(PesananSayaActivity.this)
                        .load(imageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.imgProduct);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return ordersArray.length();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgProduct;
            TextView tvProductName, tvCategory, tvStatus;
            MaterialButton btnAction;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgProduct = itemView.findViewById(R.id.imgProductOrder);
                tvProductName = itemView.findViewById(R.id.tvOrderProductName);
                tvCategory = itemView.findViewById(R.id.tvOrderCategory);
                tvStatus = itemView.findViewById(R.id.tvOrderStatus);
                btnAction = itemView.findViewById(R.id.btnOrderAction);
            }
        }
    }
}
