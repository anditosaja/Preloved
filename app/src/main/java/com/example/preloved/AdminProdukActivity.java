package com.example.preloved;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preloved.models.ApiResponse;
import com.example.preloved.models.Category;
import com.example.preloved.models.Product;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProdukActivity extends AppCompatActivity {

    private RecyclerView rvProduk;
    private TextView tvTotalProduk, tvEmptyProduk;
    private SessionManager sessionManager;
    private ProdukAdapter adapter;
    private final List<Product> daftarProduk = new ArrayList<>();

    /** Lookup categoryId -> nama_kategori, dimuat sekali dari /api/categories */
    private final Map<Integer, String> namaKategoriById = new HashMap<>();

    /** null = semua, "available" = aktif, "ditangguhkan" = menunggu, "ditolak" = ditolak */
    private String filterStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_admin);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isAdmin() || sessionManager.getAdminToken() == null) {
            redirectToLogin();
            return;
        }

        tvTotalProduk = findViewById(R.id.tvTotalProduk);
        tvEmptyProduk = findViewById(R.id.tvEmptyProduk);
        rvProduk = findViewById(R.id.rvProduk);

        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdukAdapter(daftarProduk);
        rvProduk.setAdapter(adapter);

        if (findViewById(R.id.btnMenu) != null) {
            findViewById(R.id.btnMenu).setOnClickListener(v -> finish());
        }

        setupTabs();
        setupBottomNav();
        loadKategori();
        loadProduk();
    }

    /**
     * Ambil seluruh kategori sekali saat halaman dibuka, supaya adapter bisa
     * menampilkan nama kategori asli (Product hanya menyimpan categoryId).
     */
    private void loadKategori() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    namaKategoriById.clear();
                    for (Category kategori : response.body()) {
                        namaKategoriById.put(kategori.getCategory_id(), kategori.getNama_kategori());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // Kalau gagal, adapter tetap fallback menampilkan "Kategori #ID".
            }
        });
    }

    private void setupTabs() {
        View tabSemua = findViewById(R.id.tabProdukSemua);
        View tabAktif = findViewById(R.id.tabProdukAktif);
        View tabMenunggu = findViewById(R.id.tabProdukMenunggu);
        View tabDitolak = findViewById(R.id.tabProdukDitolak);

        if (tabSemua != null) tabSemua.setOnClickListener(v -> { filterStatus = null; loadProduk(); });
        if (tabAktif != null) tabAktif.setOnClickListener(v -> { filterStatus = "available"; loadProduk(); });
        if (tabMenunggu != null) tabMenunggu.setOnClickListener(v -> { filterStatus = "ditangguhkan"; loadProduk(); });
        if (tabDitolak != null) tabDitolak.setOnClickListener(v -> { filterStatus = "ditolak"; loadProduk(); });
    }

    private void loadProduk() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        Call<ApiResponse<List<Product>>> call = apiService.getAdminProducts(bearerToken, filterStatus, null);

        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    daftarProduk.clear();
                    daftarProduk.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();

                    tvTotalProduk.setText("Total " + daftarProduk.size() + " produk");
                    tvEmptyProduk.setVisibility(daftarProduk.isEmpty() ? View.VISIBLE : View.GONE);
                } else if (response.code() == 401 || response.code() == 403) {
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                Toast.makeText(AdminProdukActivity.this, "Gagal memuat produk: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAksiProduk(Product product) {
        String[] opsi = {"Setujui / Aktifkan", "Tangguhkan", "Tolak", "Hapus"};

        new AlertDialog.Builder(this)
            .setTitle(product.getNama_barang())
            .setItems(opsi, (dialog, which) -> {
                switch (which) {
                    case 0:
                        approveProduk(product);
                        break;
                    case 1:
                        mintaCatatanLaluKirim(product, true);
                        break;
                    case 2:
                        mintaCatatanLaluKirim(product, false);
                        break;
                    case 3:
                        confirmHapus(product);
                        break;
                }
            })
            .show();
    }

    private void mintaCatatanLaluKirim(Product product, boolean isSuspend) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Catatan untuk penjual (opsional)");

        new AlertDialog.Builder(this)
            .setTitle(isSuspend ? "Tangguhkan Produk" : "Tolak Produk")
            .setView(input)
            .setPositiveButton("Kirim", (dialog, which) -> {
                String catatan = input.getText().toString().trim();
                if (isSuspend) {
                    suspendProduk(product, catatan);
                } else {
                    rejectProduk(product, catatan);
                }
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void approveProduk(Product product) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        apiService.approveProduct(bearerToken, product.getProductId()).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                Toast.makeText(AdminProdukActivity.this, "Produk disetujui", Toast.LENGTH_SHORT).show();
                loadProduk();
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                Toast.makeText(AdminProdukActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void suspendProduk(Product product, String catatan) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        apiService.suspendProduct(bearerToken, product.getProductId(), catatan).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                Toast.makeText(AdminProdukActivity.this, "Produk ditangguhkan", Toast.LENGTH_SHORT).show();
                loadProduk();
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                Toast.makeText(AdminProdukActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void rejectProduk(Product product, String catatan) {
        if (catatan.isEmpty()) {
            Toast.makeText(this, "Catatan alasan penolakan wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        apiService.rejectProduct(bearerToken, product.getProductId(), catatan).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                Toast.makeText(AdminProdukActivity.this, "Produk ditolak", Toast.LENGTH_SHORT).show();
                loadProduk();
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                Toast.makeText(AdminProdukActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmHapus(Product product) {
        new AlertDialog.Builder(this)
            .setTitle("Hapus Produk")
            .setMessage("Produk \"" + product.getNama_barang() + "\" akan dihapus permanen. Lanjutkan?")
            .setPositiveButton("Hapus", (dialog, which) -> hapusProduk(product))
            .setNegativeButton("Batal", null)
            .show();
    }

    private void hapusProduk(Product product) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        apiService.deleteProduct(bearerToken, product.getProductId()).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                Toast.makeText(AdminProdukActivity.this, "Produk dihapus", Toast.LENGTH_SHORT).show();
                loadProduk();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(AdminProdukActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupBottomNav() {
        LinearLayout navDashboard = findViewById(R.id.navAdminDashboard);
        LinearLayout navTransaksi = findViewById(R.id.navAdminTransaksi);
        LinearLayout navKomplain = findViewById(R.id.navAdminKomplain);
        LinearLayout navPengguna = findViewById(R.id.navAdminPengguna);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                startActivity(new Intent(AdminProdukActivity.this, AdminMainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navTransaksi != null) {
            navTransaksi.setOnClickListener(v -> {
                startActivity(new Intent(AdminProdukActivity.this, AdminTransaksiActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navKomplain != null) {
            navKomplain.setOnClickListener(v -> {
                startActivity(new Intent(AdminProdukActivity.this, AdminKomplainActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navPengguna != null) {
            navPengguna.setOnClickListener(v -> {
                startActivity(new Intent(AdminProdukActivity.this, AdminPenggunaActivity.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }

    private void redirectToLogin() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ViewHolder> {

        private final List<Product> data;

        ProdukAdapter(List<Product> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk_admin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product produk = data.get(position);

            holder.tvNama.setText(produk.getNama_barang());
            String namaKategori = namaKategoriById.get(produk.getCategoryId());
            holder.tvKategori.setText(namaKategori != null ? namaKategori : "Kategori #" + produk.getCategoryId());
            holder.tvHarga.setText("Rp" + produk.getHarga_jual());
            holder.tvPenjual.setText("Penjual: " + (produk.getSeller() != null ? "@" + produk.getSeller().getName() : "-"));

            String status = produk.getStatus_barang() != null ? produk.getStatus_barang() : "available";
            holder.tvStatus.setText(labelStatus(status));

            switch (status) {
                case "ditangguhkan":
                    holder.cardBadge.setCardBackgroundColor(0xFFFFF3E0);
                    holder.tvStatus.setTextColor(0xFFFF9800);
                    break;
                case "ditolak":
                    holder.cardBadge.setCardBackgroundColor(0xFFFFEBEE);
                    holder.tvStatus.setTextColor(0xFFF44336);
                    break;
                case "sold":
                    holder.cardBadge.setCardBackgroundColor(0xFFE3F2FD);
                    holder.tvStatus.setTextColor(0xFF2196F3);
                    break;
                default:
                    holder.cardBadge.setCardBackgroundColor(0xFFE8F5E9);
                    holder.tvStatus.setTextColor(0xFF4CAF50);
                    break;
            }

            holder.btnMore.setOnClickListener(v -> showAksiProduk(produk));
            holder.itemView.setOnClickListener(v -> showAksiProduk(produk));
        }

        private String labelStatus(String status) {
            switch (status) {
                case "ditangguhkan": return "Menunggu";
                case "ditolak": return "Ditolak";
                case "sold": return "Terjual";
                case "reserved": return "Dipesan";
                default: return "Aktif";
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNama, tvKategori, tvHarga, tvPenjual, tvStatus;
            ImageView btnMore;
            com.google.android.material.card.MaterialCardView cardBadge;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNama = itemView.findViewById(R.id.tvNamaProduk);
                tvKategori = itemView.findViewById(R.id.tvKategoriProduk);
                tvHarga = itemView.findViewById(R.id.tvHargaProduk);
                tvPenjual = itemView.findViewById(R.id.tvPenjualProduk);
                tvStatus = itemView.findViewById(R.id.tvStatusProduk);
                btnMore = itemView.findViewById(R.id.btnMoreProduk);
                cardBadge = itemView.findViewById(R.id.badgeStatusProduk);
            }
        }
    }
}
