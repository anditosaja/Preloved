package com.example.preloved;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

        // 1. Menangkap ID chat room hasil operan dari halaman List Chat
        chatId = getIntent().getIntExtra("CHAT_ID", 0);
        namaPengirim = getIntent().getStringExtra("NAMA_PENGIRIM");

        // 2. Set nama pengirim secara otomatis pada judul toolbar atas
        tvTitleNamaToolbar = findViewById(R.id.tvTitleNamaToolbar);
        if (tvTitleNamaToolbar != null && namaPengirim != null) {
            tvTitleNamaToolbar.setText(namaPengirim);
        }

        // Fungsionalitas Tombol Kembali
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 3. Ambil data riwayat percakapan dari API database MySQL sesuai chat_id
        loadIsiPercakapan();
    }

    private void loadIsiPercakapan() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getChatDetail(chatId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Berhasil menarik data pesan individu ('isi_pesan', 'waktu_kirim')
                    // Data JSON mentah tinggal disalurkan ke adapter RecyclerView chatroom kelompokmu
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ChatActivityDetail.this, "Gagal memuat detail riwayat chat", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
