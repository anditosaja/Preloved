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

public class BarangSayaActivity extends AppCompatActivity {

    private RecyclerView rvMyProducts;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barang_saya);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getToken();

        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvMyProducts = findViewById(R.id.rvMyProducts);
        progressBar = findViewById(R.id.progressBar);
        ImageView btnBack = findViewById(R.id.btnBack);

        if (rvMyProducts != null) {
            // Menggunakan LinearLayoutManager agar tampilan list pesanan rapi vertikal
            rvMyProducts.setLayoutManager(new LinearLayoutManager(this));
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tarikBarangSaya();
    }

    private void tarikBarangSaya() {
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        rvMyProducts.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        // Memanggil rute getMySales untuk mendapatkan SEMUA data produk (laku & belum)
        Call<ResponseBody> call = apiService.getMySales(authHeader);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonString = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonString);

                        if (jsonArray.length() == 0) {
                            rvMyProducts.setVisibility(View.GONE);
                            tvEmptyState.setVisibility(View.VISIBLE);
                        } else {
                            tvEmptyState.setVisibility(View.GONE);
                            rvMyProducts.setVisibility(View.VISIBLE);

                            // Pasang data ke adapter internal
                            SalesAdapter adapter = new SalesAdapter(jsonArray);
                            rvMyProducts.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BarangSayaActivity.this, "Gagal memproses data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown Error";
                        Log.e("API_ERROR_SALES", "Code: " + response.code() + " | " + errorBody);
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                    Toast.makeText(BarangSayaActivity.this, "Gagal mengambil data penjualan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(BarangSayaActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk memproses ACC Pesanan ke Laravel
    private void eksekusiAccPesanan(int orderId) {
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.acceptOrder(authHeader, orderId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BarangSayaActivity.this, "Pesanan berhasil di-ACC!", Toast.LENGTH_SHORT).show();
                    tarikBarangSaya(); // Refresh data list otomatis
                } else {
                    Toast.makeText(BarangSayaActivity.this, "Gagal konfirmasi ke server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(BarangSayaActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========================================================
    // INNER CLASS ADAPTER
    // ========================================================
    private class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {
        private final JSONArray productsArray;

        public SalesAdapter(JSONArray productsArray) {
            this.productsArray = productsArray;
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
                // Sekarang yang ditangkap adalah objek Product langsung
                JSONObject productObj = productsArray.getJSONObject(position);
                int productId = productObj.optInt("product_id");
                String namaBarang = productObj.optString("nama_barang", "Produk");
                int categoryId = productObj.optInt("category_id", 0);
                double hargaTampil = productObj.optDouble("harga_jual", 0);
                String statusBarang = productObj.optString("status_barang", "available");

                int orderId = 0;
                String statusOrder = "";

                // Cek apakah produk ini sudah memiliki pesanan masuk (nested JSON orders dari Laravel)
                JSONArray nestedOrders = productObj.optJSONArray("orders");
                if (nestedOrders != null && nestedOrders.length() > 0) {
                    JSONObject latestOrder = nestedOrders.getJSONObject(0);
                    orderId = latestOrder.optInt("order_id", 0);
                    statusOrder = latestOrder.optString("status", "");
                    // Pakai harga final dari order jika sudah dibeli
                    hargaTampil = latestOrder.optDouble("harga_final", hargaTampil);
                }

                holder.tvProductName.setText(namaBarang);
                holder.tvCategory.setText("Kategori ID: " + categoryId + " | Rp " + new DecimalFormat("#,###").format(hargaTampil));

                // ==========================================================
                // LOGIKA TAMPILAN BERDASARKAN STATUS
                // ==========================================================
                if (statusBarang.equalsIgnoreCase("available")) {
                    holder.tvStatus.setText("Status: Belum Terjual (Tersedia)");
                    holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#757575")); // Warna Abu-abu
                    holder.btnAction.setVisibility(View.GONE);
                } else {
                    // Logika jika barang sudah terjual (Order Status)
                    if (statusOrder.equalsIgnoreCase("paid")) {
                        holder.tvStatus.setText("Status: Menunggu ACC Penjual");
                        holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                        holder.btnAction.setVisibility(View.VISIBLE);
                        holder.btnAction.setText("ACC Pesanan");
                        int finalOrderId = orderId;
                        holder.btnAction.setOnClickListener(v -> eksekusiAccPesanan(finalOrderId));
                    } else if (statusOrder.equalsIgnoreCase("shipped")) {
                        holder.tvStatus.setText("Status: Barang Sedang Dikirim");
                        holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#2196F3"));
                        holder.btnAction.setVisibility(View.GONE);
                    } else if (statusOrder.equalsIgnoreCase("completed")) {
                        holder.tvStatus.setText("Status: Transaksi Selesai (Dana Cair)");
                        holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
                        holder.btnAction.setVisibility(View.GONE);
                    } else {
                        // Jika status lain (contoh: cancelled, reserved)
                        holder.tvStatus.setText("Status: " + (statusOrder.isEmpty() ? statusBarang.toUpperCase() : statusOrder.toUpperCase()));
                        holder.btnAction.setVisibility(View.GONE);
                    }
                }

                // ==========================================================
                // LOAD GAMBAR
                // ==========================================================
                JSONArray imagesArray = productObj.optJSONArray("images");
                if (imagesArray != null && imagesArray.length() > 0) {
                    JSONObject imageObj = imagesArray.getJSONObject(0);
                    String imagePath = imageObj.optString("image_url", "");
                    if(imagePath.isEmpty()) {
                        imagePath = imageObj.optString("image_path", "");
                    }

                    String imageUrl = imagePath.startsWith("http") ? imagePath : "http://192.168.18.169:8000/storage/" + imagePath;

                    Glide.with(BarangSayaActivity.this)
                        .load(imageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.imgProduct);
                } else {
                    holder.imgProduct.setImageResource(android.graphics.drawable.GradientDrawable.RECTANGLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return productsArray.length();
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
