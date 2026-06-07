<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    public function index(Request $request)
    {
        return response()->json(
            Notification::where(
                'user_id',
                $request->user()->user_id
            )
            ->latest()
            ->get()
        );
    }

    public function unreadCount(Request $request)
    {
        return response()->json([
            'unread_count' =>
                Notification::where(
                    'user_id',
                    $request->user()->user_id
                )
                ->where(
                    'is_read',
                    false
                )
                ->count()
        ]);
    }

    public function markAsRead($id)
    {
        $notification = Notification::findOrFail($id);

        $notification->update([
            'is_read' => true
        ]);

        return response()->json([
            'message' => 'Notifikasi dibaca'
        ]);
    }

    public function markAllAsRead(Request $request)
    {
        Notification::where(
            'user_id',
            $request->user()->user_id
        )
        ->update([
            'is_read' => true
        ]);

        return response()->json([
            'message' => 'Semua notifikasi dibaca'
        ]);
    }
}