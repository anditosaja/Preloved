<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Category;

class CategoryController extends Controller
{
    public function index()
    {
        $categories = Category::withCount('products')->get();

    return response()->json($categories);

    }

    public function products($id)
    {
        $category = Category::with([
            'products.images',
            'products.seller'
        ])->findOrFail($id);

        return response()->json($category);
    }
}