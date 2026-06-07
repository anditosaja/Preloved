<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Review extends Model
{
    protected $primaryKey = 'review_id';

    protected $fillable = [
        'order_id',
        'product_id',
        'reviewer_id',
        'seller_id',
        'rating',
        'comment'
    ];

    public function order()
    {
        return $this->belongsTo(Order::class, 'order_id');
    }

    public function reviewer()
    {
        return $this->belongsTo(User::class, 'reviewer_id');
    }

    public function seller()
    {
        return $this->belongsTo(User::class, 'seller_id');
    }
    public function product()
{
    return $this->belongsTo(
        Product::class,
        'product_id'
    );
}
}