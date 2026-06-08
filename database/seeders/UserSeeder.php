<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    public function run(): void
    {
        User::create([
            'nama_lengkap' => 'Ananda Naufal',
            'username' => 'ananda',
            'email' => 'ananda@gmail.com',
            'password' => Hash::make('password'),
            'is_verified' => true,
            'rating' => 4.9,
            'jumlah_ulasan' => 56
        ]);

        User::create([
            'nama_lengkap' => 'Nadira Putri',
            'username' => 'nadira',
            'email' => 'nadira@gmail.com',
            'password' => Hash::make('password'),
            'is_verified' => true,
            'rating' => 4.8,
            'jumlah_ulasan' => 120
        ]);
    }
}