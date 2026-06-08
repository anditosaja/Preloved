package com.example.preloved;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {

    private TextView txtNama, txtUsername, txtRating, txtUlasan, txtStatRating, txtStatusVerifikasi;
    private Button btnAksiVerifikasi;
    private String token;

    // Gunakan alamat IP emulator yang mengarah ke localhost Laravel lo
    private static final String URL_GET_PROFILE = "http://10.0.2.2:8000/api/user";

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
        txtStatusVerifikasi = findViewById(R.id.txtStatusVerifikasi);
        btnAksiVerifikasi = findViewById(R.id.btnAksiVerifikasi);

        // Ambil token login yang disimpan di SharedPreferences pas login berhasil
        SharedPreferences sharedPreferences = getSharedPreferences("PrelovedPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("AUTH_TOKEN", "");

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

        // Contoh klik Menu Barang Saya
        findViewById(R.id.menuBarangSaya).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfilActivity.this, "Membuka Barang Saya", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataProfilDariLaravel() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            URL_GET_PROFILE,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Ambil data objek JSON hasil respon ProfileController Laravel
                        String namaLengkap = response.getString("nama_lengkap");
                        String username = response.getString("username");
                        double rating = response.getDouble("rating");
                        int jumlahUlasan = response.getInt("jumlah_ulasan");
                        int isVerified = response.getInt("is_verified"); // boolean dibaca 0 / 1 oleh JSON

                        // Set nilai ke UI secara dinamis
                        txtNama.setText(namaLengkap);
                        txtUsername.setText("@" + username);
                        txtRating.setText(String.valueOf(rating));
                        txtStatRating.setText(String.valueOf(rating));
                        txtUlasan.setText(" (" + jumlahUlasan + ")");

                        // Cek Status Verifikasi
                        if (isVerified == 1) {
                            txtStatusVerifikasi.setText("Akun Terverifikasi");
                            btnAksiVerifikasi.setVisibility(View.GONE); // Sembunyikan tombol kalau sudah aman
                        } else {

                                txtStatusVerifikasi.setText("Akun Belum Terverifikasi");
                                btnAksiVerifikasi.setVisibility(View.VISIBLE); // <--- Diubah jadi VISIBLE biar tombolnya muncul normal
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
            // Mengirimkan Token Bearer ke API Laravel Auth middleware
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
