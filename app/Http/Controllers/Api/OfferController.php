<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Offer;
use App\Models\Product;
use Illuminate\Http\Request;

class OfferController extends Controller
{
    public function store(Request $request)
    {
        $request->validate([
            'product_id' => 'required|exists:products,product_id',
            'harga_tawaran' => 'required|numeric|min:1'
        ]);

        $product = Product::findOrFail($request->product_id);

        if ($product->seller_id == $request->user()->user_id) {
            return response()->json([
            'message' => 'Tidak dapat menawar produk milik sendiri'
            ], 422);
        }

        $offer = Offer::create([
            'product_id' => $request->product_id,
            'buyer_id' => $request->user()->user_id,
            'harga_tawaran' => $request->harga_tawaran,
            'status' => 'pending'
        ]);
        $product = Product::findOrFail($request->product_id);

        NotificationHelper::create(
        $product->seller_id,
        'Tawaran Baru',
        'Anda menerima tawaran baru untuk produk '.$product->nama_barang,
        'offer'
        );
        return response()->json([
            'message' => 'Tawaran berhasil dikirim',
            'data' => $offer
        ], 201);
    }

    public function myOffers(Request $request)
    {
        return response()->json(
            Offer::with([
                'product.images',
                'product.seller'
            ])
            ->where('buyer_id', $request->user()->user_id)
            ->latest()
            ->get()
        );
    }

    public function receivedOffers(Request $request)
    {
        
        return response()->json(
        Offer::with([
            'product.images',
            'buyer'
        ])
        ->whereHas('product', function ($query) use ($request) {
            $query->where(
                'seller_id',
                $request->user()->user_id
            );
        })
        ->latest()
        ->get()
        );
    }

    public function accept($id)
    {
        $offer = Offer::findOrFail($id);

        $offer->update([
            'status' => 'accepted'
        ]);

        return response()->json([
            'message' => 'Tawaran diterima'
        ]);
    }

    public function reject($id)
    {
        $offer = Offer::findOrFail($id);

        $offer->update([
            'status' => 'rejected'
        ]);

        return response()->json([
            'message' => 'Tawaran ditolak'
        ]);
    }
}