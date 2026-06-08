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
        $favorites = Favorite::with([
            'product.images',
            'product.seller',
            'product.category'
        ])
        ->where('user_id', $request->user()->user_id)
        ->get();

        return response()->json($favorites);
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