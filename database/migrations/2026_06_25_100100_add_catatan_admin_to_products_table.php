<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // 1. Perluas enum status_barang agar admin bisa menangguhkan/menolak produk.
        //    Driver MySQL/MariaDB butuh raw statement untuk modifikasi ENUM.
        DB::statement("ALTER TABLE products MODIFY status_barang ENUM('available', 'reserved', 'sold', 'ditangguhkan', 'ditolak') DEFAULT 'available'");

        // 2. Tambah kolom catatan moderasi dari admin.
        Schema::table('products', function (Blueprint $table) {
            $table->text('catatan_admin')->nullable()->after('status_barang');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('products', function (Blueprint $table) {
            $table->dropColumn('catatan_admin');
        });

        DB::statement("ALTER TABLE products MODIFY status_barang ENUM('available', 'reserved', 'sold') DEFAULT 'available'");
    }
};
