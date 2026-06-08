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
        Schema::create('reviews', function (Blueprint $table) {
        $table->id('review_id');

        $table->foreignId('order_id')
            ->constrained('orders', 'order_id')
            ->cascadeOnDelete();
        
        $table->foreignId('product_id')
        ->constrained('products', 'product_id')
        ->cascadeOnDelete();
        
        $table->foreignId('reviewer_id')
            ->constrained('users', 'user_id');

        $table->foreignId('seller_id')
            ->constrained('users', 'user_id');

        $table->tinyInteger('rating');

        $table->text('comment')
            ->nullable();

        $table->timestamps();
    });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('reviews');
    }
};
