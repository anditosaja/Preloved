<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Product extends Model
{
    protected $primaryKey = 'product_id';

protected $fillable = [
    'seller_id',
    'category_id',
    'nama_barang',
    'deskripsi',
    'harga_jual',
    'harga_asli',
    'kondisi',
    'merek',
    'warna',
    'lokasi_kota',
    'status_barang'
];

    protected $appends = ['is_favorited'];

    
    public function seller()
    {
        return $this->belongsTo(User::class, 'seller_id');
        }
        
        public function category()
        {
            return $this->belongsTo(Category::class, 'category_id');
            }
            
            public function images()
            {
                return $this->hasMany(ProductImage::class, 'product_id');
}

public function favorites()
{
    return $this->hasMany(Favorite::class, 'product_id');
    
    }
    public function isFavoritedBy($userId)
    {
        return $this->favorites()
        ->where('user_id', $userId)
        ->exists();
        }
        
        public function offers()
        {
            return $this->hasMany(Offer::class, 'product_id');
            }
            public function orders()
            {
                return $this->hasMany(Order::class, 'product_id');
                }
                public function reviews()
                {
                    return $this->hasMany(Review::class, 'product_id');
}

// Tambahkan fungsi ini di bagian bawah
public function getIsFavoritedAttribute()
{
    // Kalau belum login, otomatis false
    if (!auth('sanctum')->check()) {
        return false;
    }

    // Cek apakah user yang login udah nge-like produk ini
    return $this->favorites()
                ->where('user_id', auth('sanctum')->id())
                ->exists();
}
}
