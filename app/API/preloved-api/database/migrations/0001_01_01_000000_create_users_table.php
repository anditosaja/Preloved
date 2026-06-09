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
        Schema::create('users', function (Blueprint $table) {
    $table->id('user_id');

    $table->string('nama_lengkap');
    $table->string('username')->unique();
    $table->string('email')->unique();
    $table->string('password');
    $table->string('foto_profil')->nullable();

    $table->boolean('is_verified')->default(false);

    $table->float('rating')->default(0);
    $table->integer('jumlah_ulasan')->default(0);

    $table->decimal('saldo_preloved',15,2)->default(0);

    $table->dateTime('waktu_aktif_terakhir')->nullable();

    $table->timestamps();
});
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('users');
        Schema::dropIfExists('password_reset_tokens');
        Schema::dropIfExists('sessions');
    }
};
