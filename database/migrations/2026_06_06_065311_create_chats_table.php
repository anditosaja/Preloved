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
        Schema::create('chats', function (Blueprint $table) {
        $table->id('chat_id');

        $table->foreignId('product_id')
            ->constrained('products', 'product_id')
            ->cascadeOnDelete();

        $table->foreignId('sender_id')
            ->constrained('users', 'user_id')
            ->cascadeOnDelete();

        $table->foreignId('receiver_id')
            ->constrained('users', 'user_id')
            ->cascadeOnDelete();

        $table->text('message');

        $table->boolean('is_read')
            ->default(false);

        $table->timestamps();
    });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('chats');
    }
};
