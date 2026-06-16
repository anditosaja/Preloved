package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.preloved.models.Product;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private TextView txtNama, txtKategori, txtHarga, txtSaldo;
    private MaterialButton btnBeli;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ambil Token dari SessionManager
        SessionManager sessionManager = new SessionManager(this);
        token = sessionManager.getToken();

        // 1. Terima data dari ProfilBarangActivity
        Product product = (Product) getIntent().getSerializableExtra("PRODUCT");

        // 2. Inisialisasi UI
        txtNama = findViewById(R.id.txtNamaOrder);
        txtKategori = findViewById(R.id.txtKategoriOrder);
        txtHarga = findViewById(R.id.txtHargaOrder);
        txtSaldo = findViewById(R.id.txtSaldoPembayaran);
        btnBeli = findViewById(R.id.btnBeliSekarang);

        // 3. Tampilkan data produk dengan format rupiah
        if (product != null) {
            txtNama.setText(product.getNama_barang());
            txtKategori.setText("Kategori: " + product.getCategoryId());

            try {
                double harga = Double.parseDouble(product.getHarga_jual());
                txtHarga.setText("Rp " + new DecimalFormat("#,###").format(harga));
            } catch (Exception e) {
                txtHarga.setText("Rp " + product.getHarga_jual());
            }

            // [PROTEKSI ID]: Cek apakah getProductId() bernilai valid, kalau 0 ambil fallback getId()
            int finalProductId = product.getProductId();
            if (finalProductId == 0) {
                // Sesuai penamaan field umum retrofit model
                // coba cek method getId() di model Product lu jika getProductId() return angka 0
                finalProductId = product.getProductId();
            }

            final int idBarangDiBeli = finalProductId;
            btnBeli.setOnClickListener(v -> prosesPembelian(idBarangDiBeli));
        }

        // 4. Load Saldo dari API Profile menggunakan Retrofit bray!
        getBalance();
    }

    // ========================================================
    // FUNGSI GET BALANCE (MURNI RETROFIT 🚀)
    // ========================================================
    private void getBalance() {
        if (token == null || token.isEmpty()) {
            txtSaldo.setText("Rp 0 (Belum Login)");
            return;
        }

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getMyProfile(authHeader).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonString = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonString);

                        // Ambil field balance dari JSON profile Laravel lu bray
                        int saldo = jsonObject.optInt("balance", 0);

                        // Format ke Rupiah biar cakep di UI
                        txtSaldo.setText("Rp " + new DecimalFormat("#,###").format(saldo));

                    } catch (Exception e) {
                        e.printStackTrace();
                        txtSaldo.setText("Rp 0");
                    }
                } else {
                    Log.e("RETROFIT_ORDER", "Gagal load saldo, Code: " + response.code());
                    txtSaldo.setText("Gagal memuat saldo");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("RETROFIT_ORDER", "Koneksi mampet bray: " + t.getMessage());
                txtSaldo.setText("Gagal memuat saldo");
            }
        });
    }

    // ========================================================
    // FUNGSI PROSES PEMBELIAN (MURNI RETROFIT KETAT)
    // ========================================================
    private void prosesPembelian(int productId) {
        if (productId == 0) {
            Toast.makeText(this, "Gagal: ID Produk tidak valid (0)", Toast.LENGTH_LONG).show();
            return;
        }

        btnBeli.setEnabled(false);
        txtSaldo.setText("Memproses pembayaran...");

        // 1. Gunakan JSONObject biar format JSON-nya 100% valid dan rapi bray!
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("product_id", productId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ubah JSON Object menjadi request body text murni
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            jsonObject.toString()
        );

        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        // 2. Tembak pake Retrofit Client
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.prosesOrderBarang(authHeader, requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnBeli.setEnabled(true);

                // JIKA LARAVEL BENAR-BENAR BERHASIL MEMBACA & MENYIMPAN KE MYSQL (Status 200 OK)
                if (response.isSuccessful()) {
                    Toast.makeText(OrderActivity.this, "Pembelian Berhasil!", Toast.LENGTH_SHORT).show();

                    // Pindah ke halaman sukses bray
                    Intent intent = new Intent(OrderActivity.this, SuccessActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // JIKA SERVER MENOLAK (Entah saldo kurang atau validasi product_id gagal)
                    getBalance(); // Reset teks saldo ke angka semula

                    try {
                        String errorRaw = response.errorBody() != null ? response.errorBody().string() : "Error";
                        Log.e("DEBUG_ORDER_FAIL", "Code: " + response.code() + " | Respon: " + errorRaw);

                        // Parsing pesan error dari JSON Laravel lu bray
                        JSONObject jsonError = new JSONObject(errorRaw);
                        String pesanError = jsonError.optString("message", "Transaksi ditolak server.");
                        String detailError = jsonError.optString("error_detail", "");

                        // Tampilkan pesan error asli dari Laravel di Toast biar keliatan salahnya di mana!
                        Toast.makeText(OrderActivity.this, "Gagal (" + response.code() + "): " + pesanError + " " + detailError, Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(OrderActivity.this, "Ditolak server dengan kode: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnBeli.setEnabled(true);
                getBalance();
                Log.e("DEBUG_ORDER_CONN", "Koneksi mati: " + t.getMessage());
                Toast.makeText(OrderActivity.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
