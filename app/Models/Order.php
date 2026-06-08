<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Order extends Model
{
    protected $primaryKey = 'order_id';

protected $fillable = [
    'product_id',
    'buyer_id',
    'seller_id',
    'offer_id',
    'harga_final',
    'status'
];

public function product()
{
    return $this->belongsTo(Product::class, 'product_id');
}
public function review()
{
    return $this->hasOne(
        Review::class,
        'order_id'
    );
}
public function buyer()
{
    return $this->belongsTo(User::class, 'buyer_id');
}

public function seller()
{
    return $this->belongsTo(User::class, 'seller_id');
}

public function offer()
{
    return $this->belongsTo(Offer::class, 'offer_id');
}

}
