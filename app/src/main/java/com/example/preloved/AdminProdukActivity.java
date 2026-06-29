package com.example.preloved;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preloved.models.ApiResponse;
import com.example.preloved.models.Category;
import com.example.preloved.models.Product;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.Config;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;

import java.text.DecimalFormat;
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

    private final Map<Integer, String> namaKategoriById = new HashMap<>();
    private String filterStatus = null;

    // Deklarasi Tab
    private TextView tabSemua, tabAktif, tabMenunggu, tabDitolak;

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
            public void onFailure(Call<List<Category>> call, Throwable t) { }
        });
    }

    private void setupTabs() {
        tabSemua = findViewById(R.id.tabProdukSemua);
        tabAktif = findViewById(R.id.tabProdukAktif);
        tabMenunggu = findViewById(R.id.tabProdukMenunggu);
        tabDitolak = findViewById(R.id.tabProdukDitolak);

        if (tabSemua != null) tabSemua.setOnClickListener(v -> { filterStatus = null; updateTabUI(tabSemua); loadProduk(); });
        if (tabAktif != null) tabAktif.setOnClickListener(v -> { filterStatus = "available"; updateTabUI(tabAktif); loadProduk(); });
        if (tabMenunggu != null) tabMenunggu.setOnClickListener(v -> { filterStatus = "ditangguhkan"; updateTabUI(tabMenunggu); loadProduk(); });
        if (tabDitolak != null) tabDitolak.setOnClickListener(v -> { filterStatus = "ditolak"; updateTabUI(tabDitolak); loadProduk(); });
    }

    private void updateTabUI(TextView activeTab) {
        TextView[] allTabs = {tabSemua, tabAktif, tabMenunggu, tabDitolak};
        for (TextView tab : allTabs) {
            if (tab == null) continue;
            if (tab == activeTab) {
                ViewCompat.setBackgroundTintList(tab, ColorStateList.valueOf(android.graphics.Color.parseColor("#6952D9")));
                tab.setTextColor(android.graphics.Color.WHITE);
                tab.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                ViewCompat.setBackgroundTintList(tab, ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
                tab.setTextColor(android.graphics.Color.parseColor("#757575"));
                tab.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }
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
                    case 0: approveProduk(product); break;
                    case 1: mintaCatatanLaluKirim(product, true); break;
                    case 2: mintaCatatanLaluKirim(product, false); break;
                    case 3: confirmHapus(product); break;
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
                if (isSuspend) suspendProduk(product, catatan);
                else rejectProduk(product, catatan);
            })
            .setNegativeButton("Batal", null)
            .show();
    }

    private void approveProduk(Product product) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();
        apiService.approveProduct(bearerToken, product.getProductId()).enqueue(new Callback<ApiResponse<Product>>() {
            @Override public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) { loadProduk(); }
            @Override public void onFailure(Call<ApiResponse<Product>> call, Throwable t) { }
        });
    }

    private void suspendProduk(Product product, String catatan) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();
        apiService.suspendProduct(bearerToken, product.getProductId(), catatan).enqueue(new Callback<ApiResponse<Product>>() {
            @Override public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) { loadProduk(); }
            @Override public void onFailure(Call<ApiResponse<Product>> call, Throwable t) { }
        });
    }

    private void rejectProduk(Product product, String catatan) {
        if (catatan.isEmpty()) { Toast.makeText(this, "Catatan wajib diisi", Toast.LENGTH_SHORT).show(); return; }
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();
        apiService.rejectProduct(bearerToken, product.getProductId(), catatan).enqueue(new Callback<ApiResponse<Product>>() {
            @Override public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) { loadProduk(); }
            @Override public void onFailure(Call<ApiResponse<Product>> call, Throwable t) { }
        });
    }

    private void confirmHapus(Product product) {
        new AlertDialog.Builder(this).setTitle("Hapus Produk").setMessage("Hapus permanen?").setPositiveButton("Hapus", (d, w) -> hapusProduk(product)).setNegativeButton("Batal", null).show();
    }

    private void hapusProduk(Product product) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();
        apiService.deleteProduct(bearerToken, product.getProductId()).enqueue(new Callback<ApiResponse<Object>>() {
            @Override public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) { loadProduk(); }
            @Override public void onFailure(Call<ApiResponse<Object>> call, Throwable t) { }
        });
    }

    private void setupBottomNav() {
        LinearLayout navDashboard = findViewById(R.id.navAdminDashboard);
        LinearLayout navTransaksi = findViewById(R.id.navAdminTransaksi);
        LinearLayout navKomplain = findViewById(R.id.navAdminKomplain);
        LinearLayout navPengguna = findViewById(R.id.navAdminPengguna);

        if (navDashboard != null) navDashboard.setOnClickListener(v -> { startActivity(new Intent(this, AdminMainActivity.class)); overridePendingTransition(0, 0); finish(); });
        if (navTransaksi != null) navTransaksi.setOnClickListener(v -> { startActivity(new Intent(this, AdminTransaksiActivity.class)); overridePendingTransition(0, 0); finish(); });
        if (navKomplain != null) navKomplain.setOnClickListener(v -> { startActivity(new Intent(this, AdminKomplainActivity.class)); overridePendingTransition(0, 0); finish(); });
        if (navPengguna != null) navPengguna.setOnClickListener(v -> { startActivity(new Intent(this, AdminPenggunaActivity.class)); overridePendingTransition(0, 0); finish(); });
    }

    private void redirectToLogin() {
        sessionManager.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // ==========================================
    // ADAPTER PRODUK LOCK IN!
    // ==========================================
    private class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.ViewHolder> {
        private final List<Product> data;

        ProdukAdapter(List<Product> data) { this.data = data; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produk_admin, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product produk = data.get(position);

            holder.tvNamaProduk.setText(produk.getNama_barang());
            String namaKategori = namaKategoriById.get(produk.getCategoryId());
            holder.tvKategoriProduk.setText(namaKategori != null ? namaKategori : "Kategori #" + produk.getCategoryId());

            // Format Harga jadi ada titiknya (Rp1.000.000)
            try {
                double harga = Double.parseDouble(produk.getHarga_jual());
                holder.tvHargaProduk.setText("Rp" + new DecimalFormat("#,###").format(harga));
            } catch (Exception e) {
                holder.tvHargaProduk.setText("Rp" + produk.getHarga_jual());
            }

            holder.tvPenjualProduk.setText("Penjual: " + (produk.getSeller() != null ? "@" + produk.getSeller().getName() : "-"));

            // LOGIKA GAMBAR (PAKAI GLIDE)
            if (produk.getImages() != null && !produk.getImages().isEmpty()) {
                String imgPath = produk.getImages().get(0).getImage_path();
                if (imgPath != null) {
                    String url = imgPath.startsWith("http") ? imgPath : Config.IMAGE_URL + imgPath;
                    Glide.with(holder.itemView.getContext())
                        .load(url)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.ivProduk);
                }
            } else {
                holder.ivProduk.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            String status = produk.getStatus_barang() != null ? produk.getStatus_barang() : "available";
            holder.tvStatusProduk.setText(labelStatus(status));

            switch (status) {
                case "ditangguhkan":
                    holder.badgeStatusProduk.setCardBackgroundColor(0xFFFFF3E0);
                    holder.tvStatusProduk.setTextColor(0xFFFF9800);
                    break;
                case "ditolak":
                    holder.badgeStatusProduk.setCardBackgroundColor(0xFFFFEBEE);
                    holder.tvStatusProduk.setTextColor(0xFFF44336);
                    break;
                case "sold":
                    holder.badgeStatusProduk.setCardBackgroundColor(0xFFE3F2FD);
                    holder.tvStatusProduk.setTextColor(0xFF2196F3);
                    break;
                default:
                    holder.badgeStatusProduk.setCardBackgroundColor(0xFFE8F5E9);
                    holder.tvStatusProduk.setTextColor(0xFF4CAF50);
                    break;
            }

            holder.btnMoreProduk.setOnClickListener(v -> showAksiProduk(produk));
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
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            // NAMA VARIABEL SAMA PERSIS DENGAN ID XML
            ImageView ivProduk;
            TextView tvNamaProduk, tvKategoriProduk, tvHargaProduk, tvPenjualProduk, tvStatusProduk;
            ImageView btnMoreProduk;
            com.google.android.material.card.MaterialCardView badgeStatusProduk;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                // BINDING ID
                ivProduk = itemView.findViewById(R.id.ivProduk);
                tvNamaProduk = itemView.findViewById(R.id.tvNamaProduk);
                tvKategoriProduk = itemView.findViewById(R.id.tvKategoriProduk);
                tvHargaProduk = itemView.findViewById(R.id.tvHargaProduk);
                tvPenjualProduk = itemView.findViewById(R.id.tvPenjualProduk);
                tvStatusProduk = itemView.findViewById(R.id.tvStatusProduk);
                btnMoreProduk = itemView.findViewById(R.id.btnMoreProduk);
                badgeStatusProduk = itemView.findViewById(R.id.badgeStatusProduk);
            }
        }
    }
}
