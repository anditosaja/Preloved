<?php

namespace App\Http\Controllers\Api;

use App\Models\Order;
use App\Models\Offer;
use App\Models\Product;
use App\Services\NotificationService;
use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

class OrderController extends Controller
{
    public function store(Request $request)
{
    $request->validate([
        'offer_id' => 'required|exists:offers,offer_id'
    ]);

    $offer = Offer::with('product')->findOrFail(
        $request->offer_id
    );

    $product = $offer->product;

    $order = Order::create([
        'product_id' => $product->product_id,
        'buyer_id' => $offer->buyer_id,
        'seller_id' => $product->seller_id,
        'offer_id' => $offer->offer_id,
        'harga_final' => $offer->harga_tawaran,
        'status' => 'pending'
    ]);

    NotificationService::create(
        $offer->buyer_id,
        'Pesanan Baru',
        'Penjual menerima tawaran Anda',
        'order'
    );

    return response()->json([
        'message' => 'Order berhasil dibuat',
        'data' => $order
    ]);
}

public function myPurchases(Request $request)
{
    return response()->json(
        Order::with([
            'product',
            'seller'
        ])
        ->where(
            'buyer_id',
            $request->user()->user_id
        )
        ->latest()
        ->get()
    );
}

public function mySales(Request $request)
{
    return response()->json(
        Order::with([
            'product',
            'buyer'
        ])
        ->where(
            'seller_id',
            $request->user()->user_id
        )
        ->latest()
        ->get()
    );
}

public function show($id)
{
    return response()->json(
        Order::with([
            'product',
            'buyer',
            'seller',
            'offer'
        ])->findOrFail($id)
    );
}

public function updateStatus(
    Request $request,
    $id
)
{
    $request->validate([
        'status' =>
        'required|in:pending,paid,shipped,completed,cancelled'
    ]);

    $order = Order::findOrFail($id);

    $order->update([
        'status' => $request->status
    ]);

    NotificationService::create(
        $order->buyer_id,
        'Status Pesanan',
        'Status pesanan berubah menjadi '.$request->status,
        'order'
    );

    return response()->json([
        'message' => 'Status berhasil diperbarui',
        'data' => $order
    ]);
}
}