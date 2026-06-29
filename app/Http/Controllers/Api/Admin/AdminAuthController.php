<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\Admin;
use App\Services\ApiResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

class AdminAuthController extends Controller
{
    /**
     * Login khusus admin. Sengaja dipisah dari AuthController milik user
     * biasa karena admin disimpan di tabel & model yang berbeda.
     */
    public function login(Request $request)
    {
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        $admin = Admin::where('email', $request->email)->first();

        if (! $admin || ! Hash::check($request->password, $admin->password)) {
            return ApiResponse::error('Email atau password salah', 401);
        }

        if (! $admin->is_active) {
            return ApiResponse::error('Akun admin ini telah dinonaktifkan', 403);
        }

        $admin->update(['waktu_aktif_terakhir' => now()]);

        $token = $admin->createToken('admin-android-token')->plainTextToken;

        return ApiResponse::success([
            'token' => $token,
            'admin' => $admin,
        ], 'Login admin berhasil');
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return ApiResponse::success(null, 'Logout berhasil');
    }

    public function me(Request $request)
    {
        return ApiResponse::success($request->user());
    }
}
