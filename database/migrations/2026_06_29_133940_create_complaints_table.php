<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::create('complaints', function (Blueprint $table) {
            $table->id();
            $table->string('ticket_id')->unique(); 
            
            // Kolom kita buat manual tanpa "constrained()". 
            // Anti-gagal dan anti-bentrok tipe data!
            $table->unsignedBigInteger('user_id'); 
            $table->unsignedBigInteger('product_id')->nullable(); 
            
            $table->string('subject'); 
            $table->text('description')->nullable(); 
            $table->enum('status', ['pending', 'processing', 'resolved'])->default('pending'); 
            $table->timestamps();
        });
    }

    public function down()
    {
        Schema::dropIfExists('complaints');
    }
};