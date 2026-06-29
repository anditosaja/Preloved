package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.preloved.adapters.UserListAdapter;
import com.example.preloved.models.UserChatResponse;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvUserList = findViewById(R.id.rvUserList);
        rvUserList.setLayoutManager(new LinearLayoutManager(this));

        // 1. Panggil API untuk memuat daftar chat
        ambilDataChatDariLaravel();

        // 2. Panggil fungsi untuk mengaktifkan tombol navigasi bawah
        aturNavigasi();
    }

    private void ambilDataChatDariLaravel() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) return;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getGlobalUsers("Bearer " + token).enqueue(new Callback<List<UserChatResponse>>() {
            @Override
            public void onResponse(Call<List<UserChatResponse>> call, Response<List<UserChatResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    UserListAdapter adapter = new UserListAdapter(response.body(), user -> {
                        Intent intent = new Intent(ChatActivity.this, ChatActivityDetail.class);
                        intent.putExtra("NAMA_PENGIRIM", user.getNamaLengkap());
                        intent.putExtra("RECEIVER_ID", user.getUserId());
                        startActivity(intent);
                    });

                    rvUserList.setAdapter(adapter);
                } else {
                    Log.e("CEK_CHAT", "Gagal load data. Kode Error: " + response.code());
                    Toast.makeText(ChatActivity.this, "Gagal memuat chat (Error " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserChatResponse>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Koneksi gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- FUNGSI MENGHIDUPKAN TOMBOL NAVIGASI BAWAH ---
    private void aturNavigasi() {
        LinearLayout navBeranda = findViewById(R.id.navBeranda);
        LinearLayout navKategori = findViewById(R.id.navKategori);
        LinearLayout navJual = findViewById(R.id.navJual);
        LinearLayout navChat = findViewById(R.id.navChat);
        LinearLayout navProfil = findViewById(R.id.navProfil);

        if (navBeranda != null) {
            navBeranda.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish(); // Tutup halaman chat agar kembali ke Beranda dengan bersih
            });
        }

        if (navKategori != null) {
            navKategori.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, KategoriActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (navJual != null) {
            navJual.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, JualActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        if (navChat != null) {
            navChat.setOnClickListener(v -> {
                // Sedang berada di halaman Chat, jadi tidak perlu ngapa-ngapain
            });
        }

        if (navProfil != null) {
            navProfil.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, ProfilActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }
}
