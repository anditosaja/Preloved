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

        $table->foreignId('product_id')
            ->constrained('products', 'product_id');

        $table->foreignId('buyer_id')
            ->constrained('users', 'user_id');

        $table->foreignId('seller_id')
            ->constrained('users', 'user_id');

        $table->foreignId('offer_id')
            ->nullable()
            ->constrained('offers', 'offer_id')
            ->nullOnDelete();

        $table->decimal('harga_final', 12, 2);

        $table->enum('status', [
            'pending',
            'paid',
            'shipped',
            'completed',
            'cancelled'
        ])->default('pending');

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
