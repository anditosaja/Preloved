package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.preloved.models.LoginRequest;
import com.example.preloved.models.LoginResponse;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnMasuk;
    private TextView txtKeRegister; // Ubah nama variabel menyesuaikan XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Hubungkan variabel dengan ID XML yang benar
        etEmail = findViewById(R.id.editTextLoginEmail);
        etPassword = findViewById(R.id.editTextLoginPassword);
        btnMasuk = findViewById(R.id.btnMasuk);
        txtKeRegister = findViewById(R.id.txtKeRegister); // Pastikan ini txtKeRegister

        // 2. Aksi tombol back
        if (findViewById(R.id.btnBack) != null) {
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        }

        // 3. Aksi ketika tombol MASUK diklik
        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    etEmail.setError("Email tidak boleh kosong");
                    return;
                }

                if (password.isEmpty()) {
                    etPassword.setError("Kata sandi tidak boleh kosong");
                    return;
                }

                loginUser(email, password);
            }
        });

        // 4. Aksi klik teks "Daftar"
        if (txtKeRegister != null) {
            txtKeRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void loginUser(String email, String password) {
        // Tampilkan feedback sedang memproses
        btnMasuk.setEnabled(false);
        btnMasuk.setText("MEMPROSES...");

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(new LoginRequest(email, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // Kembalikan tombol ke state semula
                btnMasuk.setEnabled(true);
                btnMasuk.setText("MASUK");

                if (response.isSuccessful() && response.body() != null) {
                    // Ambil token dari response JSON Laravel
                    String token = response.body().getToken();

                    // Simpan token ke SharedPreferences
                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                    sessionManager.saveToken(token);

                    Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();

                    // Pindah ke MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Tutup LoginActivity
                } else {
                    Toast.makeText(LoginActivity.this, "Email atau password salah", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Kembalikan tombol ke state semula
                btnMasuk.setEnabled(true);
                btnMasuk.setText("MASUK");

                Toast.makeText(LoginActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
