package com.example.preloved.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

        holder.tvNama.setText(
                product.getNama_barang()
        );

        holder.tvLokasi.setText(
                product.getLokasi_kota()
        );

        try {

            double harga =
                    Double.parseDouble(
                            product.getHarga_jual()
                    );

            DecimalFormat format =
                    new DecimalFormat("#,###");

            holder.tvHarga.setText(
                    "Rp " + format.format(harga)
            );

        } catch (Exception e) {

            holder.tvHarga.setText(
                    "Rp " + product.getHarga_jual()
            );
        }

        if(product.getImages() != null
                && !product.getImages().isEmpty()) {

            ProductImage image =
                    product.getImages().get(0);

            Glide.with(holder.itemView.getContext())
                    .load(image.getImage_url())
                    .into(holder.ivProduk);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView ivProduk;
        TextView tvNama;
        TextView tvHarga;
        TextView tvLokasi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProduk =
                    itemView.findViewById(
                            R.id.ivProduk
                    );

            tvNama =
                    itemView.findViewById(
                            R.id.tvNama
                    );

            tvHarga =
                    itemView.findViewById(
                            R.id.tvHarga
                    );

            tvLokasi =
                    itemView.findViewById(
                            R.id.tvLokasi
                    );
        }
    }
}