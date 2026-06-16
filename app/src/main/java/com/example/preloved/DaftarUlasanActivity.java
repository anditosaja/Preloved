package com.example.preloved;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preloved.models.Product;
import com.example.preloved.models.Review;
import com.example.preloved.models.User;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.Config;
import com.example.preloved.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DaftarUlasanActivity extends AppCompatActivity {

    private RecyclerView rvUlasan;
    private ProgressBar progressBar;
    private TextView tvEmptyUlasan;
    private int sellerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_ulasan);

        ImageView btnBackUlasan = findViewById(R.id.btnBackUlasan);
        rvUlasan = findViewById(R.id.rvUlasan);
        progressBar = findViewById(R.id.progressBarUlasan);
        tvEmptyUlasan = findViewById(R.id.tvEmptyUlasan);

        rvUlasan.setLayoutManager(new LinearLayoutManager(this));

        if (btnBackUlasan != null) {
            btnBackUlasan.setOnClickListener(v -> finish());
        }

        if (getIntent() != null && getIntent().hasExtra("SELLER_ID")) {
            sellerId = getIntent().getIntExtra("SELLER_ID", -1);
        }

        if (sellerId == -1) {
            Toast.makeText(this, "ID Toko tidak valid!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadUlasanToko();
    }

    private void loadUlasanToko() {
        progressBar.setVisibility(View.VISIBLE);
        rvUlasan.setVisibility(View.GONE);
        tvEmptyUlasan.setVisibility(View.GONE);

        com.example.preloved.utils.SessionManager session = new com.example.preloved.utils.SessionManager(this);
        String token = session.getToken();
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getSellerReviews(authHeader, sellerId).enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Review> listReview = response.body();

                    if (listReview.isEmpty()) {
                        tvEmptyUlasan.setVisibility(View.VISIBLE);
                    } else {
                        rvUlasan.setVisibility(View.VISIBLE);
                        UlasanAdapter adapter = new UlasanAdapter(DaftarUlasanActivity.this, listReview);
                        rvUlasan.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(DaftarUlasanActivity.this, "Gagal mengambil data ulasan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Review>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DaftarUlasanActivity.this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ========================================================
    // ADAPTER RECYCLERVIEW UNTUK ULASAN & PRODUK TERJUAL
    // ========================================================
    private static class UlasanAdapter extends RecyclerView.Adapter<UlasanAdapter.ViewHolder> {
        private final Context context;
        private final List<Review> listReview;

        public UlasanAdapter(Context context, List<Review> listReview) {
            this.context = context;
            this.listReview = listReview;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_ulasan, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Review review = listReview.get(position);

            holder.tvReviewComment.setText(review.getComment() != null && !review.getComment().isEmpty() ? review.getComment() : "(Pembeli tidak menulis deskripsi ulasan)");
            holder.ratingBarItem.setRating(review.getRating());

            String tanggal = review.getCreatedAt() != null ? review.getCreatedAt().substring(0, 10) : "";
            holder.tvReviewDate.setText(tanggal);

            User pembeli = review.getReviewer();
            if (pembeli != null) {
                holder.tvReviewerName.setText(pembeli.getNamaLengkap());
                if (pembeli.getFoto_profil() != null && !pembeli.getFoto_profil().isEmpty()) {
                    String urlAvatar = pembeli.getFoto_profil().startsWith("http") ? pembeli.getFoto_profil() : Config.IMAGE_URL + pembeli.getFoto_profil();
                    Glide.with(context).load(urlAvatar).placeholder(android.R.drawable.sym_contact_card).into(holder.imgReviewerAvatar);
                }
            }

            // 3. Set Informasi Produk Sungguhan yang di-Review
            Product realProduct = review.getProduct();

            if (realProduct != null) {
                // Set Nama Produk
                holder.tvReviewedProductName.setText(realProduct.getNama_barang());

                // Set Gambar Produk
                if (realProduct.getImages() != null && !realProduct.getImages().isEmpty()) {
                    String imgPath = realProduct.getImages().get(0).getImage_path();
                    if (imgPath == null || imgPath.isEmpty()) {
                        imgPath = realProduct.getImages().get(0).getImage_url(); // Fallback kalau pakai image_url
                    }
                    if (imgPath != null) {
                        String urlProduct = imgPath.startsWith("http") ? imgPath : Config.IMAGE_URL + imgPath;
                        Glide.with(context).load(urlProduct).placeholder(android.R.drawable.ic_menu_gallery).into(holder.imgReviewedProduct);
                    }
                }

                // Lempar Object Asli ke ProfilBarangActivity
                holder.layoutProductReviewed.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ProfilBarangActivity.class);
                    // Sekarang datanya bukan barang kosong lagi, harga dan detailnya bakal nampil!
                    intent.putExtra("PRODUCT", realProduct);
                    context.startActivity(intent);
                });
            } else {
                // Skenario jaga-jaga kalau produk dihapus oleh penjual
                holder.tvReviewedProductName.setText("Produk tidak tersedia");
                holder.layoutProductReviewed.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return listReview.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgReviewerAvatar, imgReviewedProduct;
            TextView tvReviewerName, tvReviewDate, tvReviewComment, tvReviewedProductName;
            RatingBar ratingBarItem;
            View layoutProductReviewed;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgReviewerAvatar = itemView.findViewById(R.id.imgReviewerAvatar);
                imgReviewedProduct = itemView.findViewById(R.id.imgReviewedProduct);
                tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
                tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
                tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
                tvReviewedProductName = itemView.findViewById(R.id.tvReviewedProductName);
                ratingBarItem = itemView.findViewById(R.id.ratingBarItem);
                layoutProductReviewed = itemView.findViewById(R.id.layoutProductReviewed);
            }
        }
    }
}
