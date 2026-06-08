<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\ProductImage;
use Illuminate\Http\Request;

class ProductImageController extends Controller
{
    public function store(Request $request, $id)
    {
        $request->validate([
            'image' => 'required|image'
        ]);

        $path = $request
            ->file('image')
            ->store('products', 'public');

        $image = ProductImage::create([
            'product_id' => $id,
            'image_url' => $path,
            'is_primary' => false
        ]);

        return response()->json([
            'message' => 'Gambar berhasil diupload',
            'data' => $image
        ]);
    }

    public function destroy($id)
    {
        $image = ProductImage::findOrFail($id);

        $image->delete();

        return response()->json([
            'message' => 'Gambar berhasil dihapus'
        ]);
    }
}