<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Product;
use Illuminate\Http\Request;
use App\Models\ProductImage;

class ProductController extends Controller
{
    public function index()
{
    return Product::with([
        'seller',
        'category',
        'images'
    ])
    ->latest()
    ->get();
}

    public function show($id)
{
    return Product::with([
        'seller',
        'category',
        'images'
    ])->findOrFail($id);
}

    public function store(Request $request)
    {
        $product = Product::create($request->all());

        return response()->json($product,201);
    }

    public function update(Request $request,$id)
    {
        $product = Product::findOrFail($id);

        $product->update($request->all());

        return response()->json($product);
    }

    public function destroy($id)
    {
        Product::destroy($id);

        return response()->json([
            'message'=>'Data dihapus'
        ]);
    }

    //Product Image Table-Model
    public function uploadImages(Request $request,$id)
{
    $request->validate([
        'images.*' => 'required|image'
    ]);

    $product = Product::findOrFail($id);

    $uploaded = [];

    foreach($request->file('images') as $image)
    {
        $path = $image->store(
            'products',
            'public'
        );

        ProductImage::create([
            'product_id'=>$product->product_id,
            'image_path'=>$path
        ]);

        $uploaded[] = $path;
    }

    return response()->json([
        'message'=>'Foto berhasil diupload',
        'images'=>$uploaded
    ]);
}

public function search(Request $request)
{
    $keyword = $request->keyword;

    return Product::with([
        'seller',
        'category',
        'images'
    ])
    ->where(
        'nama_barang',
        'LIKE',
        "%{$keyword}%"
    )
    ->get();
}

public function byCategory($id)
{
    return Product::with([
        'seller',
        'category',
        'images'
    ])
    ->where(
        'category_id',
        $id
    )
    ->get();
}

public function myProducts(Request $request)
{
    return Product::with('images')
        ->where(
            'seller_id',
            $request->user()->user_id
        )
        ->get();
}

public function latestProducts()
{
    return Product::with([
        'seller',
        'category',
        'images'
    ])
    ->latest()
    ->take(20)
    ->get();
}
public function myFavorites($userId)
{
    return Favorite::with([
        'product.images',
        'product.seller'
    ])
    ->where('user_id',$userId)
    ->get();
}
}