package com.example.preloved;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JualActivity extends AppCompatActivity {

    private TextInputEditText etNamaBarang, etHarga, etDeskripsi;
    private ImageView imgPreview;
    private Uri selectedImageUri = null;

    // Launcher untuk memilih gambar dari Galeri
    private final ActivityResultLauncher<String> pickImageLauncher =
        registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                imgPreview.setImageURI(uri);
                imgPreview.setVisibility(View.VISIBLE);
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_jual);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Inisialisasi View
        etNamaBarang = findViewById(R.id.etNamaBarang);
        etHarga = findViewById(R.id.etHarga);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        imgPreview = findViewById(R.id.imgPreview);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Klik area Upload Foto
        View btnUploadFoto = findViewById(R.id.btnUploadFoto);
        if (btnUploadFoto != null) {
            btnUploadFoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        }

        // Klik Tombol Lanjutkan (Jual Sekarang)
        MaterialButton btnLanjutkan = findViewById(R.id.btnLanjutkan);
        if (btnLanjutkan != null) {
            btnLanjutkan.setOnClickListener(v -> prosesJualBarang());
        }
    }

    private void prosesJualBarang() {
        String nama = etNamaBarang.getText().toString().trim();
        String harga = etHarga.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();

        // Validasi Sederhana
        if (nama.isEmpty() || harga.isEmpty() || deskripsi.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Harap isi semua data dan pilih 1 foto", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Sedang mengunggah barang...", Toast.LENGTH_SHORT).show();

        // Siapkan data Teks untuk dikirim secara Multipart
        RequestBody sellerIdPart = RequestBody.create(MediaType.parse("text/plain"), "1"); // Contoh statis user 1
        RequestBody categoryIdPart = RequestBody.create(MediaType.parse("text/plain"), "1"); // Kategori Pakaian
        RequestBody namaPart = RequestBody.create(MediaType.parse("text/plain"), nama);
        RequestBody hargaPart = RequestBody.create(MediaType.parse("text/plain"), harga);
        RequestBody deskripsiPart = RequestBody.create(MediaType.parse("text/plain"), deskripsi);
        RequestBody kondisiPart = RequestBody.create(MediaType.parse("text/plain"), "Baru"); // Contoh statis
        RequestBody lokasiPart = RequestBody.create(MediaType.parse("text/plain"), "Jakarta"); // Contoh statis

        // Siapkan File Gambar
        MultipartBody.Part fotoPart = null;
        try {
            File imageFile = getFileFromUri(selectedImageUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            fotoPart = MultipartBody.Part.createFormData("foto", imageFile.getName(), requestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Hit API
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.uploadProduct(
            sellerIdPart, categoryIdPart, namaPart, deskripsiPart,
            hargaPart, kondisiPart, lokasiPart, fotoPart
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(JualActivity.this, "Berhasil! Produk siap dijual.", Toast.LENGTH_LONG).show();
                    finish(); // Kembali ke MainActivity
                } else {
                    Toast.makeText(JualActivity.this, "Gagal mengunggah produk.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(JualActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fungsi bantuan untuk mengubah Uri (dari Galeri) menjadi File nyata agar bisa dikirim Retrofit
    private File getFileFromUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());
        tempFile.deleteOnExit();
        OutputStream out = new FileOutputStream(tempFile);
        if (inputStream != null) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            inputStream.close();
            out.close();
        }
        return tempFile;
    }
}
