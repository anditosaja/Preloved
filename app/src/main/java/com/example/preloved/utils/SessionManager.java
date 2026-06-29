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

    // ===================== SESI ADMIN =====================
    // Disimpan terpisah dari sesi user biasa karena admin punya
    // model & tabel sendiri (lihat App\Models\Admin di backend).

    public void saveAdminSession(String token, int adminId, String namaAdmin){
        pref.edit()
            .putString("ADMIN_TOKEN", token)
            .putInt("ADMIN_ID", adminId)
            .putString("ADMIN_NAMA", namaAdmin)
            .putBoolean("IS_ADMIN", true)
            .apply();
    }

    public String getAdminToken(){
        return pref.getString("ADMIN_TOKEN", null);
    }

    public int getAdminId(){
        return pref.getInt("ADMIN_ID", 0);
    }

    public String getAdminNama(){
        return pref.getString("ADMIN_NAMA", null);
    }

    public boolean isAdmin(){
        return pref.getBoolean("IS_ADMIN", false);
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
