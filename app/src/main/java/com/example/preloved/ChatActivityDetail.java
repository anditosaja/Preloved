package com.example.preloved;

import android.os.Bundle;
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

import com.example.preloved.network.RetrofitClient;
import com.example.preloved.network.ApiService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivityDetail extends AppCompatActivity {

    private int chatId;
    private int mySenderId;
    private int targetReceiverId;
    private String namaPengirim;
    private TextView tvTitleNamaToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);

        View mainView = findViewById(R.id.main);
        if (mainView == null) {
            mainView = findViewById(android.R.id.content);
        }

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Tangkap data dinamis lemparan dari ChatActivity halaman depan
        chatId = getIntent().getIntExtra("CHAT_ID", 0);
        namaPengirim = getIntent().getStringExtra("NAMA_PENGIRIM");
        mySenderId = getIntent().getIntExtra("SENDER_ID", 0);
        targetReceiverId = getIntent().getIntExtra("RECEIVER_ID", 0);

        tvTitleNamaToolbar = findViewById(R.id.tvTitleNamaToolbar);
        if (tvTitleNamaToolbar != null && namaPengirim != null) {
            tvTitleNamaToolbar.setText(namaPengirim);
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Perbaikan pencarian View di dalam inputBar Layout
        LinearLayout inputBar = findViewById(R.id.inputBar);
        EditText etKetikPesan = null;
        ImageView btnKirimPesan = null;

        if (inputBar != null) {
            etKetikPesan = (android.widget.EditText) inputBar.getChildAt(0);
            btnKirimPesan = (ImageView) inputBar.getChildAt(1);
        }

        if (btnKirimPesan != null && etKetikPesan != null) {
            final EditText finalEtKetikPesan = etKetikPesan;
            btnKirimPesan.setOnClickListener(v -> {
                String teksPesan = finalEtKetikPesan.getText().toString().trim();
                if (!teksPesan.isEmpty()) {
                    eksekusiKirimPesan(teksPesan, finalEtKetikPesan);
                } else {
                    Toast.makeText(ChatActivityDetail.this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            });
        }

        loadIsiPercakapan();
    }

    private void loadIsiPercakapan() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getChatDetail(chatId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // Sisi pembacaan data riwayat sukses tersambung ke backend
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChatActivityDetail.this, "Gagal memuat riwayat chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eksekusiKirimPesan(String pesan, EditText editText) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.kirimPesan(chatId, pesan, mySenderId, targetReceiverId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChatActivityDetail.this, "Pesan terkirim!", Toast.LENGTH_SHORT).show();
                    editText.setText("");
                    loadIsiPercakapan();
                } else {
                    Toast.makeText(ChatActivityDetail.this, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChatActivityDetail.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
