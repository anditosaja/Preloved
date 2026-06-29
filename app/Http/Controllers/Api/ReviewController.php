<?php

namespace App\Http\Controllers\Api;

use App\Models\Order;
use App\Models\Review;
use App\Models\User;
use App\Services\NotificationService;
use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

class ReviewController extends Controller
{
   public function store(Request $request)
{
    $request->validate([
        'order_id' => 'required|exists:orders,order_id',
        'rating' => 'required|integer|min:1|max:5',
        'comment' => 'nullable|string'
    ]);

    $order = Order::findOrFail(
        $request->order_id
    );

    if ($order->buyer_id != $request->user()->user_id) {
        return response()->json([
            'message' => 'Bukan pembeli'
        ], 403);
    }

    $exists = Review::where(
        'order_id',
        $order->order_id
    )->exists();

    if ($exists) {
        return response()->json([
            'message' => 'Review sudah dibuat'
        ], 422);
    }

    $review = Review::create([
        'order_id' => $order->order_id,
        'reviewer_id' => $order->buyer_id,
        'product_id' => $order->product_id,
        'seller_id' => $order->seller_id,
        'rating' => $request->rating,
        'comment' => $request->comment
    ]);

    $avgRating = Review::where(
        'seller_id',
        $order->seller_id
    )->avg('rating');

    $reviewCount = Review::where(
        'seller_id',
        $order->seller_id
    )->count();

    User::where(
        'user_id',
        $order->seller_id
    )->update([
        'rating' => round($avgRating, 1),
        'jumlah_ulasan' => $reviewCount
    ]);

    NotificationService::create(
        $order->seller_id,
        'Ulasan Baru',
        'Anda menerima ulasan baru',
        'review'
    );

    return response()->json([
        'message' => 'Review berhasil dibuat',
        'data' => $review
    ]);
}

public function sellerReviews($sellerId)
{
    return response()->json(
        Review::with([
            'reviewer',
            'product.images',
            'product.seller'
        ])
        ->where(
            'seller_id',
            $sellerId
        )
        ->latest()
        ->get()
    );
}

public function myReviews(Request $request)
{
    return response()->json(
        Review::with([
            'seller'
        ])
        ->where(
            'reviewer_id',
            $request->user()->user_id
        )
        ->latest()
        ->get()
    );
}
}
