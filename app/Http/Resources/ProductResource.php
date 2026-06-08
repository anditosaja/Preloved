<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class ProductResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [

            'id' => $this->product_id,

            'nama_barang' => $this->nama_barang,

            'harga_jual' => $this->harga_jual,

            'harga_asli' => $this->harga_asli,

            'kondisi' => $this->kondisi,

            'merek' => $this->merek,

            'warna' => $this->warna,

            'lokasi_kota' => $this->lokasi_kota,

            'status_barang' => $this->status_barang,

            'seller' => [
                'id' => $this->seller?->user_id,
                'nama' => $this->seller?->nama_lengkap,
                'rating' => $this->seller?->rating,
            ],

            'category' => [
                'id' => $this->category?->category_id,
                'nama' => $this->category?->nama_kategori,
            ],

            'images' => $this->images->map(function ($image) {
                return [
                    'id' => $image->image_id,
                    'url' => $image->image_url,
                    'is_primary' => $image->is_primary
                ];
            })
        ];
    }
}