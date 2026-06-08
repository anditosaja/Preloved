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
        Schema::create('favorites', function (Blueprint $table) {

    $table->id('favorite_id');

    $table->foreignId('user_id')
        ->constrained('users','user_id')
        ->cascadeOnDelete();

    $table->foreignId('product_id')
        ->constrained('products','product_id')
        ->cascadeOnDelete();

    $table->dateTime('tanggal_ditambahkan');

    $table->timestamps();

    $table->unique(['user_id','product_id']);
});
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('favorites');
    }
};
