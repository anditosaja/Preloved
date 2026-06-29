<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\Product;
use App\Models\User;
use App\Services\ApiResponse;
use Illuminate\Http\Request;

class AdminDashboardController extends Controller
{
    /**
     * Ringkasan angka untuk kartu-kartu di halaman dashboard admin:
     */
    public function summary(Request $request)
    {
        $totalPengguna = User::count();
        $produkAktif = Product::where('status_barang', 'available')->count();
        $totalTransaksi = Order::count();
        $totalPendapatan = Order::where('status', 'completed')->sum('harga_final');

        return ApiResponse::success([
            'total_pengguna' => $totalPengguna,
            'produk_aktif' => $produkAktif,
            'total_transaksi' => $totalTransaksi,
            'total_pendapatan' => (float) $totalPendapatan,
        ]);
    }

    /**
     * Grafik Dinamis (Mesin Waktu 7 Hari Terakhir)
     */
    public function chartData(Request $request)
    {
        $type = $request->query('type', 'transaksi');
        $startDate = now()->subDays(6)->startOfDay();

        if ($type === 'pengguna') {
            $query = User::selectRaw('DATE(created_at) as tanggal, COUNT(*) as jumlah');
        } elseif ($type === 'produk') {
            $query = Product::selectRaw('DATE(created_at) as tanggal, COUNT(*) as jumlah');
        } elseif ($type === 'transaksi') {
            $query = Order::selectRaw('DATE(created_at) as tanggal, COUNT(*) as jumlah');
        } elseif ($type === 'pendapatan') {
            $query = Order::where('status', 'completed')
                          ->selectRaw('DATE(created_at) as tanggal, SUM(harga_final) as jumlah');
        } else {
            return ApiResponse::error('Tipe grafik tidak valid', 400);
        }

        // Eksekusi query
        $rawData = $query->where('created_at', '>=', $startDate)
                         ->groupBy('tanggal')
                         ->orderBy('tanggal')
                         ->get()
                         ->keyBy('tanggal');

        // Isi hari yang kosong dengan 0
        $data = [];
        for ($i = 6; $i >= 0; $i--) {
            $date = now()->subDays($i)->format('Y-m-d');
            $data[] = [
                'tanggal' => $date,
                'jumlah' => isset($rawData[$date]) ? (float) $rawData[$date]->jumlah : 0
            ];
        }

        return ApiResponse::success($data);
    }
}