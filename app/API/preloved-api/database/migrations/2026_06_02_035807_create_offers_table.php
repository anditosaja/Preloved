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
       Schema::create('offers', function (Blueprint $table) {

    $table->id('offer_id');

    $table->foreignId('buyer_id')
        ->constrained('users','user_id')
        ->cascadeOnDelete();

    $table->foreignId('product_id')
        ->constrained('products','product_id')
        ->cascadeOnDelete();

    $table->decimal('harga_tawaran',15,2);

    $table->enum('status_tawaran',[
        'Menunggu',
        'Diterima',
        'Ditolak'
    ])->default('Menunggu');

    $table->dateTime('waktu_tawaran');

    $table->timestamps();
});
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('offers');
    }
};
