package com.example.preloved.models;

/**
 * Wrapper untuk response berformat ApiResponse::success() di backend Laravel:
 * { "success": true, "message": "...", "data": { ... } }
 *
 * Dipakai khusus untuk endpoint admin (AdminAuthController, dst) yang
 * konsisten memakai helper ApiResponse, berbeda dengan endpoint user
 * biasa (AuthController) yang return JSON manual tanpa wrapper.
 */
public class AdminLoginResponse {

    private boolean success;
    private String message;
    private AdminLoginData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public AdminLoginData getData() {
        return data;
    }

    public static class AdminLoginData {
        private String token;
        private Admin admin;

        public String getToken() {
            return token;
        }

        public Admin getAdmin() {
            return admin;
        }
    }
}
