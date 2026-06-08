<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Chat;
use Illuminate\Http\Request;

class ChatController extends Controller
{
    public function index()
    {
        return Chat::with([
            'sender',
            'receiver',
            'product'
        ])->latest()->get();
    }

    public function store(Request $request)
    {
        return Chat::create([
            'sender_id'=>$request->sender_id,
            'receiver_id'=>$request->receiver_id,
            'product_id'=>$request->product_id,
            'isi_pesan'=>$request->isi_pesan,
            'waktu_kirim'=>now()
        ]);
    }
}