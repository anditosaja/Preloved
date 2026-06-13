package com.example.preloved.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences pref;

    public SessionManager(Context context){
        // Pastikan nama brankasnya konsisten yaitu "preloved"
        pref = context.getSharedPreferences("preloved", Context.MODE_PRIVATE);
    }

    public void saveToken(String token){
        pref.edit()
            .putString("TOKEN", token)
            .commit(); // <-- Ubah apply() jadi commit()
    }

    public String getToken(){
        return pref.getString("TOKEN", null);
    }

    public String getBearerToken() {
        String token = getToken();
        // Cegah return null agar aman saat dilempar ke header API
        return (token != null && !token.isEmpty()) ? "Bearer " + token : "";
    }

    public void saveUserId(int userId){
        pref.edit()
            .putInt("USER_ID", userId)
            .apply();
    }

    public int getUserId(){
        return pref.getInt("USER_ID", 0);
    }

    public boolean isLoggedIn() {
        // User dianggap login kalau tokennya ada dan tidak kosong
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public void clearSession() {
        // Langsung bersihkan brankas "preloved" yang sudah di-init di atas
        pref.edit().clear().apply();
    }

    public void logout(){
        clearSession();
    }
}
