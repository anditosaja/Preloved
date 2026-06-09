package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.example.preloved.models.LoginRequest;
import com.example.preloved.models.LoginResponse;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnMasuk;
    private TextView txtKeRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.editTextLoginEmail);
        etPassword = findViewById(R.id.editTextLoginPassword);
        btnMasuk = findViewById(R.id.btnMasuk);
        txtKeRegister = findViewById(R.id.txtKeRegister);

        if (findViewById(R.id.btnBack) != null) {
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        }

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    etEmail.setError("Email tidak boleh kosong");
                } else if (password.isEmpty()) {
                    etPassword.setError("Kata sandi tidak boleh kosong");
                } else {
                    btnMasuk.setEnabled(false);
                    btnMasuk.setText("MEMPROSES...");
                    loginUser(email, password);
                }
            }
        });

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
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(new LoginRequest(email, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("MASUK");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();

                    int userId = 0;
                    if (loginResponse.getUser() != null) {
                        userId = loginResponse.getUser().getUser_id();
                    }

                    // Kunci data login ke SessionManager lokal HP
                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                    sessionManager.saveToken(token);
                    sessionManager.saveUserId(userId);

                    Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Email atau password salah", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnMasuk.setEnabled(true);
                btnMasuk.setText("MASUK");
                Toast.makeText(LoginActivity.this, "Error Koneksi Backend: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
