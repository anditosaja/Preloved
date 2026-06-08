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

        $table->foreignId('product_id')
            ->constrained('products', 'product_id')
            ->cascadeOnDelete();

        $table->foreignId('buyer_id')
            ->constrained('users', 'user_id')
            ->cascadeOnDelete();

        $table->decimal('harga_tawaran', 12, 2);

        $table->enum('status', [
            'pending',
            'accepted',
            'rejected',
            'countered'
        ])->default('pending');

        $table->unsignedBigInteger('parent_offer_id')
            ->nullable();

        $table->foreign('parent_offer_id')
            ->references('offer_id')
            ->on('offers')
            ->nullOnDelete();

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
