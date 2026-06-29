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
import com.example.preloved.network.Config;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNama, etEmail, etPassword;
    private MaterialButton btnDaftar;
    private TextView txtKeLogin;

    // URL API Laravel (Sesuaikan port jika berbeda, 8000 adalah bawaan php artisan serve)
    // Jika menjalankan Laravel di laptop dan ngetes via emulator, pakai 10.0.2.2
    private static final String URL_REGISTER = Config.BASE_URL + "/api/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Konek ke ID XML lo
        etNama = findViewById(R.id.editTextNama);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnDaftar = findViewById(R.id.btnDaftar);
        txtKeLogin = findViewById(R.id.txtKeLogin);

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etNama.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (nama.isEmpty()) {
                    etNama.setError("Nama tidak boleh kosong");
                } else if (email.isEmpty()) {
                    etEmail.setError("Email tidak boleh kosong");
                } else if (password.isEmpty()) {
                    etPassword.setError("Kata sandi tidak boleh kosong");
                } else if (password.length() < 6) {
                    etPassword.setError("Kata sandi minimal 6 karakter");
                } else {
                    // Generate username otomatis dari bagian depan email + angka random
                    // Solusi karena database & API Laravel lo mewajibkan kolom 'username' yang unik
                    String generatedUsername = email.split("@")[0] + new Random().nextInt(999);

                    Toast.makeText(RegisterActivity.this, "Memproses pendaftaran...", Toast.LENGTH_SHORT).show();
                    registerUserKeLaravel(nama, generatedUsername, email, password);
                }
            }
        });

        // Pindah ke Halaman LoginActivity
        txtKeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUserKeLaravel(String nama, String username, String email, String password) {
        btnDaftar.setEnabled(false); // Kunci tombol biar ga double click saat loading

        // Membuat objek JSON body sesuai validasi di AuthController Laravel lo
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nama_lengkap", nama);
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Pakai JsonObjectRequest karena Laravel mengekspektasikan Request Content-Type: application/json
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_REGISTER, jsonBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    btnDaftar.setEnabled(true);
                    try {
                        // Membaca response JSON dari Laravel return response()->json([...], 201);
                        String message = response.getString("message");
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                        // Lempar ke LoginActivity setelah sukses
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Gagal membaca response data", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    btnDaftar.setEnabled(true);
                    String formatError = "Pendaftaran gagal. ";

                    // Cek jika ada response error dari Laravel (misal: email unique error)
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorJsonStr = new String(error.networkResponse.data);
                            JSONObject errorObj = new JSONObject(errorJsonStr);
                            if (errorObj.has("message")) {
                                formatError = errorObj.getString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        formatError += (error.getMessage() != null) ? error.getMessage() : error.toString();
                    }

                    Toast.makeText(RegisterActivity.this, formatError, Toast.LENGTH_LONG).show();
                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Beritahu Laravel kalau data yang dikirim & diminta berbentuk JSON
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        // Jalankan Antrean Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}
