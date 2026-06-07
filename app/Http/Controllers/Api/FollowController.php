<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Follow;
use App\Models\User;
use Illuminate\Http\Request;
use App\Helpers\NotificationHelper;

class FollowController extends Controller
{
    public function index(Request $request)
    {
        $following = Follow::with('following')
            ->where('follower_id', $request->user()->user_id)
            ->get();

        return response()->json($following);
    }

    public function follow(Request $request, $sellerId)
    {
        if ($request->user()->user_id == $sellerId) {
            return response()->json([
                'message' => 'Tidak bisa follow diri sendiri'
            ], 422);
        }

        User::findOrFail($sellerId);

        $follow = Follow::firstOrCreate([
            'follower_id' => $request->user()->user_id,
            'following_id' => $sellerId
        ]);
        NotificationHelper::create(
        $sellerId,
        'Follower Baru',
        'Seseorang mulai mengikuti akun Anda',
        'follow'
        );
        return response()->json([
            'message' => 'Berhasil follow seller',
            'data' => $follow
        ]);
    }

    public function unfollow(Request $request, $sellerId)
    {
        Follow::where([
            'follower_id' => $request->user()->user_id,
            'following_id' => $sellerId
        ])->delete();

        return response()->json([
            'message' => 'Berhasil unfollow seller'
        ]);
    }
    
}