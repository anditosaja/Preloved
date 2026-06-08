<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Category;

class CategorySeeder extends Seeder
{
    public function run(): void
    {
        $categories = [
            'Pakaian',
            'Tas',
            'Sepatu',
            'Elektronik',
            'Buku',
            'Hobi',
            'Kecantikan',
            'Rumah Tangga',
            'Lainnya'
        ];

        foreach ($categories as $category) {
            Category::create([
                'nama_kategori' => $category,
                'icon' => null
            ]);
        }
    }
}