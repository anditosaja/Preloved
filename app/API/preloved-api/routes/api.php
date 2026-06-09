<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ProductController;
use App\Http\Controllers\Api\CategoryController;
use App\Http\Controllers\Api\FavoriteController;
use App\Http\Controllers\Api\FollowController;
use App\Http\Controllers\Api\ChatController;
use App\Http\Controllers\Api\OfferController;
use App\Http\Controllers\Api\OrderController;

//Route API: Login-Register
Route::post('/register',[AuthController::class,'register']);
Route::post('/login',[AuthController::class,'login']);

//Route API: Profile
Route::middleware('auth:sanctum')->get(
    '/profile',
    [AuthController::class,'profile']
);

//Route API: Logout
Route::middleware('auth:sanctum')->post(
    '/logout',
    [AuthController::class,'logout']
);

//Route API: Category
Route::get(
    '/products/category/{id}',
    [ProductController::class,'byCategory']
);

//Route API: Search Produk
Route::get(
    '/products/search',
    [ProductController::class,'search']
);

//Route API: Produk Saya
Route::get(
    '/my-products',
    [ProductController::class,'myProducts']
)->middleware('auth:sanctum');

//Route API: New Product
Route::get(
    '/latest-products',
    [ProductController::class,'latestProducts']
);

//Route API: Favorite Product
Route::get(
    '/favorites/user/{id}',
    [FavoriteController::class,'myFavorites']
);

//Route API: Fungsi Aplikasi
Route::apiResource('products',ProductController::class);
Route::apiResource('categories',CategoryController::class);

Route::apiResource('favorites',FavoriteController::class);
Route::apiResource('follows',FollowController::class);

Route::apiResource('chats',ChatController::class);
Route::apiResource('offers',OfferController::class);
Route::apiResource('orders',OrderController::class);

//Foto Profil
Route::post(
    '/profile/upload',
    [AuthController::class,'uploadProfile']
)->middleware('auth:sanctum');

//Route API: Offer
Route::put(
    '/offers/{id}/status',
    [OfferController::class,'updateStatus']
);

//Route API: Order
Route::put(
    '/orders/{id}/status',
    [OrderController::class,'updateStatus']
);

//Route API: Upload Produk
Route::post(
    '/products/{id}/upload-images',
    [ProductController::class,'uploadImages']
);



