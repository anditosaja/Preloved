<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Http\Resources\UserResource;

class TopUpController extends Controller
{
    public function store(Request $request)
    {
        // 1. Validasi input dari Android 
        // Memastikan 'amount' wajib diisi, berupa angka, dan misal minimal Top Up Rp 10.000
        $request->validate([
            'amount' => 'required|numeric|min:10000',
        ]);

        // 2. Ambil data user yang sedang login (berdasarkan Token Sanctum dari Android)
        $user = $request->user();

        // 3. Tambahkan saldo saat ini dengan nominal top up
        $user->balance += $request->amount;
        $user->save();

        // 4. Kembalikan response JSON yang akan dibaca oleh Retrofit di Android
        return response()->json([
            'success' => true,
            'message' => 'Top up berhasil!',
            'data' => [
                'new_balance' => $user->balance
            ]
        ]);

        
    }

    public function updateBalance(Request $request) {
    $user = auth()->user();
    
    // Logika update saldo
    $user->balance += $request->amount;
    $user->save();

    // Mengembalikan data user dengan format UserResource
    return new UserResource($user);
}
}