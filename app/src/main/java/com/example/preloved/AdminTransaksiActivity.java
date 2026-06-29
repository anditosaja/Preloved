package com.example.preloved;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preloved.models.ApiResponse;
import com.example.preloved.models.Order;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.Config;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTransaksiActivity extends AppCompatActivity {

    private RecyclerView rvTransaksi;
    private TextView tvTotalTransaksi, tvEmptyTransaksi;
    private SessionManager sessionManager;
    private TransaksiAdapter adapter;
    private final List<Order> daftarTransaksi = new ArrayList<>();

    private String filterStatus = null;

    // Tab TextViews
    private TextView tabTrxSemua, tabTrxMenunggu, tabTrxDiproses, tabTrxSelesai, tabTrxDibatalkan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_admin);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin() || sessionManager.getAdminToken() == null) {
            redirectToLogin();
            return;
        }

        tvTotalTransaksi = findViewById(R.id.tvTotalTransaksi);
        tvEmptyTransaksi = findViewById(R.id.tvEmptyTransaksi);
        rvTransaksi = findViewById(R.id.rvTransaksi);

        rvTransaksi.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransaksiAdapter(daftarTransaksi);
        rvTransaksi.setAdapter(adapter);

        if (findViewById(R.id.btnMenu) != null) {
            findViewById(R.id.btnMenu).setOnClickListener(v -> finish());
        }

        setupTabs();
        setupBottomNav();
        loadTransaksi();
    }

    private void setupTabs() {
        tabTrxSemua = findViewById(R.id.tabTrxSemua);
        tabTrxMenunggu = findViewById(R.id.tabTrxMenunggu);
        tabTrxDiproses = findViewById(R.id.tabTrxDiproses);
        tabTrxSelesai = findViewById(R.id.tabTrxSelesai);
        tabTrxDibatalkan = findViewById(R.id.tabTrxDibatalkan);

        if (tabTrxSemua != null) tabTrxSemua.setOnClickListener(v -> { filterStatus = null; updateTabUI(tabTrxSemua); loadTransaksi(); });
        if (tabTrxMenunggu != null) tabTrxMenunggu.setOnClickListener(v -> { filterStatus = "pending"; updateTabUI(tabTrxMenunggu); loadTransaksi(); });
        if (tabTrxDiproses != null) tabTrxDiproses.setOnClickListener(v -> { filterStatus = "shipped"; updateTabUI(tabTrxDiproses); loadTransaksi(); });
        if (tabTrxSelesai != null) tabTrxSelesai.setOnClickListener(v -> { filterStatus = "completed"; updateTabUI(tabTrxSelesai); loadTransaksi(); });
        if (tabTrxDibatalkan != null) tabTrxDibatalkan.setOnClickListener(v -> { filterStatus = "cancelled"; updateTabUI(tabTrxDibatalkan); loadTransaksi(); });
    }

    private void updateTabUI(TextView activeTab) {
        TextView[] allTabs = {tabTrxSemua, tabTrxMenunggu, tabTrxDiproses, tabTrxSelesai, tabTrxDibatalkan};
        for (TextView tab : allTabs) {
            if (tab == null) continue;
            if (tab == activeTab) {
                ViewCompat.setBackgroundTintList(tab, ColorStateList.valueOf(android.graphics.Color.parseColor("#6952D9")));
                tab.setTextColor(android.graphics.Color.WHITE);
                tab.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                ViewCompat.setBackgroundTintList(tab, ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
                tab.setTextColor(android.graphics.Color.parseColor("#757575"));
                tab.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
    }

    private void loadTransaksi() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        Call<ApiResponse<List<Order>>> call = apiService.getAdminOrders(bearerToken, filterStatus);

        call.enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    daftarTransaksi.clear();
                    daftarTransaksi.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();

                    tvTotalTransaksi.setText("Total " + daftarTransaksi.size() + " transaksi");
                    tvEmptyTransaksi.setVisibility(daftarTransaksi.isEmpty() ? View.VISIBLE : View.GONE);
                } else if (response.code() == 401 || response.code() == 403) {
                    redirectToLogin();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Toast.makeText(AdminTransaksiActivity.this, "Gagal memuat transaksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUbahStatus(Order order) {
        String[] statusList = {"pending", "paid", "shipped", "completed", "cancelled"};
        String[] labelList = {"Menunggu", "Dibayar", "Dikirim", "Selesai", "Dibatalkan"};

        new AlertDialog.Builder(this)
            .setTitle("Ubah Status #" + order.getOrder_id())
            .setItems(labelList, (dialog, which) -> updateStatus(order, statusList[which]))
            .show();
    }

    private void updateStatus(Order order, String statusBaru) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.updateOrderStatus("Bearer " + sessionManager.getAdminToken(), order.getOrder_id(), statusBaru).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminTransaksiActivity.this, "Status diperbarui", Toast.LENGTH_SHORT).show();
                    loadTransaksi();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) { }
        });
    }

    private void setupBottomNav() {
        LinearLayout navDashboard = findViewById(R.id.navAdminDashboard);
        LinearLayout navProduk = findViewById(R.id.navAdminProduk);
        LinearLayout navKomplain = findViewById(R.id.navAdminKomplain);
        LinearLayout navPengguna = findViewById(R.id.navAdminPengguna);

        if (navDashboard != null) navDashboard.setOnClickListener(v -> { startActivity(new Intent(this, AdminMainActivity.class)); overridePendingTransition(0, 0); finish(); });
        if (navProduk != null) navProduk.setOnClickListener(v -> { startActivity(new Intent(this, AdminProdukActivity.class)); overridePendingTransition(0, 0); finish(); });
        if (navKomplain != null) navKomplain.setOnClickListener(v -> { startActivity(new Intent(this, AdminKomplainActivity.class)); overridePendingTransition(0, 0); finish(); });
        if (navPengguna != null) navPengguna.setOnClickListener(v -> { startActivity(new Intent(this, AdminPenggunaActivity.class)); overridePendingTransition(0, 0); finish(); });
    }

    private void redirectToLogin() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ==========================================
    // ADAPTER TRANSAKSI + GLIDE
    // ==========================================
    private class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {
        private final List<Order> data;

        TransaksiAdapter(List<Order> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi_admin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Order order = data.get(position);

            holder.tvInvoiceTransaksi.setText("#INV-" + order.getOrder_id());

            String tanggal = order.getCreated_at();
            holder.tvTanggalTransaksi.setText(tanggal != null ? tanggal.substring(0, Math.min(16, tanggal.length())).replace("T", " ") : "-");

            String namaProduk = order.getProduct() != null ? order.getProduct().getNama_barang() : "Produk tidak ditemukan";
            holder.tvNamaProdukTransaksi.setText(namaProduk);

            String pembeli = order.getBuyer() != null ? order.getBuyer().getNama_lengkap() : "-";
            String penjual = order.getSeller() != null ? order.getSeller().getNama_lengkap() : "-";
            holder.tvBuyerSellerTransaksi.setText(pembeli + " • " + penjual);

            try {
                double harga = Double.parseDouble(order.getHarga_final());
                holder.tvHargaTransaksi.setText("Rp" + new DecimalFormat("#,###").format(harga));
            } catch (Exception e) {
                holder.tvHargaTransaksi.setText("Rp" + order.getHarga_final());
            }

            // LOAD GAMBAR PRODUK VIA GLIDE
            if (order.getProduct() != null && order.getProduct().getImages() != null && !order.getProduct().getImages().isEmpty()) {
                String imgPath = order.getProduct().getImages().get(0).getImage_path();
                if (imgPath != null) {
                    String url = imgPath.startsWith("http") ? imgPath : Config.IMAGE_URL + imgPath;
                    Glide.with(holder.itemView.getContext())
                        .load(url)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.ivProdukTransaksi);
                }
            } else {
                holder.ivProdukTransaksi.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            String status = order.getStatus() != null ? order.getStatus() : "pending";
            holder.tvStatusTransaksi.setText(labelStatus(status));

            switch (status) {
                case "pending":
                    holder.badgeStatusTransaksi.setCardBackgroundColor(0xFFFFF3E0);
                    holder.tvStatusTransaksi.setTextColor(0xFFFF9800);
                    break;
                case "completed":
                    holder.badgeStatusTransaksi.setCardBackgroundColor(0xFFE8F5E9);
                    holder.tvStatusTransaksi.setTextColor(0xFF4CAF50);
                    break;
                case "cancelled":
                    holder.badgeStatusTransaksi.setCardBackgroundColor(0xFFFFEBEE);
                    holder.tvStatusTransaksi.setTextColor(0xFFF44336);
                    break;
                default:
                    holder.badgeStatusTransaksi.setCardBackgroundColor(0xFFE3F2FD);
                    holder.tvStatusTransaksi.setTextColor(0xFF2196F3);
                    break;
            }

            holder.itemView.setOnClickListener(v -> showUbahStatus(order));
        }

        private String labelStatus(String status) {
            switch (status) {
                case "pending": return "Menunggu";
                case "paid": return "Dibayar";
                case "shipped": return "Dikirim";
                case "completed": return "Selesai";
                case "cancelled": return "Dibatalkan";
                default: return status;
            }
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvInvoiceTransaksi, tvTanggalTransaksi, tvNamaProdukTransaksi, tvBuyerSellerTransaksi, tvHargaTransaksi, tvStatusTransaksi;
            ImageView ivProdukTransaksi;
            com.google.android.material.card.MaterialCardView badgeStatusTransaksi;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvInvoiceTransaksi = itemView.findViewById(R.id.tvInvoiceTransaksi);
                tvTanggalTransaksi = itemView.findViewById(R.id.tvTanggalTransaksi);
                tvNamaProdukTransaksi = itemView.findViewById(R.id.tvNamaProdukTransaksi);
                tvBuyerSellerTransaksi = itemView.findViewById(R.id.tvBuyerSellerTransaksi);
                tvHargaTransaksi = itemView.findViewById(R.id.tvHargaTransaksi);
                tvStatusTransaksi = itemView.findViewById(R.id.tvStatusTransaksi);
                ivProdukTransaksi = itemView.findViewById(R.id.ivProdukTransaksi);
                badgeStatusTransaksi = itemView.findViewById(R.id.badgeStatusTransaksi);
            }
        }
    }
}
