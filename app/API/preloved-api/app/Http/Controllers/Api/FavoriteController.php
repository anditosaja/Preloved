<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Favorite;
use Illuminate\Http\Request;

class FavoriteController extends Controller
{
    public function index()
    {
        return Favorite::with([
            'user',
            'product'
        ])->get();
    }

    public function store(Request $request)
    {
        return Favorite::create([
            'user_id'=>$request->user_id,
            'product_id'=>$request->product_id,
            'tanggal_ditambahkan'=>now()
        ]);
    }

    public function destroy($id)
    {
        Favorite::destroy($id);

        return response()->json([
            'message'=>'Favorite dihapus'
        ]);
    }
}