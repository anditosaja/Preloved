<?php

namespace App\Http\Controllers\Api;

use App\Models\ProductImage;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use App\Services\ApiResponse;
use App\Http\Controllers\Controller;
use App\Models\Product;
use Illuminate\Http\Request;
use App\Http\Resources\ProductResource;

class ProductController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
         $products = Product::with([
        'seller',
        'category',
        'images'
    ])->latest()->get();

    return response()->json($products);
    }

    public function search(Request $request)
{
    $query = Product::with([
        'seller',
        'category',
        'images'
    ]);

    if ($request->filled('q')) {
        $query->where('nama_barang', 'like', '%' . $request->q . '%')
              ->orWhere('deskripsi', 'like', '%' . $request->q . '%')
              ->orWhere('merek', 'like', '%' . $request->q . '%');
    }

    if ($request->filled('category_id')) {
        $query->where('category_id', $request->category_id);
    }

    if ($request->filled('city')) {
        $query->where('lokasi_kota', $request->city);
    }

    if ($request->filled('min_price')) {
    $query->where('harga_jual', '>=', $request->min_price);
    }

    if ($request->filled('max_price')) {
    $query->where('harga_jual', '<=', $request->max_price);
    }

    return response()->json(
        $query->latest()->get()
    );
}
    public function create()
    {
        //
    }

    /**
     * Store a newly created resource in storage.
     */
   public function store(Request $request)
{
    $request->validate([
    'category_id' => 'required|exists:categories,category_id',
    'nama_barang' => 'required|string|max:255',
    'deskripsi' => 'required|string',
    'harga_jual' => 'required|numeric|min:0',
    'harga_asli' => 'nullable|numeric|min:0',
    'kondisi' => 'required',
    'merek' => 'nullable|string|max:100',
    'warna' => 'nullable|string|max:100',
    'lokasi_kota' => 'required|string|max:100',
    'images.*' => 'image|mimes:jpg,jpeg,png|max:2048'
]);

    DB::beginTransaction();

    try {

        $product = Product::create([
            'seller_id' => auth()->id(),
            'category_id' => $request->category_id,
            'nama_barang' => $request->nama_barang,
            'deskripsi' => $request->deskripsi,
            'harga_jual' => $request->harga_jual,
            'harga_asli' => $request->harga_asli,
            'kondisi' => $request->kondisi,
            'merek' => $request->merek,
            'warna' => $request->warna,
            'lokasi_kota' => $request->lokasi_kota,
            'status_barang' => 'available'
        ]);

        if ($request->hasFile('images')) {

            foreach ($request->file('images') as $index => $image) {

                $path = $image->store('products', 'public');

                ProductImage::create([
                    'product_id' => $product->product_id,
                    'image_url' => $path,
                    'is_primary' => ($index === 0)
                ]);
            }
        }

        DB::commit();

        return response()->json([
            'message' => 'Produk berhasil dibuat',
            'product' => $product->load('images')
        ], 201);

    } catch (\Exception $e) {

        DB::rollBack();

        return response()->json([
            'message' => $e->getMessage()
        ], 500);
    }
}

    /**
     * Display the specified resource.
     */
   public function show($id)
{
    $product = Product::with([
        'seller',
        'category',
        'images'
    ])->findOrFail($id);

    return response()->json($product);
}

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Product $product)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, $id)
{
    $product = Product::findOrFail($id);

    $product->update($request->all());

    return response()->json([
        'message' => 'Produk berhasil diperbarui',
        'data' => $product
    ]);
}

    /**
     * Remove the specified resource from storage.
     */
    public function destroy($id)
{
    $product = Product::findOrFail($id);

    $product->delete();

    return response()->json([
        'message' => 'Produk berhasil dihapus'
    ]);
}
}
