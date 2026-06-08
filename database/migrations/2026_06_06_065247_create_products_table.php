<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
         Schema::create('products', function (Blueprint $table) {
        $table->id('product_id');

        $table->foreignId('seller_id')
            ->constrained('users', 'user_id')
            ->cascadeOnDelete();

        $table->foreignId('category_id')
            ->constrained('categories', 'category_id');

        $table->string('nama_barang');
        $table->text('deskripsi');

        $table->decimal('harga_jual', 12, 2);

        $table->decimal('harga_asli', 12, 2)
            ->nullable();

        $table->enum('kondisi', [
            'baru',
            'sangat_baik',
            'baik',
            'cukup'
        ]);

        $table->string('merek')->nullable();
        $table->string('warna')->nullable();

        $table->string('lokasi_kota');

        $table->enum('status_barang', [
            'available',
            'reserved',
            'sold'
        ])->default('available');

        $table->integer('jumlah_dilihat')->default(0);

        $table->timestamps();
    });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('products');
    }
};
