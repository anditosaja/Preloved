package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.preloved.network.RetrofitClient;
import com.example.preloved.network.ApiService;

import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private View itemChat1;
    private TextView tvNamaUser1, tvPesanTerakhir1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // Inisialisasi komponen UI list chat
        itemChat1 = findViewById(R.id.itemChat1);
        tvNamaUser1 = findViewById(R.id.tvNamaUser1);
        tvPesanTerakhir1 = findViewById(R.id.tvPesanTerakhir1);

        // Menarik data list chat room dinamis dari Laravel MySQL
        ambilDataChatDariLaravel();

        // ====================================================================
        // BOTTOM NAVIGATION BAR
        // ====================================================================
        LinearLayout navBeranda = findViewById(R.id.navBeranda);
        if (navBeranda != null) {
            navBeranda.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout navKategori = findViewById(R.id.navKategori);
        if (navKategori != null) {
            navKategori.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, KategoriActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void ambilDataChatDariLaravel() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getChatRooms();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Semua kode parsing JSON wajib masuk ke dalam blok try {}
                    try {
                        String jsonResponse = response.body().string();
                        JSONArray jsonArray = new JSONArray(jsonResponse);

                        if (jsonArray.length() > 0) {
                            JSONObject chatObj = jsonArray.getJSONObject(0);

                            int chatId = chatObj.getInt("chat_id");
                            String pesanTerakhir = chatObj.getString("isi_pesan");
                            String namaPengirim = chatObj.optString("nama_sender", "User Preloved");

                            if (tvNamaUser1 != null) tvNamaUser1.setText(namaPengirim);
                            if (tvPesanTerakhir1 != null) tvPesanTerakhir1.setText(pesanTerakhir);

                            if (itemChat1 != null) {
                                itemChat1.setOnClickListener(v -> {
                                    Intent intent = new Intent(ChatActivity.this, ChatActivityDetail.class);
                                    intent.putExtra("CHAT_ID", chatId);
                                    intent.putExtra("NAMA_PENGIRIM", namaPengirim);
                                    startActivity(intent);
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ChatActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Gagal konek API chat: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
