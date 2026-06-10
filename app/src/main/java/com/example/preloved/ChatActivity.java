package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.preloved.adapters.ChatAdapter; // Sesuaikan jika nama adaptermu beda
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

        ambilDataChatDariLaravel();
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

                    // Variabel 'adapter' dideklarasikan di sini saja
                    UserListAdapter adapter = new UserListAdapter(response.body(), user -> {
                        Intent intent = new Intent(ChatActivity.this, ChatActivityDetail.class);
                        intent.putExtra("NAMA_PENGIRIM", user.getNamaLengkap());
                        intent.putExtra("RECEIVER_ID", user.getUserId());
                        startActivity(intent);
                    });

                    rvUserList.setAdapter(adapter); // rvUserList harus sudah di-findViewById
                }
            }

            @Override
            public void onFailure(Call<List<UserChatResponse>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Koneksi gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
