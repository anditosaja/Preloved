package com.example.preloved;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TulisReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextInputEditText etKomentar;
    private MaterialButton btnKirimUlasan;
    private int orderId = -1;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tulis_review);

        // 1. Inisialisasi Komponen UI
        ImageView btnBackReview = findViewById(R.id.btnBackReview);
        ratingBar = findViewById(R.id.ratingBar);
        etKomentar = findViewById(R.id.etKomentar);
        btnKirimUlasan = findViewById(R.id.btnKirimUlasan);

        // 2. Tangkap ORDER_ID dari halaman pesanan sebelumnya
        if (getIntent() != null && getIntent().hasExtra("ORDER_ID")) {
            orderId = getIntent().getIntExtra("ORDER_ID", -1);
        }

        // Ambil token login
        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getToken();

        // Batalkan jika orderId tidak valid
        if (orderId == -1) {
            Toast.makeText(this, "ID Pesanan tidak valid!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tombol Kembali
        if (btnBackReview != null) {
            btnBackReview.setOnClickListener(v -> finish());
        }

        // 3. Aksi Tombol Kirim Ulasan
        btnKirimUlasan.setOnClickListener(v -> {
            // Mengunci tombol agar tidak di-spam klik
            btnKirimUlasan.setEnabled(false);

            int ratingValue = (int) ratingBar.getRating();
            String komentarValue = etKomentar.getText() != null ? etKomentar.getText().toString().trim() : "";

            // Validasi rating wajib diisi minimal 1 bintang
            if (ratingValue < 1) {
                Toast.makeText(TulisReviewActivity.this, "Silakan berikan rating minimal 1 bintang", Toast.LENGTH_SHORT).show();
                btnKirimUlasan.setEnabled(true);
                return;
            }

            // Kirim data ke Laravel via Retrofit
            kirimUlasanKeLaravel(ratingValue, komentarValue);
        });
    }

    private void kirimUlasanKeLaravel(int rating, String komentar) {
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.submitReview(authHeader, orderId, rating, komentar).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TulisReviewActivity.this, "Ulasan berhasil dikirim, terima kasih!", Toast.LENGTH_LONG).show();

                    // Tutup halaman review dan kembali ke daftar pesanan
                    finish();
                } else {
                    // Menangkap jika review sudah pernah dibuat sebelumnya (Error 422 dari Controller)
                    if (response.code() == 422) {
                        Toast.makeText(TulisReviewActivity.this, "Anda sudah membuat ulasan untuk pesanan ini!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(TulisReviewActivity.this, "Gagal mengirim ulasan (Error " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                    btnKirimUlasan.setEnabled(true); // Buka kunci tombol jika gagal
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TulisReviewActivity.this, "Koneksi internet bermasalah", Toast.LENGTH_SHORT).show();
                btnKirimUlasan.setEnabled(true);
            }
        });
    }
}
