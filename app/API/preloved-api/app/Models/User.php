<?php

namespace App\Models;

use Laravel\Sanctum\HasApiTokens;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class User extends Authenticatable
{
    use HasFactory, HasApiTokens;

    protected $primaryKey = 'user_id';

    protected $fillable = [
        'nama_lengkap',
        'username',
        'email',
        'password',
        'foto_profil',
        'is_verified',
        'rating',
        'jumlah_ulasan',
        'saldo_preloved',
        'waktu_aktif_terakhir'
    ];

    protected $hidden = [
        'password'
    ];

    public function products()
    {
        return $this->hasMany(Product::class,'seller_id');
    }

    public function favorites()
    {
        return $this->hasMany(Favorite::class,'user_id');
    }

    public function orders()
    {
        return $this->hasMany(Order::class,'buyer_id');
    }

    public function offers()
    {
        return $this->hasMany(Offer::class,'buyer_id');
    }

    public function sentChats()
    {
        return $this->hasMany(Chat::class,'sender_id');
    }

    public function receivedChats()
    {
        return $this->hasMany(Chat::class,'receiver_id');
    }

    public function following()
    {
        return $this->belongsToMany(
            User::class,
            'follows',
            'follower_id',
            'following_id'
        );
    }

    public function followers()
    {
        return $this->belongsToMany(
            User::class,
            'follows',
            'following_id',
            'follower_id'
        );
    }
}