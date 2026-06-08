package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnMasuk;
    private TextView txtKeRegister;

    // URL API Login Laravel (Sesuaikan port jika running php artisan serve)
    // 10.0.2.2 dipakai agar emulator Android Studio bisa mendeteksi localhost laptop lo
    private static final String URL_LOGIN = "http://10.0.2.2:8000/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Hubungkan variabel dengan ID di activity_login.xml yang lo kasih sebelumnya
        etEmail = findViewById(R.id.editTextLoginEmail);
        etPassword = findViewById(R.id.editTextLoginPassword);
        btnMasuk = findViewById(R.id.btnMasuk);
        txtKeRegister = findViewById(R.id.txtKeRegister); // Pastikan ID ini sesuai link Daftar di bagian bawah XML lo

        // 2. Aksi tombol back di pojok kiri atas
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
                } else if (password.isEmpty()) {
                    etPassword.setError("Kata sandi tidak boleh kosong");
                } else {
                    Toast.makeText(LoginActivity.this, "Mencoba masuk...", Toast.LENGTH_SHORT).show();
                    loginUserKeLaravel(email, password);
                }
            }
        });

        // 4. Aksi klik teks "Daftar" -> Pindah ke RegisterActivity yang terhubung ke Laravel
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

    private void loginUserKeLaravel(String email, String password) {
        btnMasuk.setEnabled(false); // Kunci tombol biar ga spam click pas loading
        btnMasuk.setText("MEMPROSES...");

        // Membuat objek JSON payload sesuai validasi API login Laravel lo ('email' & 'password')
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Pakai JsonObjectRequest agar header Request otomatis berupa application/json
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, jsonBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    btnMasuk.setEnabled(true);
                    btnMasuk.setText("MASUK");
                    try {
                        // Membaca response sukses JSON dari AuthController Laravel lo
                        String message = response.getString("message");
                        String token = response.getString("token"); // Token Sanctum Android

                        // Ambil data user yang dikirim Laravel jika lo butuh nama/id-nya
                        JSONObject userObj = response.getJSONObject("user");
                        String namaLengkap = userObj.getString("nama_lengkap");

                        Toast.makeText(LoginActivity.this, "Selamat datang, " + namaLengkap, Toast.LENGTH_SHORT).show();

                        // TODO: Di sini lo bisa simpan 'token' ke SharedPreferences biar user ga usah login ulang terus

                        // Pindah ke Halaman Utama (MainActivity) setelah sukses login
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Tutup halaman login

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Gagal memproses data akun", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    btnMasuk.setEnabled(true);
                    btnMasuk.setText("MASUK");
                    String pesanError = "Login Gagal. ";

                    // Cek jika ada response error 401/422 mentah dari Laravel
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorJsonStr = new String(error.networkResponse.data);
                            JSONObject errorObj = new JSONObject(errorJsonStr);
                            if (errorObj.has("message")) {
                                pesanError = errorObj.getString("message"); // Mengambil "Email atau password salah"
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        pesanError += (error.getMessage() != null) ? error.getMessage() : "Periksa koneksi internet laptop lo.";
                    }

                    Toast.makeText(LoginActivity.this, pesanError, Toast.LENGTH_LONG).show();
                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set Header wajib Laravel untuk pembacaan API JSON
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Eksekusi Volley Request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
