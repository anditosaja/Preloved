<?php

namespace Database\Seeders;

use App\Models\Admin;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class AdminSeeder extends Seeder
{
    /**
     * Membuat 1 akun super_admin default untuk keperluan testing awal.
     * Jalankan dengan: php artisan db:seed --class=AdminSeeder
     */
    public function run(): void
    {
        Admin::create([
            'nama_lengkap' => 'Super Admin',
            'username' => 'superadmin',
            'email' => 'admin@preloved.com',
            'password' => Hash::make('admin123'),
            'level' => 'super_admin',
            'is_active' => true,
        ]);
    }
}
