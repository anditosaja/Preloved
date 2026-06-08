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
        Schema::create('follows', function (Blueprint $table) {

    $table->id('follow_id');

    $table->foreignId('follower_id')
        ->constrained('users','user_id')
        ->cascadeOnDelete();

    $table->foreignId('following_id')
        ->constrained('users','user_id')
        ->cascadeOnDelete();

    $table->dateTime('waktu_follow');

    $table->timestamps();

    $table->unique([
        'follower_id',
        'following_id'
    ]);
});
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('follows');
    }
};
