package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preloved.adapters.ChatAdapter;
import com.example.preloved.models.ChatMessage;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.network.ApiService;
import com.example.preloved.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivityDetail extends AppCompatActivity {

    private int chatId;
    private int targetReceiverId;
    private int currentUserId;
    private String namaPengirim;

    private TextView tvTitleNamaToolbar;
    private EditText etKetikPesan;
    private ImageView btnKirimPesan;

    private RecyclerView rvChatArea;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> listPesan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);

        View mainView = findViewById(R.id.main);
        if (mainView == null) mainView = findViewById(android.R.id.content);

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // 1. Ambil Data dari Session & Intent
        SessionManager sm = new SessionManager(this);
        currentUserId = sm.getUserId();

        chatId = getIntent().getIntExtra("CHAT_ID", 0);
        namaPengirim = getIntent().getStringExtra("NAMA_PENGIRIM");
        targetReceiverId = getIntent().getIntExtra("RECEIVER_ID", 0);

        // 2. Inisialisasi Nama di Toolbar
        tvTitleNamaToolbar = findViewById(R.id.tvTitleNamaToolbar);
        if (tvTitleNamaToolbar != null && namaPengirim != null) {
            tvTitleNamaToolbar.setText(namaPengirim);
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // 3. Setup RecyclerView untuk isi chat bubble
        rvChatArea = findViewById(R.id.rvChatArea);
        chatAdapter = new ChatAdapter(listPesan, currentUserId);
        rvChatArea.setLayoutManager(new LinearLayoutManager(this));
        rvChatArea.setAdapter(chatAdapter);

        // 4. Setup Tombol Kirim (Ambil langsung via index susunan XML kamu)
        LinearLayout inputBarLayout = findViewById(R.id.inputBar);
        if (inputBarLayout != null) {
            try {
                LinearLayout llDalam = (LinearLayout) inputBarLayout.getChildAt(0);
                etKetikPesan = (EditText) llDalam.getChildAt(0);
                btnKirimPesan = (ImageView) inputBarLayout.getChildAt(1);

                if (btnKirimPesan != null) {
                    btnKirimPesan.setOnClickListener(v -> {
                        String teksPesan = etKetikPesan.getText().toString().trim();
                        if (!teksPesan.isEmpty()) {
                            eksekusiKirimPesan(teksPesan, etKetikPesan);
                        } else {
                            Toast.makeText(this, "Pesan kosong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("ChatDetail", "Gagal mapping inputBar: " + e.getMessage());
            }
        }

        loadIsiPercakapan();
    }

    private void loadIsiPercakapan() {
        SessionManager sm = new SessionManager(this);
        String token = "Bearer " + sm.getToken();

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.getChatDetail(token, targetReceiverId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonResponse = response.body().string();
                        Log.d("ChatDetail", "Respon Pesan: " + jsonResponse);

                        JSONArray jsonArray = new JSONArray(jsonResponse);
                        listPesan.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            int senderId = obj.getInt("sender_id");
                            String messageText = obj.getString("message");
                            String time = obj.optString("created_at", "");

                            if (time.length() > 16) {
                                time = time.substring(11, 16);
                            }

                            ChatMessage msg = new ChatMessage(senderId, messageText, time);
                            listPesan.add(msg);
                        }

                        chatAdapter.notifyDataSetChanged();
                        if (listPesan.size() > 0) {
                            rvChatArea.scrollToPosition(listPesan.size() - 1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ChatActivityDetail.this, "Gagal memproses obrolan", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ChatDetail", "Error: " + t.getMessage());
            }
        });
    }

    private void eksekusiKirimPesan(String pesan, EditText editText) {
        SessionManager sm = new SessionManager(this);
        String token = "Bearer " + sm.getToken();
        int productId = getIntent().getIntExtra("PRODUCT_ID", 0);

        // LOG UNTUK KROSTEK VALUE
        Log.d("DEBUG_KIRIM", "Token: " + token);
        Log.d("DEBUG_KIRIM", "Target Receiver ID: " + targetReceiverId);
        Log.d("DEBUG_KIRIM", "Isi Pesan: " + pesan);

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        api.kirimPesan(token, productId, targetReceiverId, pesan).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("DEBUG_KIRIM", "Respon Server: Sukses Terkirim!");
                    editText.setText("");
                    loadIsiPercakapan();
                } else {
                    // Log jika server menolak (misal error 422 atau 500)
                    Log.e("DEBUG_KIRIM", "Gagal Kirim. Kode Error Server: " + response.code());
                    Toast.makeText(ChatActivityDetail.this, "Gagal mengirim: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("DEBUG_KIRIM", "Failure Koneksi: " + t.getMessage());
                Toast.makeText(ChatActivityDetail.this, "Error koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
