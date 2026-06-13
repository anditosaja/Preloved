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
import com.example.preloved.models.ProductImage;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent,
        int viewType
    ) {
        View view = LayoutInflater.from(
            parent.getContext()
        ).inflate(
            R.layout.item_product,
            parent,
            false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
        @NonNull ViewHolder holder,
        int position
    ) {
        Product product = productList.get(position);

        holder.tvNama.setText(product.getNama_barang());
        holder.tvLokasi.setText(product.getLokasi_kota());

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

        // ================== LOGIKA URL GAMBAR GLIDE ==================
        String finalImageUrl = "";
        if (product.getImages() != null && !product.getImages().isEmpty()) {

            // PENTING 1: Sesuaikan dengan method di ProductImage lu (getImage_path atau getImage_url)
            String imagePath = product.getImages().get(0).getImage_path();

            if (imagePath != null) {
                if (imagePath.startsWith("http")) {
                    finalImageUrl = imagePath;
                } else {
                    // PENTING 2: Ganti IP di bawah ini SAMAIN PERSIS dengan BASE_URL di RetrofitClient.java lu!
                    finalImageUrl = "http://10.255.149.23:8000/storage/" + imagePath;
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
                // Langsung kirim 1 koper utuh (Sama kayak di MainActivity)
                intent.putExtra("PRODUCT", product);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduk;
        TextView tvNama, tvHarga, tvLokasi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduk = itemView.findViewById(R.id.ivProduk);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
        }
    }
}
