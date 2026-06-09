<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Offer extends Model
{
    protected $primaryKey = 'offer_id';

    protected $fillable = [
        'buyer_id',
        'product_id',
        'harga_tawaran',
        'status_tawaran',
        'waktu_tawaran'
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