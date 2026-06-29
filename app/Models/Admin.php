<?php

namespace App\Models;

use Laravel\Sanctum\HasApiTokens;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;

class Admin extends Authenticatable
{
    use HasApiTokens, Notifiable;

    protected $primaryKey = 'admin_id';

    protected $fillable = [
        'nama_lengkap',
        'username',
        'email',
        'password',
        'level',
        'is_active',
        'waktu_aktif_terakhir',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected $casts = [
        'is_active' => 'boolean',
    ];

    public function isSuperAdmin(): bool
    {
        return $this->level === 'super_admin';
    }
}
