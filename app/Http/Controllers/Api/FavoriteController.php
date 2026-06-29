<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Favorite;
use App\Models\Product;
use Illuminate\Http\Request;

class FavoriteController extends Controller
{
    public function index(Request $request)
    {
        // Ambil semua data favorit milik user ini, beserta relasi produk, gambar, dan sellernya
        $favorites = \App\Models\Favorite::with([
            'product.images', 
            'product.seller'
        ])
        ->where('user_id', $request->user()->user_id)
        ->latest()
        ->get()
        ->pluck('product'); // pluck('product') dipakai biar response JSON-nya langsung berisi array Product, persis kayak response halaman Home/Search.

        return response()->json($favorites);
    }

    public function toggleFavorite(Request $request, $productId)
    {
        $userId = $request->user()->user_id;

        // Cek apakah sudah di-like
        $favorite = \App\Models\Favorite::where('user_id', $userId)
                                        ->where('product_id', $productId)
                                        ->first();

        if ($favorite) {
            // Kalau sudah ada, hapus (Unlike)
            $favorite->delete();
            return response()->json(['message' => 'Dihapus dari favorit', 'is_favorited' => false]);
        } else {
            // Kalau belum ada, tambahkan (Like)
            \App\Models\Favorite::create([
                'user_id' => $userId,
                'product_id' => $productId
            ]);
            return response()->json(['message' => 'Ditambahkan ke favorit', 'is_favorited' => true]);
        }
    }

    public function store(Request $request, $productId)
    {
        $product = Product::findOrFail($productId);

        $favorite = Favorite::firstOrCreate([
            'user_id' => $request->user()->user_id,
            'product_id' => $product->product_id
        ]);

        return response()->json([
            'message' => 'Produk ditambahkan ke favorit',
            'data' => $favorite
        ]);
    }

    public function destroy(Request $request, $productId)
    {
        Favorite::where([
            'user_id' => $request->user()->user_id,
            'product_id' => $productId
        ])->delete();

        return response()->json([
            'message' => 'Produk dihapus dari favorit'
        ]);
    }
}