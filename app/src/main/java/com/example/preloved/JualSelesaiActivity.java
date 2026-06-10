package com.example.preloved;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.preloved.models.ImageResponse;
import com.example.preloved.models.ProductRequest;
import com.example.preloved.models.ProductResponse;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JualSelesaiActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnJualSekarang;

    private String passedImageUri;
    private ProductRequest finalRequestData;

    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jual_selesai);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Tangkap Data yang sudah terisi penuh dari Step 1 sampai 4
        if (getIntent() != null) {
            passedImageUri = getIntent().getStringExtra("IMAGE_URI");
            finalRequestData = (ProductRequest) getIntent().getSerializableExtra("PRODUCT_DATA");
        }

        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        btnBack = findViewById(R.id.btnBack);
        btnJualSekarang = findViewById(R.id.btnJualSekarang);

        btnBack.setOnClickListener(v -> finish());

        btnJualSekarang.setOnClickListener(v -> prosesIklankanProduk());
    }

    private void prosesIklankanProduk() {
        btnJualSekarang.setEnabled(false);
        btnJualSekarang.setText("MENGIRIM DATA...");

        String token = sessionManager.getBearerToken();

        apiService.createProduct(token, finalRequestData).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int productId = response.body().getProduct().getProductId();
                    btnJualSekarang.setText("MENGUNGGAH FOTO...");
                    prosesUploadFoto(productId);
                } else {
                    btnJualSekarang.setEnabled(true);
                    btnJualSekarang.setText("JUAL SEKARANG");

                    // BONGKAR PESAN ERROR DARI LARAVEL
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error Tidak Diketahui";

                        // Cetak ke Logcat dengan warna merah agar mudah dicari
                        android.util.Log.e("API_ERROR_PRELOVED", "Gagal POST Produk: " + errorBody);

                        // Tampilkan di layar HP
                        Toast.makeText(JualSelesaiActivity.this, "Penolakan Server: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                btnJualSekarang.setEnabled(true);
                btnJualSekarang.setText("JUAL SEKARANG");
                Toast.makeText(JualSelesaiActivity.this, "Koneksi jaringan gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prosesUploadFoto(int productId) {
        Uri imageUri = Uri.parse(passedImageUri);
        File fileGambar = getFileFromUri(imageUri);

        if (fileGambar == null) {
            Toast.makeText(this, "Gagal memproses file foto untuk diunggah", Toast.LENGTH_SHORT).show();
            btnJualSekarang.setEnabled(true);
            btnJualSekarang.setText("JUAL SEKARANG");
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileGambar);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", fileGambar.getName(), requestFile);

        String token = sessionManager.getBearerToken();

        apiService.uploadProductImage(token, productId, body).enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(JualSelesaiActivity.this, "Hore! Barang berhasil diiklankan", Toast.LENGTH_LONG).show();
                    kembaliKeBeranda();
                } else {
                    btnJualSekarang.setEnabled(true);
                    btnJualSekarang.setText("JUAL SEKARANG");
                    Toast.makeText(JualSelesaiActivity.this, "Data produk berhasil, namun gagal unggah foto", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                btnJualSekarang.setEnabled(true);
                btnJualSekarang.setText("JUAL SEKARANG");
                Toast.makeText(JualSelesaiActivity.this, "Gagal unggah foto (Timeout): " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "preloved_upload_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void kembaliKeBeranda() {
        Intent intent = new Intent(JualSelesaiActivity.this, MainActivity.class);
        // FLAG_ACTIVITY_CLEAR_TOP akan menutup semua history Stepper sehingga user tidak bisa tombol "Back" ke pengisian form
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
