<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Review extends Model
{
    // Supaya Laravel tahu primary key-nya bukan 'id' standar kalau misalnya kamu pakai review_id
    // (Abaikan baris ini jika di database kamu primary key ulasan bernama 'id')
    // protected $primaryKey = 'review_id'; 

    protected $fillable = [
        'order_id', 
        'reviewer_id', 
        'seller_id', 
        'product_id', 
        'rating', 
        'comment'
    ];

    // 1. Relasi ke Pembeli (Siapa yang nulis review ini)
    public function reviewer()
    {
        // Harus mendefinisikan 'user_id' karena primary key tabel users kamu adalah user_id
        return $this->belongsTo(User::class, 'reviewer_id', 'user_id');
    }

    // 2. Relasi ke Penjual (Siapa yang di-review)
    public function seller()
    {
        return $this->belongsTo(User::class, 'seller_id', 'user_id');
    }

    // 3. Relasi ke Produk (Barang apa yang di-review)
    public function product()
    {
        // Harus mendefinisikan 'product_id' karena primary key tabel products kamu adalah product_id
        return $this->belongsTo(Product::class, 'product_id', 'product_id');
    }

    // 4. Relasi ke Order (Dari transaksi mana review ini berasal)
    public function order()
    {
        return $this->belongsTo(Order::class, 'order_id', 'order_id');
    }
}