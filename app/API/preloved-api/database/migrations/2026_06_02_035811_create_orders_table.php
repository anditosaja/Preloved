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
        Schema::create('orders', function (Blueprint $table) {

    $table->id('order_id');

    $table->foreignId('buyer_id')
        ->constrained('users','user_id')
        ->cascadeOnDelete();

    $table->foreignId('product_id')
        ->constrained('products','product_id');

    $table->decimal('total_harga',15,2);

    $table->enum('status_pesanan',[
        'Menunggu',
        'Diproses',
        'Selesai'
    ])->default('Menunggu');

    $table->dateTime('tanggal_transaksi');

    $table->timestamps();
});
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('orders');
    }
};
