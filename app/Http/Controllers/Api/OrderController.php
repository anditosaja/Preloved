<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Product;
use App\Models\Order;
use App\Models\User;
use Illuminate\Support\Facades\DB;

class OrderController extends Controller
{
    public function store(Request $request) {
        $request->validate([
            'product_id' => 'required'
        ]);

        $user = auth()->user(); 
        
        $product = Product::find($request->product_id);
        if (!$product) {
            return response()->json(['message' => 'Produk tidak ditemukan!'], 404);
        }

        if ($user->user_id == $product->seller_id) {
            return response()->json(['message' => 'Tidak bisa membeli barang sendiri!'], 400);
        }

        if ($user->balance < $product->harga_jual) {
            return response()->json(['message' => 'Saldo tidak mencukupi, silakan top up dulu!'], 400);
        }

        DB::beginTransaction();
        try {
            $potongSaldo = User::where('user_id', $user->user_id)->decrement('balance', $product->harga_jual);

            if (!$potongSaldo) {
                throw new \Exception("Gagal eksekusi potong saldo pembeli.");
            }

            Order::create([
                'product_id'  => $product->product_id, 
                'buyer_id'    => $user->user_id,       
                'seller_id'   => $product->seller_id,  
                'harga_final' => $product->harga_jual, 
                'status'      => 'paid'                
            ]);

            $product->update(['status_barang' => 'sold']);

            DB::commit();

            $userFresh = $user->fresh();
            
            return response()->json([
                'message' => 'Pembelian berhasil',
                'new_balance' => $userFresh->balance 
            ], 200);

        } catch (\Exception $e) {
            DB::rollBack();
            
            \Log::error('TRX_FAIL: ' . $e->getMessage());
            
            return response()->json([
                'message' => 'Terjadi kesalahan sistem',
                'error_detail' => $e->getMessage()
            ], 500);
        }
    }

    // ==========================================================
    // UNTUK PEMBELI (Menu: Pesanan Saya)
    // ==========================================================
    public function myOrders()
    {
        $user = auth()->user();
        
        // Mengambil data gambar produk (product.images) dan data penjual
        $orders = Order::with(['product.images', 'seller'])
            ->where('buyer_id', $user->user_id)
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json($orders, 200);
    }

    // ==========================================================
    // UNTUK PENJUAL (Menu: Barang Saya / Penjualan)
    // ==========================================================
    public function mySales()
    {
        $user = auth()->user();
        
        // Tarik SEMUA produk milik penjual ini
        $products = Product::with(['images', 'orders' => function($query) {
            // Jika produk sudah ada ordernya, ambil data order tersebut
            $query->orderBy('created_at', 'desc');
        }])
        ->where('seller_id', $user->user_id)
        // Urutkan secara spesifik: yang 'sold' di urutan atas, 'available' di bawah
        ->orderByRaw("FIELD(status_barang, 'available', 'reserved', 'sold')")
        ->orderBy('created_at', 'desc')
        ->get();

        return response()->json($products, 200);
    }

    // ==========================================================
    // UNTUK PENJUAL (Aksi: ACC Pesanan / Kirim Barang)
    // ==========================================================
    public function acceptOrder($order_id)
    {
        $user = auth()->user();
        $order = Order::find($order_id);

        if (!$order) {
            return response()->json(['message' => 'Pesanan tidak ditemukan'], 404);
        }

        // Pastikan hanya penjual barang yang bisa meng-ACC
        if ($order->seller_id != $user->user_id) {
            return response()->json(['message' => 'Kamu tidak berhak mengubah pesanan ini'], 403);
        }

        // Ubah status dari 'paid' menjadi 'shipped' (Dikirim)
        $order->update(['status' => 'shipped']);

        return response()->json([
            'message' => 'Pesanan berhasil di-ACC dan diproses',
            'order' => $order
        ], 200);
    }

    // ==========================================================
    // UNTUK PEMBELI (Aksi: Pesanan Diterima / Dana Cair ke Penjual)
    // ==========================================================
    public function completeOrder($order_id)
    {
        $user = auth()->user();
        $order = Order::find($order_id);

        if (!$order) {
            return response()->json(['message' => 'Pesanan tidak ditemukan'], 404);
        }

        // Pastikan hanya pembeli asli yang bisa memencet tombol ini
        if ($order->buyer_id != $user->user_id) {
            return response()->json(['message' => 'Kamu tidak berhak menyelesaikan pesanan ini'], 403);
        }

        // Pastikan barang sudah dikirim (di-ACC) oleh penjual
        if ($order->status != 'shipped') {
            return response()->json(['message' => 'Pesanan belum dikirim oleh penjual, tidak bisa diselesaikan'], 400);
        }

        DB::beginTransaction();
        try {
            // 1. Ubah status order menjadi selesai (completed)
            $order->update(['status' => 'completed']);

            // 2. Cairkan dana ke Penjual (Increment / Tambah saldo penjual)
            $tambahSaldo = User::where('user_id', $order->seller_id)
                               ->increment('balance', $order->harga_final);

            if (!$tambahSaldo) {
                throw new \Exception("Gagal mencairkan dana ke saldo penjual.");
            }

            DB::commit();

            return response()->json([
                'message' => 'Pesanan selesai! Dana berhasil diteruskan ke penjual.',
                'order' => $order
            ], 200);

        } catch (\Exception $e) {
            DB::rollBack();
            \Log::error('COMPLETE_ORDER_FAIL: ' . $e->getMessage());
            
            return response()->json([
                'message' => 'Terjadi kesalahan sistem saat memproses dana',
                'error_detail' => $e->getMessage()
            ], 500);
        }
    }
}