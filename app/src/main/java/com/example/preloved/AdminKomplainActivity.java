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

import com.example.preloved.models.ApiResponse;
import com.example.preloved.models.Complaint;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminKomplainActivity extends AppCompatActivity {

    private RecyclerView rvKomplain;
    private TextView tvTotalKomplain, tvEmptyKomplain;
    private SessionManager sessionManager;
    private KomplainAdapter adapter;
    private final List<Complaint> daftarKomplain = new ArrayList<>();

    private String filterStatus = null;

    // Tab TextViews
    private TextView tabCmpSemua, tabCmpMenunggu, tabCmpDiproses, tabCmpSelesai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain_admin);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin() || sessionManager.getAdminToken() == null) {
            redirectToLogin();
            return;
        }

        tvTotalKomplain = findViewById(R.id.tvTotalKomplain);
        tvEmptyKomplain = findViewById(R.id.tvEmptyKomplain);
        rvKomplain = findViewById(R.id.rvKomplain);

        rvKomplain.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KomplainAdapter(daftarKomplain);
        rvKomplain.setAdapter(adapter);

        if (findViewById(R.id.btnMenu) != null) {
            findViewById(R.id.btnMenu).setOnClickListener(v -> finish());
        }

        setupTabs();
        setupBottomNav();
        loadKomplain();
    }

    private void setupTabs() {
        tabCmpSemua = findViewById(R.id.tabCmpSemua);
        tabCmpMenunggu = findViewById(R.id.tabCmpMenunggu);
        tabCmpDiproses = findViewById(R.id.tabCmpDiproses);
        tabCmpSelesai = findViewById(R.id.tabCmpSelesai);

        if (tabCmpSemua != null) tabCmpSemua.setOnClickListener(v -> { filterStatus = null; updateTabUI(tabCmpSemua); loadKomplain(); });
        if (tabCmpMenunggu != null) tabCmpMenunggu.setOnClickListener(v -> { filterStatus = "pending"; updateTabUI(tabCmpMenunggu); loadKomplain(); });
        if (tabCmpDiproses != null) tabCmpDiproses.setOnClickListener(v -> { filterStatus = "processing"; updateTabUI(tabCmpDiproses); loadKomplain(); });
        if (tabCmpSelesai != null) tabCmpSelesai.setOnClickListener(v -> { filterStatus = "resolved"; updateTabUI(tabCmpSelesai); loadKomplain(); });
    }

    private void updateTabUI(TextView activeTab) {
        TextView[] allTabs = {tabCmpSemua, tabCmpMenunggu, tabCmpDiproses, tabCmpSelesai};
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

    private void loadKomplain() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        Call<ApiResponse<List<Complaint>>> call = apiService.getAdminComplaints(bearerToken, filterStatus);

        call.enqueue(new Callback<ApiResponse<List<Complaint>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Complaint>>> call, Response<ApiResponse<List<Complaint>>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {
                        daftarKomplain.clear();
                        daftarKomplain.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();

                        tvTotalKomplain.setText("Total " + daftarKomplain.size() + " komplain");
                        tvEmptyKomplain.setVisibility(daftarKomplain.isEmpty() ? View.VISIBLE : View.GONE);

                        // JIKA SUKSES, KITA LIHAT APAKAH BENERAN 0 DARI LARAVEL
                        if (daftarKomplain.isEmpty()) {
                            Toast.makeText(AdminKomplainActivity.this, "Laravel ngirim Array Kosong []", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // KALO GAGAL PARSING (MISAL NAMA VARIABEL BEDA)
                        Toast.makeText(AdminKomplainActivity.this, "Data JSON rusak / Gagal di-parse GSON", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // JIKA SERVER LARAVEL ERROR (Misal 500 / 404)
                    Toast.makeText(AdminKomplainActivity.this, "Error Server Kode: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Complaint>>> call, Throwable t) {
                // JIKA GAGAL KONEKSI / CRASH PARSING
                Toast.makeText(AdminKomplainActivity.this, "Crash: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showUbahStatus(Complaint complaint) {
        String[] statusList = {"pending", "processing", "resolved"};
        String[] labelList = {"Menunggu", "Diproses", "Selesai"};

        new AlertDialog.Builder(this)
            .setTitle("Ubah Status " + complaint.getTicketId())
            .setItems(labelList, (dialog, which) -> updateStatus(complaint, statusList[which]))
            .show();
    }

    private void updateStatus(Complaint complaint, String statusBaru) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.updateComplaintStatus("Bearer " + sessionManager.getAdminToken(), complaint.getId(), statusBaru).enqueue(new Callback<ApiResponse<Complaint>>() {
            @Override
            public void onResponse(Call<ApiResponse<Complaint>> call, Response<ApiResponse<Complaint>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminKomplainActivity.this, "Status diperbarui", Toast.LENGTH_SHORT).show();
                    loadKomplain(); // Refresh data
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Complaint>> call, Throwable t) { }
        });
    }

    private void setupBottomNav() {
        LinearLayout navDashboard = findViewById(R.id.navAdminDashboard);
        LinearLayout navProduk = findViewById(R.id.navAdminProduk);
        LinearLayout navTransaksi = findViewById(R.id.navAdminTransaksi);
        LinearLayout navPengguna = findViewById(R.id.navAdminPengguna);

        if (navDashboard != null) navDashboard.setOnClickListener(v -> { startActivity(new Intent(this, AdminMainActivity.class)); overridePendingTransition(0, 0); finish(); });
        if (navProduk != null) navProduk.setOnClickListener(v -> { startActivity(new Intent(this, AdminProdukActivity.class)); overridePendingTransition(0, 0); finish(); });
        if (navTransaksi != null) navTransaksi.setOnClickListener(v -> { startActivity(new Intent(this, AdminTransaksiActivity.class)); overridePendingTransition(0, 0); finish(); });
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
    // ADAPTER KOMPLAIN
    // ==========================================
    private class KomplainAdapter extends RecyclerView.Adapter<KomplainAdapter.ViewHolder> {
        private final List<Complaint> data;

        KomplainAdapter(List<Complaint> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_komplain_admin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Complaint cmp = data.get(position);

            holder.tvTicketId.setText(cmp.getTicketId() != null ? cmp.getTicketId() : "#CMP-XXX");

            String tgl = cmp.getCreatedAt();
            holder.tvTanggalKomplain.setText(tgl != null ? tgl.substring(0, Math.min(16, tgl.length())).replace("T", " ") : "-");

            holder.tvSubjectKomplain.setText(cmp.getSubject());

            String namaUser = cmp.getUser() != null ? cmp.getUser().getNama_lengkap() : "-";
            holder.tvUserKomplain.setText("Oleh: " + namaUser);

            String namaProduk = cmp.getProduct() != null ? cmp.getProduct().getNama_barang() : "-";
            holder.tvProductKomplain.setText("Produk: " + namaProduk);

            String status = cmp.getStatus() != null ? cmp.getStatus() : "pending";

            switch (status) {
                case "pending":
                    holder.tvStatusKomplain.setText("Menunggu");
                    holder.badgeStatusKomplain.setCardBackgroundColor(0xFFFFF3E0);
                    holder.tvStatusKomplain.setTextColor(0xFFF57C00); // Orange
                    break;
                case "processing":
                    holder.tvStatusKomplain.setText("Diproses");
                    holder.badgeStatusKomplain.setCardBackgroundColor(0xFFE3F2FD);
                    holder.tvStatusKomplain.setTextColor(0xFF1E88E5); // Biru
                    break;
                case "resolved":
                    holder.tvStatusKomplain.setText("Selesai");
                    holder.badgeStatusKomplain.setCardBackgroundColor(0xFFE8F5E9);
                    holder.tvStatusKomplain.setTextColor(0xFF4CAF50); // Hijau
                    break;
            }

            holder.itemView.setOnClickListener(v -> showUbahStatus(cmp));
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTicketId, tvTanggalKomplain, tvSubjectKomplain, tvUserKomplain, tvProductKomplain, tvStatusKomplain;
            com.google.android.material.card.MaterialCardView badgeStatusKomplain;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTicketId = itemView.findViewById(R.id.tvTicketId);
                tvTanggalKomplain = itemView.findViewById(R.id.tvTanggalKomplain);
                tvSubjectKomplain = itemView.findViewById(R.id.tvSubjectKomplain);
                tvUserKomplain = itemView.findViewById(R.id.tvUserKomplain);
                tvProductKomplain = itemView.findViewById(R.id.tvProductKomplain);
                tvStatusKomplain = itemView.findViewById(R.id.tvStatusKomplain);
                badgeStatusKomplain = itemView.findViewById(R.id.badgeStatusKomplain);
            }
        }
    }
}
