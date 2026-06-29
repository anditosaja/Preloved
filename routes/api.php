<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ProductController;
use App\Http\Controllers\Api\HomeController;
use App\Http\Controllers\Api\CategoryController;
use App\Http\Controllers\Api\FavoriteController;
use App\Http\Controllers\Api\ProfileController;
use App\Http\Controllers\Api\FollowController;
use App\Http\Controllers\Api\OfferController;
use App\Http\Controllers\Api\ChatController;
use App\Http\Controllers\Api\NotificationController;
use App\Http\Controllers\Api\OrderController;
use App\Http\Controllers\Api\ReviewController;
use App\Http\Controllers\Api\ProductImageController;

use App\Http\Controllers\Api\Admin\AdminAuthController;
use App\Http\Controllers\Api\Admin\AdminDashboardController;
use App\Http\Controllers\Api\Admin\AdminUserController;
use App\Http\Controllers\Api\Admin\AdminProductController;
use App\Http\Controllers\Api\Admin\AdminOrderController;
use App\Http\Controllers\Api\Admin\AdminComplaintController;

/*
|--------------------------------------------------------------------------
| Rute Publik & User 
|--------------------------------------------------------------------------
*/

Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

Route::get('/products/search', [ProductController::class, 'search']);
Route::get('/products', [ProductController::class, 'index']);
Route::get('/products/{id}', [ProductController::class, 'show']);
Route::get('/home', [HomeController::class, 'index']);
Route::get('/categories', [CategoryController::class, 'index']);

Route::get(
    '/products/search',
    [ProductController::class, 'search']
);

Route::get('/categories/{id}/products',[CategoryController::class, 'products']
);

Route::get('/categories/populer', [CategoryController::class, 'populer']);

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/favorites', [FavoriteController::class, 'index']);
    Route::post('/favorites/{productId}', [FavoriteController::class, 'store']);
    Route::delete('/favorites/{productId}', [FavoriteController::class, 'destroy']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/notifications', [NotificationController::class, 'index']);
    Route::get('/notifications/unread-count', [NotificationController::class, 'unreadCount']);
    Route::put('/notifications/{id}/read', [NotificationController::class, 'markAsRead']);
    Route::put('/notifications/read-all', [NotificationController::class, 'markAllAsRead']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/profile', [ProfileController::class, 'show']);
    Route::put('/profile', [ProfileController::class, 'update']);
    Route::post('/profile/photo', [ProfileController::class, 'uploadPhoto']);
    Route::get('/profile/{userId}', [ProfileController::class, 'seller']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/products', [ProductController::class, 'store']);
    Route::put('/products/{id}', [ProductController::class, 'update']);
    Route::delete('/products/{id}', [ProductController::class, 'destroy']);
    Route::post('/products/{id}/images', [ProductImageController::class, 'store']);
    Route::delete('/product-images/{id}', [ProductImageController::class, 'destroy']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/following', [FollowController::class, 'index']);
    Route::post('/follow/{sellerId}', [FollowController::class, 'follow']);
    Route::delete('/follow/{sellerId}', [FollowController::class, 'unfollow']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/chats', [ChatController::class, 'send']);
    Route::get('/chats/{userId}', [ChatController::class, 'detail']);
    Route::get('/chat-rooms', [ChatController::class, 'rooms']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/offers', [OfferController::class, 'store']);
    Route::get('/offers', [OfferController::class, 'myOffers']);
    Route::get('/offers/received', [OfferController::class, 'receivedOffers']);
    Route::put('/offers/{id}/accept', [OfferController::class, 'accept']);
    Route::put('/offers/{id}/reject', [OfferController::class, 'reject']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/orders', [OrderController::class, 'store']);
    Route::get('/orders/purchases', [OrderController::class, 'myPurchases']);
    Route::get('/orders/sales', [OrderController::class, 'mySales']);
    Route::get('/orders/{id}', [OrderController::class, 'show']);
    Route::put('/orders/{id}/status', [OrderController::class, 'updateStatus']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/reviews', [ReviewController::class, 'store']);
    Route::get('/reviews/my', [ReviewController::class, 'myReviews']);
    Route::get('/reviews/seller/{sellerId}', [ReviewController::class, 'sellerReviews']);
});

Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me', [AuthController::class, 'me']);
});

/*
|--------------------------------------------------------------------------
| Rute Khusus Admin
|--------------------------------------------------------------------------
| Login admin terbuka untuk publik (tidak butuh token). Semua rute lain
| di bawah prefix /admin wajib login via token Sanctum DAN harus berupa
| akun Admin (dicek oleh middleware 'admin' / EnsureAdmin).
*/

Route::prefix('admin')->group(function () {

    Route::post('/login', [AdminAuthController::class, 'login']);
    
    Route::middleware(['auth:sanctum', 'admin'])->group(function () {
        
        
           
    Route::post('/logout', [AdminAuthController::class, 'logout']);
        Route::get('/me', [AdminAuthController::class, 'me']);

        // Dashboard
        Route::get('/dashboard/summary', [AdminDashboardController::class, 'summary']);
        Route::get('/dashboard/chart-transaksi', [AdminDashboardController::class, 'chartTransaksi']);
        Route::get('/dashboard/chart', [AdminDashboardController::class, 'chartData']);

        // Kelola Pengguna
        Route::get('/users', [AdminUserController::class, 'index']);
        Route::get('/users/{id}', [AdminUserController::class, 'show']);
        Route::put('/users/{id}/block', [AdminUserController::class, 'block']);
        Route::put('/users/{id}/unblock', [AdminUserController::class, 'unblock']);
        Route::delete('/users/{id}', [AdminUserController::class, 'destroy']);

        // Kelola Produk
        Route::get('/products', [AdminProductController::class, 'index']);
        Route::get('/products/{id}', [AdminProductController::class, 'show']);
        Route::put('/products/{id}/approve', [AdminProductController::class, 'approve']);
        Route::put('/products/{id}/suspend', [AdminProductController::class, 'suspend']);
        Route::put('/products/{id}/reject', [AdminProductController::class, 'reject']);
        Route::delete('/products/{id}', [AdminProductController::class, 'destroy']);

        // KELOLA KOMPLAIN
        Route::get('/complaints', [App\Http\Controllers\Api\Admin\AdminComplaintController::class, 'index']);
         Route::put('/complaints/{id}/status', [App\Http\Controllers\Api\Admin\AdminComplaintController::class, 'updateStatus']);

        // Kelola Transaksi
        Route::get('/orders', [AdminOrderController::class, 'index']);
        Route::get('/orders/{id}', [AdminOrderController::class, 'show']);
        Route::put('/orders/{id}/status', [AdminOrderController::class, 'updateStatus']);
    });
});
