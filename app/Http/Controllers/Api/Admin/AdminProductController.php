<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\Product;
use App\Services\ApiResponse;
use Illuminate\Http\Request;

class AdminProductController extends Controller
{
    /**
     * Daftar semua produk untuk panel admin, mendukung filter status
     * (available, reserved, sold, ditangguhkan, ditolak) dan pencarian nama.
     */
    public function index(Request $request)
    {
        $query = Product::with(['seller', 'category', 'images']);

        if ($request->filled('status')) {
            $query->where('status_barang', $request->status);
        }

        if ($request->filled('q')) {
            $query->where('nama_barang', 'like', '%' . $request->q . '%');
        }

        $products = $query->latest()->get();

        return ApiResponse::success($products);
    }

    public function show($id)
    {
        $product = Product::with(['seller', 'category', 'images', 'reviews'])->findOrFail($id);

        return ApiResponse::success($product);
    }

    /**
     * Tangguhkan produk (disembunyikan sementara dari katalog) dengan catatan alasan.
     */
    public function suspend(Request $request, $id)
    {
        $request->validate([
            'catatan_admin' => 'nullable|string',
        ]);

        $product = Product::findOrFail($id);
        $product->update([
            'status_barang' => 'ditangguhkan',
            'catatan_admin' => $request->catatan_admin,
        ]);

        return ApiResponse::success($product, 'Produk berhasil ditangguhkan');
    }

    /**
     * Tolak produk secara permanen (misal melanggar aturan platform).
     */
    public function reject(Request $request, $id)
    {
        $request->validate([
            'catatan_admin' => 'required|string',
        ]);

        $product = Product::findOrFail($id);
        $product->update([
            'status_barang' => 'ditolak',
            'catatan_admin' => $request->catatan_admin,
        ]);

        return ApiResponse::success($product, 'Produk berhasil ditolak');
    }

    /**
     * Pulihkan produk yang ditangguhkan/ditolak kembali menjadi available.
     */
    public function approve($id)
    {
        $product = Product::findOrFail($id);
        $product->update([
            'status_barang' => 'available',
            'catatan_admin' => null,
        ]);

        return ApiResponse::success($product, 'Produk berhasil disetujui/dipulihkan');
    }

    public function destroy($id)
    {
        $product = Product::findOrFail($id);
        $product->delete();

        return ApiResponse::success(null, 'Produk berhasil dihapus');
    }
}
