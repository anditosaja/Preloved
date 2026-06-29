package com.example.preloved.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.preloved.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static final String PREF_NAME = "PrelovedFavorites";
    private static final String KEY_FAV_LIST = "fav_list";
    private SharedPreferences prefs;
    private Gson gson;

    public FavoriteManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // Fungsi untuk menambah atau menghapus dari favorit
    public void toggleFavorite(Product product) {
        List<Product> favorites = getFavorites();
        boolean isExist = false;

        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getProductId() == product.getProductId()) {
                favorites.remove(i); // Jika sudah ada, hapus (Toggle off)
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            favorites.add(product); // Jika belum ada, tambahkan (Toggle on)
        }

        saveFavorites(favorites);
    }

    // Cek apakah suatu produk berstatus favorit
    public boolean isFavorite(int productId) {
        List<Product> favorites = getFavorites();
        for (Product p : favorites) {
            if (p.getProductId() == productId) {
                return true;
            }
        }
        return false;
    }

    // Ambil semua daftar barang favorit
    public List<Product> getFavorites() {
        String json = prefs.getString(KEY_FAV_LIST, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveFavorites(List<Product> favorites) {
        String json = gson.toJson(favorites);
        prefs.edit().putString(KEY_FAV_LIST, json).apply();
    }
}
