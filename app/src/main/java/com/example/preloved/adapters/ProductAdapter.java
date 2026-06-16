package com.example.preloved.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.preloved.ProfilBarangActivity;
import com.example.preloved.R;
import com.example.preloved.models.Product;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Mengisi data tekstual produk
        if (holder.tvNama != null) holder.tvNama.setText(product.getNama_barang());
        if (holder.tvLokasi != null) holder.tvLokasi.setText(product.getLokasi_kota());
        if (holder.tvProductName != null) holder.tvProductName.setText(product.getNama_barang());

        // Format Harga Jual ke Rupiah
        String formattedPrice;
        try {
            double harga = Double.parseDouble(product.getHarga_jual());
            DecimalFormat format = new DecimalFormat("#,###");
            formattedPrice = "Rp " + format.format(harga);
            holder.tvHarga.setText(formattedPrice);
        } catch (Exception e) {
            formattedPrice = "Rp " + product.getHarga_jual();
            holder.tvHarga.setText(formattedPrice);
        }

        // ================== LOGIKA STATUS DINAMIS (SOLD & GRAYSCALE) ==================
        if (product.getStatus_barang() != null && !product.getStatus_barang().equalsIgnoreCase("available")) {
            // Tampilkan teks overlay SOLD jika barang tidak available
            if (holder.tvSoldBadge != null) {
                holder.tvSoldBadge.setVisibility(View.VISIBLE);
            }

            // Mengubah foto produk menjadi abu-abu (Grayscale) menggunakan ColorMatrix
            android.graphics.ColorMatrix matrix = new android.graphics.ColorMatrix();
            matrix.setSaturation(0f); // 0f berarti murni abu-abu (tanpa warna)
            android.graphics.ColorMatrixColorFilter filter = new android.graphics.ColorMatrixColorFilter(matrix);
            holder.ivProduk.setColorFilter(filter);
        } else {
            // Jika barang masih tersedia, kembalikan tampilan kartu ke kondisi normal
            if (holder.tvSoldBadge != null) {
                holder.tvSoldBadge.setVisibility(View.GONE);
            }
            holder.ivProduk.clearColorFilter(); // Menghapus efek filter abu-abu
        }

        // ================== LOGIKA URL GAMBAR GLIDE ==================
        String finalImageUrl = "";
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imagePath = product.getImages().get(0).getImage_path();

            if (imagePath != null) {
                if (imagePath.startsWith("http")) {
                    finalImageUrl = imagePath;
                } else {
                    finalImageUrl = "http://192.168.18.169:8000/storage/" + imagePath;
                }
            }

            Glide.with(holder.itemView.getContext())
                .load(finalImageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert)
                .into(holder.ivProduk);
        } else {
            holder.ivProduk.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // ================== LOGIKA KLIK PINDAH KE DETAIL ==================
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfilBarangActivity.class);
                intent.putExtra("PRODUCT", product);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ========================================================
    // VIEW HOLDER (MENGHUBUNGKAN VARIABEL JAVA DENGAN ID XML)
    // ========================================================
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduk;
        TextView tvNama, tvHarga, tvLokasi, tvProductName, tvSoldBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi Komponen Gambar
            ivProduk = itemView.findViewById(R.id.ivProduk);

            // Inisialisasi Komponen Teks Utama
            tvNama = itemView.findViewById(R.id.tvNama);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);

            // Inisialisasi Komponen Tambahan (Penyebab Error Sebelumnya)
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvSoldBadge = itemView.findViewById(R.id.tvSoldBadge);

            // Fallback safety jika di XML kamu hanya memakai salah satu antara tvNama atau tvProductName
            if (tvProductName == null) {
                tvProductName = tvNama;
            }
        }
    }
}
