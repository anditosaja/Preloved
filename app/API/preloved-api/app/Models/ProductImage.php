<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class ProductImage extends Model
{
    protected $fillable = [
        'product_id',
        'image_path'
    ];

    protected $appends = ['image_url'];

    public function getImageUrlAttribute()
{
    return asset('storage/'.$this->image_path);
}
    public function product()
    {
        return $this->belongsTo(
            Product::class,
            'product_id',
            'product_id'
        );
    }
}