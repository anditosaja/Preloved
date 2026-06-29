<?php

namespace App\Http\Resources;

use Illuminate\Http\Resources\Json\JsonResource;

class UserResource extends JsonResource
{
    public function toArray($request)
    {
        return [
            'user_id'      => $this->user_id,
            'nama_lengkap' => $this->nama_lengkap,
            'email'        => $this->email,
            'username'     => $this->username,
            'foto_profil'  => $this->foto_profil,
            'balance'      => (double) $this->balance, // Pastikan dikirim sebagai angka
        ];
    }
}