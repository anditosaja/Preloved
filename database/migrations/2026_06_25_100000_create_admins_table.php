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
        Schema::create('admins', function (Blueprint $table) {
            $table->id('admin_id');

            $table->string('nama_lengkap');
            $table->string('username')->unique();
            $table->string('email')->unique();
            $table->string('password');

            // super_admin bisa kelola admin lain, admin hanya kelola data operasional
            $table->enum('level', ['admin', 'super_admin'])->default('admin');

            $table->boolean('is_active')->default(true);
            $table->timestamp('waktu_aktif_terakhir')->nullable();

            $table->rememberToken();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('admins');
    }
};
