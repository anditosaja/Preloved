<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Chat;
use App\Models\Product;
use Illuminate\Http\Request;

class ChatController extends Controller
{
    public function rooms(Request $request)
{
    $userId = $request->user()->user_id;

    $rooms = Chat::with([
        'sender',
        'receiver',
        'product'
    ])
    ->where(function ($q) use ($userId) {
        $q->where('sender_id', $userId)
          ->orWhere('receiver_id', $userId);
    })
    ->latest()
    ->get()
    ->groupBy(function ($chat) use ($userId) {

        return $chat->sender_id == $userId
            ? $chat->receiver_id
            : $chat->sender_id;
    });

    $result = [];

    foreach ($rooms as $partnerId => $messages) {

        $lastMessage = $messages->first();

        $partner = $lastMessage->sender_id == $userId
            ? $lastMessage->receiver
            : $lastMessage->sender;

        $unread = Chat::where(
            'sender_id',
            $partnerId
        )
        ->where(
            'receiver_id',
            $userId
        )
        ->where(
            'is_read',
            false
        )
        ->count();

        $result[] = [
            'user_id' => $partner->user_id,
            'nama_lengkap' => $partner->nama_lengkap,
            'foto_profil' => $partner->foto_profil_url,
            'last_message' => $lastMessage->message,
            'last_message_time' => $lastMessage->created_at,
            'unread_count' => $unread,
            'product' => $lastMessage->product
        ];
    }

    return response()->json($result);
}
    public function send(Request $request)
    {
        $request->validate([
            'product_id' => 'required|exists:products,product_id',
            'receiver_id' => 'required|exists:users,user_id',
            'message' => 'required'
        ]);

        $chat = Chat::create([
            'product_id' => $request->product_id,
            'sender_id' => $request->user()->user_id,
            'receiver_id' => $request->receiver_id,
            'message' => $request->message,
            'is_read' => false
        ]);
        NotificationHelper::create(
        $request->receiver_id,
        'Pesan Baru',
        'Anda menerima pesan baru',
        'chat'
        );
        return response()->json([
            'message' => 'Pesan berhasil dikirim',
            'data' => $chat
        ]);
    }

    public function conversations(Request $request)
    {
        $userId = $request->user()->user_id;

        $chats = Chat::with([
            'sender',
            'receiver',
            'product'
        ])
        ->where('sender_id', $userId)
        ->orWhere('receiver_id', $userId)
        ->latest()
        ->get();

        return response()->json($chats);
    }

    public function detail(Request $request, $userId)
    {
        $currentUser = $request->user()->user_id;

        $messages = Chat::with([
            'sender',
            'receiver'
        ])
        ->where(function ($q) use ($currentUser, $userId) {
            $q->where('sender_id', $currentUser)
              ->where('receiver_id', $userId);
        })
        ->orWhere(function ($q) use ($currentUser, $userId) {
            $q->where('sender_id', $userId)
              ->where('receiver_id', $currentUser);
        })
        ->orderBy('created_at')
        ->get();

        Chat::where('sender_id', $userId)
            ->where('receiver_id', $currentUser)
            ->update([
                'is_read' => true
            ]);

        return response()->json($messages);
    }
}