<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Banner;

class BannerSeeder extends Seeder
{
    public function run(): void
    {
        Banner::create([
            'title' => 'Promo Preloved',
            'image_url' => 'banners/banner1.jpg',
            'link' => null,
            'is_active' => true
        ]);

        Banner::create([
            'title' => 'Diskon Barang Bekas',
            'image_url' => 'banners/banner2.jpg',
            'link' => null,
            'is_active' => true
        ]);
    }
}