package com.example.preloved;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preloved.models.ApiResponse;
import com.example.preloved.models.User;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.example.preloved.network.Config;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPenggunaActivity extends AppCompatActivity {

    private RecyclerView rvPengguna;
    private TextView tvTotalPengguna, tvEmptyPengguna;
    private SessionManager sessionManager;
    private PenggunaAdapter adapter;
    private final List<User> daftarPengguna = new ArrayList<>();

    /** Filter status saat ini: null = semua, atau "aktif" / "nonaktif" / "diblokir" */
    private String filterStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengguna_admin);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin() || sessionManager.getAdminToken() == null) {
            redirectToLogin();
            return;
        }

        tvTotalPengguna = findViewById(R.id.tvTotalPengguna);
        tvEmptyPengguna = findViewById(R.id.tvEmptyPengguna);
        rvPengguna = findViewById(R.id.rvPengguna);

        rvPengguna.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PenggunaAdapter(daftarPengguna);
        rvPengguna.setAdapter(adapter);

        if (findViewById(R.id.btnMenu) != null) {
            findViewById(R.id.btnMenu).setOnClickListener(v -> finish());
        }

        setupBottomNav();
        loadPengguna();
    }

    private void loadPengguna() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        Call<ApiResponse<List<User>>> call = apiService.getAdminUsers(bearerToken, filterStatus, null);

        call.enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    daftarPengguna.clear();
                    daftarPengguna.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();

                    tvTotalPengguna.setText("Total " + daftarPengguna.size() + " pengguna");
                    tvEmptyPengguna.setVisibility(daftarPengguna.isEmpty() ? View.VISIBLE : View.GONE);
                } else if (response.code() == 401 || response.code() == 403) {
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Toast.makeText(AdminPenggunaActivity.this, "Gagal memuat pengguna: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmToggleBlock(User user) {
        boolean sedangDiblokir = "diblokir".equalsIgnoreCase(user.getStatus_akun());
        String aksi = sedangDiblokir ? "mengaktifkan kembali" : "memblokir";

        new AlertDialog.Builder(this)
                .setTitle(sedangDiblokir ? "Aktifkan Pengguna" : "Blokir Pengguna")
                .setMessage("Apakah Anda yakin ingin " + aksi + " " + user.getNama_lengkap() + "?")
                .setPositiveButton("Ya", (dialog, which) -> toggleBlock(user, sedangDiblokir))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void toggleBlock(User user, boolean sedangDiblokir) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        Call<ApiResponse<User>> call = sedangDiblokir
                ? apiService.unblockUser(bearerToken, user.getUserId())
                : apiService.blockUser(bearerToken, user.getUserId());

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminPenggunaActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadPengguna();
                } else {
                    Toast.makeText(AdminPenggunaActivity.this, "Gagal memperbarui status pengguna", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(AdminPenggunaActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupBottomNav() {
        LinearLayout navDashboard = findViewById(R.id.navAdminDashboard);
        LinearLayout navProduk = findViewById(R.id.navAdminProduk);
        LinearLayout navTransaksi = findViewById(R.id.navAdminTransaksi);
        LinearLayout navKomplain = findViewById(R.id.navAdminKomplain);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                startActivity(new Intent(AdminPenggunaActivity.this, AdminMainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navProduk != null) {
            navProduk.setOnClickListener(v -> {
                startActivity(new Intent(AdminPenggunaActivity.this, AdminProdukActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navTransaksi != null) {
            navTransaksi.setOnClickListener(v -> {
                startActivity(new Intent(AdminPenggunaActivity.this, AdminTransaksiActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navKomplain != null) {
            navKomplain.setOnClickListener(v -> {
                startActivity(new Intent(AdminPenggunaActivity.this, AdminKomplainActivity.class));
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

    /**
     * Adapter RecyclerView sederhana untuk daftar pengguna di panel admin.
     */
    private class PenggunaAdapter extends RecyclerView.Adapter<PenggunaAdapter.ViewHolder> {

        private final List<User> data;

        PenggunaAdapter(List<User> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pengguna_admin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = data.get(position);

            holder.tvNama.setText(user.getNama_lengkap());
            holder.tvEmail.setText(user.getEmail());

            String fotoProfil = user.getFoto_profil();
            if (fotoProfil != null && !fotoProfil.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                    .load(Config.IMAGE_URL + fotoProfil)
                    .placeholder(R.drawable.ic_person_placeholder)
                    .error(R.drawable.ic_person_placeholder)
                    .circleCrop()
                    .into(holder.ivAvatar);
            } else {
                holder.ivAvatar.setImageResource(R.drawable.ic_person_placeholder);
            }

            String bergabung = user.getCreated_at();
            holder.tvBergabung.setText("Bergabung: " + (bergabung != null ? bergabung.substring(0, Math.min(10, bergabung.length())) : "-"));

            String status = user.getStatus_akun() != null ? user.getStatus_akun() : "aktif";
            holder.tvStatus.setText(capitalize(status));

            if ("diblokir".equalsIgnoreCase(status)) {
                holder.cardBadge.setCardBackgroundColor(0xFFFFEBEE);
                holder.tvStatus.setTextColor(0xFFF44336);
            } else if ("nonaktif".equalsIgnoreCase(status)) {
                holder.cardBadge.setCardBackgroundColor(0xFFFFF3E0);
                holder.tvStatus.setTextColor(0xFFFF9800);
            } else {
                holder.cardBadge.setCardBackgroundColor(0xFFE8F5E9);
                holder.tvStatus.setTextColor(0xFF4CAF50);
            }

            holder.btnMore.setOnClickListener(v -> confirmToggleBlock(user));
            holder.itemView.setOnClickListener(v -> confirmToggleBlock(user));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private String capitalize(String s) {
            if (s == null || s.isEmpty()) return s;
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNama, tvEmail, tvBergabung, tvStatus;
            ImageView btnMore, ivAvatar;
            com.google.android.material.card.MaterialCardView cardBadge;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNama = itemView.findViewById(R.id.tvNamaPengguna);
                tvEmail = itemView.findViewById(R.id.tvEmailPengguna);
                tvBergabung = itemView.findViewById(R.id.tvBergabungPengguna);
                tvStatus = itemView.findViewById(R.id.tvStatusPengguna);
                btnMore = itemView.findViewById(R.id.btnMorePengguna);
                cardBadge = itemView.findViewById(R.id.badgeStatusPengguna);
                ivAvatar = itemView.findViewById(R.id.ivAvatarPengguna);
            }
        }
    }
}
