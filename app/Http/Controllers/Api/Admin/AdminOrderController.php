<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Services\ApiResponse;
use Illuminate\Http\Request;

class AdminOrderController extends Controller
{
    /**
     * Daftar semua transaksi di platform, mendukung filter status
     * (pending, paid, shipped, completed, cancelled).
     */
    public function index(Request $request)
    {
        $query = Order::with(['product.images', 'buyer', 'seller']);

        if ($request->filled('status')) {
            $query->where('status', $request->status);
        }

        $orders = $query->latest()->get();

        return ApiResponse::success($orders);
    }

    public function show($id)
    {
        $order = Order::with(['product.images', 'buyer', 'seller', 'offer', 'review'])->findOrFail($id);

        return ApiResponse::success($order);
    }

    /**
     * Admin dapat mengubah status transaksi secara manual, misalnya untuk
     * menyelesaikan dispute atau membatalkan transaksi bermasalah.
     */
    public function updateStatus(Request $request, $id)
    {
        $request->validate([
            'status' => 'required|in:pending,paid,shipped,completed,cancelled',
        ]);

        $order = Order::findOrFail($id);
        $order->update(['status' => $request->status]);

        return ApiResponse::success($order, 'Status transaksi berhasil diperbarui');
    }
}
