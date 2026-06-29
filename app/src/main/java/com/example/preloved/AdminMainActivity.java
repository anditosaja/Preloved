package com.example.preloved;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.preloved.models.AdminDashboardSummary;
import com.example.preloved.models.ApiResponse;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dashboard utama untuk admin. Activity ini yang akan dibuka begitu admin
 * berhasil login (dibedakan dari MainActivity milik user biasa).
 */
public class AdminMainActivity extends AppCompatActivity {

    private TextView tvAdminTitle;
    private TextView valPengguna, valProduk, valTransaksi, valPendapatan;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        sessionManager = new SessionManager(this);

        // Jaga-jaga: kalau activity ini diakses tanpa sesi admin yang valid,
        // tendang balik ke halaman login.
        if (!sessionManager.isAdmin() || sessionManager.getAdminToken() == null) {
            redirectToLogin();
            return;
        }

        tvAdminTitle = findViewById(R.id.tvAdminTitle);
        valPengguna = findViewById(R.id.valPengguna);
        valProduk = findViewById(R.id.valProduk);
        valTransaksi = findViewById(R.id.valTransaksi);
        valPendapatan = findViewById(R.id.valPendapatan);
        View cardPengguna = findViewById(R.id.cardPengguna);
        View cardProduk = findViewById(R.id.cardProduk);
        View cardTransaksi = findViewById(R.id.cardTransaksi);
        View cardPendapatan = findViewById(R.id.cardPendapatan);

        if (tvAdminTitle != null) {
            String nama = sessionManager.getAdminNama();
            tvAdminTitle.setText("Halo, " + (nama != null ? nama : "Admin") + " \uD83D\uDC4B");
        }

        if (findViewById(R.id.imgProfile) != null) {
            findViewById(R.id.imgProfile).setOnClickListener(v -> showLogoutConfirmation());
        }

        if (cardPengguna != null) {
            cardPengguna.setOnClickListener(v -> bukaGrafik("pengguna", "Grafik Pertumbuhan Pengguna"));
        }
        if (cardProduk != null) {
            cardProduk.setOnClickListener(v -> bukaGrafik("produk", "Grafik Penambahan Produk"));
        }
        if (cardTransaksi != null) {
            cardTransaksi.setOnClickListener(v -> bukaGrafik("transaksi", "Grafik Transaksi Harian"));
        }
        if (cardPendapatan != null) {
            cardPendapatan.setOnClickListener(v -> bukaGrafik("pendapatan", "Grafik Pendapatan Kotor"));
        }

        setupBottomNav();
        loadDashboardSummary();
    }
    private void bukaGrafik(String tipeMetrik, String judul) {
        Intent intent = new Intent(AdminMainActivity.this, AdminDetailGrafikActivity.class);
        intent.putExtra("TIPE_METRIK", tipeMetrik);
        intent.putExtra("JUDUL_GRAFIK", judul);
        startActivity(intent);
    }
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Keluar")
                .setMessage("Apakah Anda yakin ingin keluar dari akun admin?")
                .setPositiveButton("Keluar", (dialog, which) -> performLogout())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void performLogout() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        apiService.adminLogout(bearerToken).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                redirectToLogin();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                // Tetap logout secara lokal walau request ke server gagal,
                // supaya admin tidak terjebak di akunnya sendiri saat offline.
                redirectToLogin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardSummary();
    }

    private void loadDashboardSummary() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        Call<ApiResponse<AdminDashboardSummary>> call = apiService.getAdminDashboardSummary(bearerToken);

        call.enqueue(new Callback<ApiResponse<AdminDashboardSummary>>() {
            @Override
            public void onResponse(Call<ApiResponse<AdminDashboardSummary>> call, Response<ApiResponse<AdminDashboardSummary>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    AdminDashboardSummary summary = response.body().getData();

                    if (valPengguna != null) {
                        valPengguna.setText(formatAngka(summary.getTotal_pengguna()));
                    }
                    if (valProduk != null) {
                        valProduk.setText(formatAngka(summary.getProduk_aktif()));
                    }
                    if (valTransaksi != null) {
                        valTransaksi.setText(formatAngka(summary.getTotal_transaksi()));
                    }
                    if (valPendapatan != null) {
                        valPendapatan.setText("Rp " + formatAngka((long) summary.getTotal_pendapatan()));
                    }
                } else if (response.code() == 401 || response.code() == 403) {
                    Toast.makeText(AdminMainActivity.this, "Sesi admin berakhir, silakan login kembali", Toast.LENGTH_LONG).show();
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AdminDashboardSummary>> call, Throwable t) {
                Toast.makeText(AdminMainActivity.this, "Gagal memuat data dashboard: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String formatAngka(long angka) {
        return NumberFormat.getNumberInstance(new Locale("in", "ID")).format(angka);
    }

    private void setupBottomNav() {
        LinearLayout navDashboard = findViewById(R.id.navAdminDashboard);
        LinearLayout navProduk = findViewById(R.id.navAdminProduk);
        LinearLayout navTransaksi = findViewById(R.id.navAdminTransaksi);
        LinearLayout navKomplain = findViewById(R.id.navAdminKomplain);
        LinearLayout navPengguna = findViewById(R.id.navAdminPengguna);

        // navDashboard: sudah di halaman ini, tidak perlu aksi pindah activity.

        if (navProduk != null) {
            navProduk.setOnClickListener(v -> {
                startActivity(new Intent(AdminMainActivity.this, AdminProdukActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        if (navTransaksi != null) {
            navTransaksi.setOnClickListener(v -> {
                startActivity(new Intent(AdminMainActivity.this, AdminTransaksiActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        if (navKomplain != null) {
            navKomplain.setOnClickListener(v -> {
                startActivity(new Intent(AdminMainActivity.this, AdminKomplainActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        if (navPengguna != null) {
            navPengguna.setOnClickListener(v -> {
                startActivity(new Intent(AdminMainActivity.this, AdminPenggunaActivity.class));
                overridePendingTransition(0, 0);
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
}
