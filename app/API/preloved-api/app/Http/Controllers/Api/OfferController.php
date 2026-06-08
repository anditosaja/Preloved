<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Offer;
use Illuminate\Http\Request;

class OfferController extends Controller
{
    public function index()
    {
        return Offer::with([
            'buyer',
            'product'
        ])->get();
    }

    public function store(Request $request)
    {
        return Offer::create([
            'buyer_id'=>$request->buyer_id,
            'product_id'=>$request->product_id,
            'harga_tawaran'=>$request->harga_tawaran,
            'status_tawaran'=>'Menunggu',
            'waktu_tawaran'=>now()
        ]);
    }

    public function updateStatus(Request $request,$id)
    {
        $offer = Offer::findOrFail($id);

        $offer->status_tawaran = $request->status_tawaran;
        $offer->save();

        return response()->json($offer);
    }
}