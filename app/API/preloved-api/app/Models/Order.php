<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Order extends Model
{
    protected $primaryKey = 'order_id';

    protected $fillable = [
        'buyer_id',
        'product_id',
        'total_harga',
        'status_pesanan',
        'tanggal_transaksi'
    ];

    public function buyer()
    {
        return $this->belongsTo(User::class,'buyer_id');
    }

    public function product()
    {
        return $this->belongsTo(Product::class,'product_id');
    }
}