<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Follow;
use Illuminate\Http\Request;

class FollowController extends Controller
{
    public function index()
    {
        return Follow::with([
            'follower',
            'following'
        ])->get();
    }

    public function store(Request $request)
    {
        return Follow::create([
            'follower_id'=>$request->follower_id,
            'following_id'=>$request->following_id,
            'waktu_follow'=>now()
        ]);
    }
}