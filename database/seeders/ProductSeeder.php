<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Product;

class ProductSeeder extends Seeder
{
    public function run(): void
    {
        Product::create([
            'seller_id' => 2,
            'category_id' => 2,
            'nama_barang' => 'Tas Totebag Canvas Minimalist',
            'deskripsi' => 'Totebag canvas tebal masih sangat bagus.',
            'harga_jual' => 85000,
            'harga_asli' => 150000,
            'kondisi' => 'sangat_baik',
            'merek' => 'No Brand',
            'warna' => 'Beige',
            'lokasi_kota' => 'Jakarta Selatan',
            'status_barang' => 'available'
        ]);

        Product::create([
            'seller_id' => 2,
            'category_id' => 3,
            'nama_barang' => 'Nike Air Force 1 White',
            'deskripsi' => 'Jarang dipakai.',
            'harga_jual' => 450000,
            'harga_asli' => 1200000,
            'kondisi' => 'baik',
            'merek' => 'Nike',
            'warna' => 'White',
            'lokasi_kota' => 'Jakarta Barat',
            'status_barang' => 'available'
        ]);
    }
}