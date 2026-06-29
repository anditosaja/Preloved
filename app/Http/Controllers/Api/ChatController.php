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
        try {
            // 1. Validasi input dari Android
            $request->validate([
                'product_id' => 'nullable|integer',
                'receiver_id' => 'required|exists:users,user_id',
                'message' => 'required'
            ]);

            // 2. Antisipasi jika product_id dikirim 0 dari chat global
            $productId = ($request->product_id == 0) ? null : $request->product_id;

            // 3. Simpan data chat ke database
            $chat = Chat::create([
                'product_id' => $productId,
                'sender_id' => $request->user()->user_id,
                'receiver_id' => $request->receiver_id,
                'message' => $request->message,
                'is_read' => false
            ]);

            // 4. Kirim notifikasi (Kita bungkus try-catch biar kalau helpernya error, chat-nya tetep masuk database)
            try {
                if (class_exists(\App\Helpers\NotificationHelper::class) || class_exists(NotificationHelper::class)) {
                    NotificationHelper::create(
                        $request->receiver_id,
                        'Pesan Baru',
                        'Anda menerima pesan baru',
                        'chat'
                    );
                }
            } catch (\Exception $notifError) {
                // Notif error diabaikan dulu agar flow chat tidak ikut rusak
            }

            return response()->json([
                'message' => 'Pesan berhasil dikirim',
                'data' => $chat
            ], 200);

        } catch (\Exception $e) {
            // JIKA CRASH, KIRIM PESAN ERROR ASLINYA KE ANDROID
            return response()->json([
                'message' => 'Laravel Crash: ' + $e->getMessage(),
                'file' => $e->getFile(),
                'line' => $e->getLine()
            ], 500);
        }
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
    public function getGlobalUsers(Request $request)
{
    $currentUserId = $request->user()->user_id;

    // Ambil semua user kecuali user yang sedang login
    $users = \App\Models\User::where('user_id', '!=', $currentUserId)->get();

    $result = [];
    foreach ($users as $user) {
        // Cek apakah sudah ada riwayat chat dengan user ini
        $lastChat = \App\Models\Chat::where(function ($q) use ($currentUserId, $user) {
            $q->where('sender_id', $currentUserId)->where('receiver_id', $user->user_id)
              ->orWhere('sender_id', $user->user_id)->where('receiver_id', $currentUserId);
        })->latest()->first();

        $result[] = [
            'user_id' => $user->user_id,
            'nama_lengkap' => $user->nama_lengkap,
            'foto_profil' => $user->foto_profil_url,
            'last_message' => $lastChat ? $lastChat->message : "Belum ada pesan",
            'last_message_time' => $lastChat ? $lastChat->created_at : null
        ];
    }

    return response()->json($result);
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