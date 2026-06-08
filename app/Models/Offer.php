<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Offer extends Model
{
    protected $primaryKey = 'offer_id';

protected $fillable = [
    'product_id',
    'buyer_id',
    'harga_tawaran',
    'status',
    'parent_offer_id'
];

public function product()
{
    return $this->belongsTo(Product::class, 'product_id');
}

public function buyer()
{
    return $this->belongsTo(User::class, 'buyer_id');
}

public function parentOffer()
{
    return $this->belongsTo(Offer::class, 'parent_offer_id');
}


}
