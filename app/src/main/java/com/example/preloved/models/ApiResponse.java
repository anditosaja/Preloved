package com.example.preloved.models;

/**
 * Wrapper generic untuk semua response endpoint admin yang memakai
 * helper ApiResponse::success()/error() di backend Laravel:
 * { "success": true, "message": "...", "data": T }
 *
 * Dipakai dengan Retrofit, contoh:
 *   Call<ApiResponse<List<User>>> getUsers();
 */
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
