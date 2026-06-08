<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ProductImage extends Model
{
    protected $primaryKey = 'image_id';

protected $fillable = [
    'product_id',
    'image_url',
    'is_primary'
];

protected $appends = ['image_full_url'];

public function getImageFullUrlAttribute()
{
    return asset('storage/' . $this->image_url);
}

public function product()
{
    return $this->belongsTo(Product::class, 'product_id');
}

}
