package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    // Deklarasi variabel komponen UI
    private TextInputEditText etNama, etEmail, etPassword;
    private MaterialButton btnDaftar;
    private TextView txtKeLogin;
    private ImageButton btnHelp;

    // PENTING: Gunakan 10.0.2.2 untuk memanggil localhost XAMPP dari Emulator Android
    private static final String URL_REGISTER = "http://10.0.2.2/api_android/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Menghubungkan variabel dengan ID di activity_register.xml
        etNama = findViewById(R.id.editTextNama);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnDaftar = findViewById(R.id.btnDaftar);
        txtKeLogin = findViewById(R.id.txtKeLogin);
        btnHelp = findViewById(R.id.btnHelp);

        // Aksi ketika tombol DAFTAR SEKARANG diklik
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etNama.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Cek apakah ada kolom yang kosong
                if (!nama.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(nama, email, password);
                } else {
                    Toast.makeText(Register.this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Aksi ketika teks "Masuk" diklik (Pindah ke halaman Login)
        txtKeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish(); // Tutup halaman register agar memori tidak menumpuk
            }
        });

        // Aksi untuk tombol tanda tanya (Help) di pojok kanan atas
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Register.this, "Bantuan belum tersedia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi untuk mengirim data ke Database lewat PHP API
    private void registerUser(final String nama, final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Mengubah respons PHP menjadi JSON
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");

                            if (status.equals("success")) {
                                Toast.makeText(Register.this, message, Toast.LENGTH_SHORT).show();

                                // Jika sukses daftar, langsung arahkan ke halaman Login
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Jika gagal (misal: email sudah dipakai)
                                Toast.makeText(Register.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Register.this, "Error membaca data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Menangkap error aslinya agar tidak menampilkan "null"
                        String pesanError = error.getMessage() != null ? error.getMessage() : error.toString();
                        Toast.makeText(Register.this, "Koneksi Error: " + pesanError, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Proses mengirim parameter ke POST PHP
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}