<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\ProductImage;

class ProductImageSeeder extends Seeder
{
    public function run(): void
    {
        ProductImage::create([
            'product_id' => 1,
            'image_url' => 'products/totebag.jpg',
            'is_primary' => true
        ]);

        ProductImage::create([
            'product_id' => 2,
            'image_url' => 'products/nike.jpg',
            'is_primary' => true
        ]);
    }
}