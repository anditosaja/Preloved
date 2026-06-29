<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class ProfileController extends Controller
{
    public function show(Request $request)
        {
            $user = $request->user();

            // 1. Hitung jumlah barang terjual (status 'sold')
            $barangTerjual = \App\Models\Product::where('seller_id', $user->user_id)
                ->where('status_barang', 'sold')
                ->count();

            // 2. Hitung jumlah barang dijual (status 'available')
            $barangDijual = \App\Models\Product::where('seller_id', $user->user_id)
                ->where('status_barang', 'available')
                ->count();

            $userData = $user->toArray();

            $userData['barang_terjual'] = $barangTerjual;
            $userData['barang_dijual'] = $barangDijual;

            return response()->json($userData);
        }
    public function seller($userId)
{
    $seller = User::with([
        'products',
        'reviewsReceived'
    ])->findOrFail($userId);

    return response()->json($seller);
}
    public function update(Request $request)
    {
        $user = $request->user();

        $request->validate([
            'nama_lengkap' => 'required|string|max:255',
            'username' => 'required|string|max:100',
            'bio' => 'nullable|string|max:500'
        ]);

        $user->update([
            'nama_lengkap' => $request->nama_lengkap,
            'username' => $request->username,
            'bio' => $request->bio
        ]);

        return response()->json([
            'message' => 'Profil berhasil diperbarui',
            'user' => $user
        ]);
    }

    public function uploadPhoto(Request $request)
    {
    $user = $request->user();

    $request->validate([
        'photo' => 'required|image|mimes:jpg,jpeg,png|max:2048'
    ]);

    if ($user->foto_profil) {
        Storage::disk('public')->delete(
            $user->foto_profil
        );
    }

    $path = $request
        ->file('photo')
        ->store('profiles', 'public');

    $user->update([
        'foto_profil' => $path
    ]);

    return response()->json([
        'message' => 'Foto profil berhasil diupload',
        'foto_profil' => asset('storage/' . $path)
    ]);
    }
}
