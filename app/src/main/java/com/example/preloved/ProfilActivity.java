package com.example.preloved;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.net.Uri;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.example.preloved.network.ApiService;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfilActivity extends AppCompatActivity {

    private TextView txtNama, txtUsername, txtRating, txtUlasan, txtStatRating;
    private String token;
    private TextView txtSaldoProfil, txtEmailProfil;
    private ShapeableImageView imgFotoProfil;

    // Gunakan alamat IP emulator yang mengarah ke localhost Laravel lo
    private static final String URL_GET_PROFILE = "http://10.255.149.23:8000/api/profile";

    private ActivityResultLauncher<String> bukaGaleri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Inisialisasi Komponen View Visual
        txtNama = findViewById(R.id.txtNamaProfil);
        txtUsername = findViewById(R.id.txtUsernameProfil);
        txtRating = findViewById(R.id.txtRatingProfil);
        txtUlasan = findViewById(R.id.txtJumlahUlasan);
        txtStatRating = findViewById(R.id.txtStatRating);
        txtSaldoProfil = findViewById(R.id.txtSaldoProfil);
        txtEmailProfil = findViewById(R.id.txtEmailProfil);
        imgFotoProfil = findViewById(R.id.imgFotoProfil);

        // Ganti bagian SharedPreferences di ProfilActivity dengan ini:
        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getToken(); // Pakai fungsi dari SessionManager kita

        // 1. Inisialisasi Launcher Galeri
        bukaGaleri = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Tampilkan sementara di layar agar responsif
                    imgFotoProfil.setImageURI(uri);

                    // Proses upload ke Laravel
                    uploadFotoKeLaravel(uri);
                }
            }
        );

        // 2. Klik foto profil untuk buka galeri
        imgFotoProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hanya tampilkan file bertipe gambar
                bukaGaleri.launch("image/*");
            }
        });

        if (token.isEmpty()) {
            Toast.makeText(this, "Token kosong! Silakan login ulang.", Toast.LENGTH_LONG).show();
            // Opsional: Langsung arahkan (Intent) kembali ke LoginActivity
            return; // Hentikan proses load data
        } else {
            Log.d("TOKEN_CEK", "Token yang dikirim: " + token);
        }

        // Ambil data terbaru dari Server Laravel
        loadDataProfilDariLaravel();

        // TOMBOL BACK
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        // =========================================================
        // KLIK MENU BARANG SAYA
        // =========================================================
        findViewById(R.id.menuBarangSaya).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman BarangSayaActivity
                Intent intent = new Intent(ProfilActivity.this, BarangSayaActivity.class);
                startActivity(intent);
            }
        });

        // =========================================================
        // KLIK MENU SALDO PRELOVED (MASUK KE TOP UP)
        // =========================================================
        findViewById(R.id.menuSaldo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, TopUpActivity.class);
                startActivity(intent);
            }
        });

        // =========================================================
        // KLIK MENU LOGOUT (KELUAR)
        // =========================================================
        findViewById(R.id.menuLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Panggil SessionManager
                SessionManager sessionManager = new SessionManager(getApplicationContext());

                // 2. Hapus token dan semua data sesi
                sessionManager.logout();

                Toast.makeText(ProfilActivity.this, "Berhasil keluar", Toast.LENGTH_SHORT).show();

                // 3. Pindah ke LoginActivity dan HAPUS semua history halaman
                Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                // 4. Tutup halaman profil ini
                finish();
            }
        });
    }

    // =========================================================
    // REFRESH DATA SAAT KEMBALI KE HALAMAN INI
    // =========================================================
    @Override
    protected void onResume() {
        super.onResume();
        // Kalau tokennya ada, panggil ulang data profil supaya saldo terbaru muncul
        if (token != null && !token.isEmpty()) {
            loadDataProfilDariLaravel();
        }
    }

    private void uploadFotoKeLaravel(Uri fileUri) {
        try {
            // 1. Salin gambar dari Galeri ke Cache Sementara
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            File tempFile = new File(getCacheDir(), "foto_profil.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            if (inputStream != null) inputStream.close();

            // 2. Siapkan file sebagai "MultipartBody.Part" sesuai permintaan Laravel
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("photo", tempFile.getName(), requestFile);

            // 3. Panggil Retrofit (Sesuaikan nama kelas Retrofit Client kamu)
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<ResponseBody> call = apiService.uploadFotoProfil("Bearer " + token, body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfilActivity.this, "Foto berhasil diunggah!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfilActivity.this, "Gagal upload: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(ProfilActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memproses gambar dari galeri", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataProfilDariLaravel() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            URL_GET_PROFILE,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        // Ambil data dari objek jsonObject
                        String namaLengkap = jsonObject.getString("nama_lengkap");
                        String username = jsonObject.getString("username");
                        String email = jsonObject.optString("email", "Email tidak tersedia");
                        String fotoProfil = jsonObject.optString("foto_profil", "");
                        double rating = jsonObject.optDouble("rating", 0.0);
                        int jumlahUlasan = jsonObject.optInt("jumlah_ulasan", 0);
                        int saldo = jsonObject.optInt("balance", 0);

                        // Set nilai ke UI
                        txtNama.setText(namaLengkap);
                        txtUsername.setText("@" + username);
                        txtRating.setText(String.valueOf(rating));
                        txtStatRating.setText(String.valueOf(rating));
                        txtUlasan.setText(" (" + jumlahUlasan + ")");
                        txtSaldoProfil.setText("Rp " + saldo);
                        txtEmailProfil.setText(email);

                        if (!fotoProfil.isEmpty() && !fotoProfil.equals("null")) {
                            String imageUrl = "http://10.255.149.23:8000/storage/" + fotoProfil;
                            Glide.with(ProfilActivity.this)
                                .load(imageUrl)
                                .circleCrop() // Opsional: Biar fotonya otomatis bulat
                                .into(imgFotoProfil);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfilActivity.this, "Gagal memproses data user", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ProfilActivity.this, "Gagal koneksi ke server: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }
}
