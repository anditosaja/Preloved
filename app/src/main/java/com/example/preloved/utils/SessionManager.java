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

    public void logout(){

        pref.edit()
                .clear()
                .apply();
    }
}