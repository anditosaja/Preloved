<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Services\ApiResponse;
use Illuminate\Http\Request;

class AdminUserController extends Controller
{
    /**
     * Daftar semua pengguna. Mendukung filter status & pencarian nama/email/username,
     * sesuai tab yang ada di layout activity_pengguna_admin.xml (Semua, Aktif, Nonaktif, Diblokir).
     */
    public function index(Request $request)
    {
        $query = User::query();

        if ($request->filled('status')) {
            $query->where('status_akun', $request->status);
        }

        if ($request->filled('q')) {
            $keyword = $request->q;
            $query->where(function ($sub) use ($keyword) {
                $sub->where('nama_lengkap', 'like', "%{$keyword}%")
                    ->orWhere('email', 'like', "%{$keyword}%")
                    ->orWhere('username', 'like', "%{$keyword}%");
            });
        }

        $users = $query->latest()->get();

        return ApiResponse::success($users);
    }

    public function show($id)
    {
        $user = User::with(['products', 'orders', 'reviewsReceived'])->findOrFail($id);

        return ApiResponse::success($user);
    }

    /**
     * Blokir pengguna. Memakai kolom status_akun yang ditambahkan
     * lewat migration add_status_akun_to_users_table.
     */
    public function block($id)
    {
        $user = User::findOrFail($id);
        $user->update(['status_akun' => 'diblokir']);

        return ApiResponse::success($user, 'Pengguna berhasil diblokir');
    }

    public function unblock($id)
    {
        $user = User::findOrFail($id);
        $user->update(['status_akun' => 'aktif']);

        return ApiResponse::success($user, 'Pengguna berhasil diaktifkan kembali');
    }

    public function destroy($id)
    {
        $user = User::findOrFail($id);
        $user->delete();

        return ApiResponse::success(null, 'Pengguna berhasil dihapus');
    }
}
