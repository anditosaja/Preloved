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

    $table->foreignId('sender_id')
        ->constrained('users','user_id')
        ->cascadeOnDelete();

    $table->foreignId('receiver_id')
        ->constrained('users','user_id')
        ->cascadeOnDelete();

    $table->foreignId('product_id')
        ->nullable()
        ->constrained('products','product_id')
        ->nullOnDelete();

    $table->text('isi_pesan');

    $table->dateTime('waktu_kirim');

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
