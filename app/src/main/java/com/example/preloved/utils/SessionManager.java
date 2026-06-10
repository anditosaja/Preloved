package com.example.preloved.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences pref;

    public SessionManager(Context context){
        pref = context.getSharedPreferences(
            "preloved",
            Context.MODE_PRIVATE
        );
    }

    public void saveToken(String token){
        pref.edit()
            .putString("TOKEN", token)
            .apply();
    }

    public String getToken(){
        return pref.getString(
            "TOKEN",
            null
        );
    }

    public String getBearerToken() {
        String token = getToken();
        return "Bearer " + token;
    }

    public void saveUserId(int userId){
        pref.edit()
            .putInt("USER_ID", userId)
            .apply();
    }

    public int getUserId(){
        return pref.getInt(
            "USER_ID",
            0
        );
    }

    public boolean isLoggedIn() {
        // Jika TOKEN tidak null (artinya ada isinya), berarti user sudah login
        return getToken() != null;
    }

    public void logout(){
        pref.edit()
            .clear()
            .apply();
    }
}
