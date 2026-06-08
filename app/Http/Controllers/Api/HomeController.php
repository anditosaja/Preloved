<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Banner;
use App\Models\Category;
use App\Models\Product;

class HomeController extends Controller
{
    public function index()
    {
        $banners = Banner::where('is_active', true)->get();

        $categories = Category::all();

        $trending = Product::with([
            'seller',
            'category',
            'images'
        ])
        ->orderByDesc('jumlah_dilihat')
        ->take(10)
        ->get();

        $recommended = Product::with([
            'seller',
            'category',
            'images'
        ])
        ->latest()
        ->take(10)
        ->get();

        return response()->json([
            'banners' => $banners,
            'categories' => $categories,
            'trending' => $trending,
            'recommended' => $recommended
        ]);
    }
}