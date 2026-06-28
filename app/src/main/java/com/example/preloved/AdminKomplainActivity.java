package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.preloved.utils.SessionManager;

/**
 * Halaman Komplain untuk admin. Untuk saat ini masih statis (data dummy
 * yang sudah ada di layout XML) karena belum ada tabel/model Complaint
 * di backend. Hanya navigasi & guard sesi admin yang aktif di sini.
 */
public class AdminKomplainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain_admin);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin() || sessionManager.getAdminToken() == null) {
            redirectToLogin();
            return;
        }

        if (findViewById(R.id.btnMenu) != null) {
            findViewById(R.id.btnMenu).setOnClickListener(v -> finish());
        }

        setupBottomNav();
    }

    private void setupBottomNav() {
        LinearLayout navDashboard = findViewById(R.id.navAdminDashboard);
        LinearLayout navProduk = findViewById(R.id.navAdminProduk);
        LinearLayout navTransaksi = findViewById(R.id.navAdminTransaksi);
        LinearLayout navPengguna = findViewById(R.id.navAdminPengguna);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                startActivity(new Intent(AdminKomplainActivity.this, AdminMainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navProduk != null) {
            navProduk.setOnClickListener(v -> {
                startActivity(new Intent(AdminKomplainActivity.this, AdminProdukActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navTransaksi != null) {
            navTransaksi.setOnClickListener(v -> {
                startActivity(new Intent(AdminKomplainActivity.this, AdminTransaksiActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navPengguna != null) {
            navPengguna.setOnClickListener(v -> {
                startActivity(new Intent(AdminKomplainActivity.this, AdminPenggunaActivity.class));
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
}
