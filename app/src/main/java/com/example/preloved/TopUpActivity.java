package com.example.preloved;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.example.preloved.models.TopUpRequest;
import com.example.preloved.models.TopUpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpActivity extends AppCompatActivity {

    private EditText etNominal;
    private Button btnKirimTopUp;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        etNominal = findViewById(R.id.etNominal);
        btnKirimTopUp = findViewById(R.id.btnKirimTopUp);

        // Ambil token dari SessionManager
        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getToken();

        // Tombol Back
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnKirimTopUp.setOnClickListener(v -> processTopUp());
    }

    private void processTopUp() {
        String nominalStr = etNominal.getText().toString().trim();

        if (nominalStr.isEmpty()) {
            Toast.makeText(this, "Nominal tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(nominalStr);
        TopUpRequest request = new TopUpRequest(amount);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<TopUpResponse> call = apiService.submitTopUp("Bearer " + token, request);

        call.enqueue(new Callback<TopUpResponse>() {
            @Override
            public void onResponse(Call<TopUpResponse> call, Response<TopUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(TopUpActivity.this, "Berhasil! Saldo ditambahkan.", Toast.LENGTH_SHORT).show();
                    // Tutup halaman Top Up, otomatis kembali ke ProfilActivity
                    // Karena di ProfilActivity ada onResume(), saldo otomatis ter-refresh!
                    finish();
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "Error tidak diketahui";
                        Log.e("DEBUG_TOPUP", "Error Code: " + response.code() + " | Body: " + errorMsg);
                        Toast.makeText(TopUpActivity.this, "Gagal: " + response.code(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(TopUpActivity.this, "Gagal parse error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TopUpResponse> call, Throwable t) {
                Toast.makeText(TopUpActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
