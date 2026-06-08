<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Order;
use Illuminate\Http\Request;

class OrderController extends Controller
{
    public function index()
    {
        return Order::with([
            'buyer',
            'product'
        ])->get();
    }

    public function store(Request $request)
    {
        return Order::create([
            'buyer_id'=>$request->buyer_id,
            'product_id'=>$request->product_id,
            'total_harga'=>$request->total_harga,
            'status_pesanan'=>'Menunggu',
            'tanggal_transaksi'=>now()
        ]);
    }

    public function updateStatus(Request $request,$id)
    {
        $order = Order::findOrFail($id);

        $order->status_pesanan =
            $request->status_pesanan;

        $order->save();

        return response()->json($order);
    }
}