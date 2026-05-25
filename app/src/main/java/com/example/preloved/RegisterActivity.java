package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNama, etEmail, etPassword;
    private MaterialButton btnDaftar;
    private TextView txtKeLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                    Toast.makeText(RegisterActivity.this, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        txtKeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}