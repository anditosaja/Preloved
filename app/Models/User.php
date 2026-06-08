<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Laravel\Sanctum\HasApiTokens;
use Database\Factories\UserFactory;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;

class User extends Authenticatable
{
    /** @use HasFactory<UserFactory> */
    use HasApiTokens, HasFactory, Notifiable;

    protected $primaryKey = 'user_id';

protected $fillable = [
    'nama_lengkap',
    'username',
    'email',
    'password',
    'foto_profil',
    'bio',
    'is_verified',
    'rating',
    'jumlah_ulasan',
    'waktu_aktif_terakhir'
];

protected $hidden = [
    'password',
    'remember_token'

    
];
protected $appends = [
    'foto_profil_url',
    'followers_count'
];  



public function products()
{
    return $this->hasMany(Product::class, 'seller_id');
}

public function favorites()
{
    return $this->hasMany(Favorite::class, 'user_id');
}

public function offers()
{
    return $this->hasMany(Offer::class, 'buyer_id');
}
public function purchases()
{
    return $this->hasMany(Order::class, 'buyer_id');
}

public function sales()
{
    return $this->hasMany(Order::class, 'seller_id');
}

public function orders()
{
    return $this->hasMany(Order::class, 'buyer_id');
}
public function reviewsReceived()
{
    return $this->hasMany(
        Review::class,
        'seller_id'
    );
}

public function reviewsGiven()
{
    return $this->hasMany(
        Review::class,
        'reviewer_id'
    );
}
public function getFotoProfilUrlAttribute()
{
    if (!$this->foto_profil) {
        return null;
    }

    return asset(
        'storage/' . $this->foto_profil
    );
}

public function getFollowersCountAttribute()
{
    return $this->followers()->count();
}

public function reviews()
{
    return $this->hasMany(Review::class, 'seller_id');
}

public function notifications()
{
    return $this->hasMany(Notification::class, 'user_id');
}
public function followers()
{
    return $this->hasMany(Follow::class, 'following_id');
}

public function following()
{
    return $this->hasMany(Follow::class, 'follower_id');
}

public function sentChats()
{
    return $this->hasMany(Chat::class, 'sender_id');
}

public function receivedChats()
{
    return $this->hasMany(Chat::class, 'receiver_id');
}

}