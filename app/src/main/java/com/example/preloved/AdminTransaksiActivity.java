package com.example.preloved;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preloved.models.ApiResponse;
import com.example.preloved.models.Order;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

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

    /** null = semua, atau pending/paid/shipped/completed/cancelled (sesuai enum status di Order) */
    private String filterStatus = null;

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
        View tabSemua = findViewById(R.id.tabTrxSemua);
        View tabMenunggu = findViewById(R.id.tabTrxMenunggu);
        View tabDiproses = findViewById(R.id.tabTrxDiproses);
        View tabSelesai = findViewById(R.id.tabTrxSelesai);
        View tabDibatalkan = findViewById(R.id.tabTrxDibatalkan);

        if (tabSemua != null) tabSemua.setOnClickListener(v -> { filterStatus = null; loadTransaksi(); });
        if (tabMenunggu != null) tabMenunggu.setOnClickListener(v -> { filterStatus = "pending"; loadTransaksi(); });
        // "Diproses" mencakup transaksi yang sudah dibayar/dikirim tapi belum selesai.
        // Backend hanya menerima 1 nilai status per request, jadi kita pilih "shipped"
        // sebagai representasi utama tahap diproses.
        if (tabDiproses != null) tabDiproses.setOnClickListener(v -> { filterStatus = "shipped"; loadTransaksi(); });
        if (tabSelesai != null) tabSelesai.setOnClickListener(v -> { filterStatus = "completed"; loadTransaksi(); });
        if (tabDibatalkan != null) tabDibatalkan.setOnClickListener(v -> { filterStatus = "cancelled"; loadTransaksi(); });
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
                Toast.makeText(AdminTransaksiActivity.this, "Gagal memuat transaksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showUbahStatus(Order order) {
        String[] statusList = {"pending", "paid", "shipped", "completed", "cancelled"};
        String[] labelList = {"Menunggu", "Dibayar", "Dikirim", "Selesai", "Dibatalkan"};

        new AlertDialog.Builder(this)
                .setTitle("Ubah Status Transaksi #" + order.getOrder_id())
                .setItems(labelList, (dialog, which) -> updateStatus(order, statusList[which]))
                .show();
    }

    private void updateStatus(Order order, String statusBaru) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        apiService.updateOrderStatus(bearerToken, order.getOrder_id(), statusBaru).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminTransaksiActivity.this, "Status transaksi diperbarui", Toast.LENGTH_SHORT).show();
                    loadTransaksi();
                } else {
                    Toast.makeText(AdminTransaksiActivity.this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                Toast.makeText(AdminTransaksiActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupBottomNav() {
        LinearLayout navDashboard = findViewById(R.id.navAdminDashboard);
        LinearLayout navProduk = findViewById(R.id.navAdminProduk);
        LinearLayout navKomplain = findViewById(R.id.navAdminKomplain);
        LinearLayout navPengguna = findViewById(R.id.navAdminPengguna);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                startActivity(new Intent(AdminTransaksiActivity.this, AdminMainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navProduk != null) {
            navProduk.setOnClickListener(v -> {
                startActivity(new Intent(AdminTransaksiActivity.this, AdminProdukActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navKomplain != null) {
            navKomplain.setOnClickListener(v -> {
                startActivity(new Intent(AdminTransaksiActivity.this, AdminKomplainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navPengguna != null) {
            navPengguna.setOnClickListener(v -> {
                startActivity(new Intent(AdminTransaksiActivity.this, AdminPenggunaActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    private void redirectToLogin() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

        private final List<Order> data;

        TransaksiAdapter(List<Order> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaksi_admin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Order order = data.get(position);

            holder.tvInvoice.setText("#INV-" + order.getOrder_id());

            String tanggal = order.getCreated_at();
            holder.tvTanggal.setText(tanggal != null ? tanggal.substring(0, Math.min(16, tanggal.length())).replace("T", " ") : "-");

            String namaProduk = order.getProduct() != null ? order.getProduct().getNama_barang() : "Produk tidak ditemukan";
            holder.tvNamaProduk.setText(namaProduk);

            String pembeli = order.getBuyer() != null ? order.getBuyer().getNama_lengkap() : "-";
            String penjual = order.getSeller() != null ? order.getSeller().getNama_lengkap() : "-";
            holder.tvBuyerSeller.setText(pembeli + " • " + penjual);

            holder.tvHarga.setText("Rp" + order.getHarga_final());

            String status = order.getStatus() != null ? order.getStatus() : "pending";
            holder.tvStatus.setText(labelStatus(status));

            switch (status) {
                case "pending":
                    holder.cardBadge.setCardBackgroundColor(0xFFFFF3E0);
                    holder.tvStatus.setTextColor(0xFFFF9800);
                    break;
                case "completed":
                    holder.cardBadge.setCardBackgroundColor(0xFFE8F5E9);
                    holder.tvStatus.setTextColor(0xFF4CAF50);
                    break;
                case "cancelled":
                    holder.cardBadge.setCardBackgroundColor(0xFFFFEBEE);
                    holder.tvStatus.setTextColor(0xFFF44336);
                    break;
                default:
                    holder.cardBadge.setCardBackgroundColor(0xFFE3F2FD);
                    holder.tvStatus.setTextColor(0xFF2196F3);
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
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvInvoice, tvTanggal, tvNamaProduk, tvBuyerSeller, tvHarga, tvStatus;
            com.google.android.material.card.MaterialCardView cardBadge;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvInvoice = itemView.findViewById(R.id.tvInvoiceTransaksi);
                tvTanggal = itemView.findViewById(R.id.tvTanggalTransaksi);
                tvNamaProduk = itemView.findViewById(R.id.tvNamaProdukTransaksi);
                tvBuyerSeller = itemView.findViewById(R.id.tvBuyerSellerTransaksi);
                tvHarga = itemView.findViewById(R.id.tvHargaTransaksi);
                tvStatus = itemView.findViewById(R.id.tvStatusTransaksi);
                cardBadge = itemView.findViewById(R.id.badgeStatusTransaksi);
            }
        }
    }
}
