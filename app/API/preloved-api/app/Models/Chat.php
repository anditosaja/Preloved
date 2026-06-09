<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Chat extends Model
{
    protected $primaryKey = 'chat_id';

    protected $fillable = [
        'sender_id',
        'receiver_id',
        'product_id',
        'isi_pesan',
        'waktu_kirim'
    ];

    public function sender()
    {
        return $this->belongsTo(User::class,'sender_id');
    }

    public function receiver()
    {
        return $this->belongsTo(User::class,'receiver_id');
    }

    public function product()
    {
        return $this->belongsTo(Product::class,'product_id');
    }
}