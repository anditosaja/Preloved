<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Storage;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        $request->validate([
            'nama_lengkap'=>'required',
            'username'=>'required|unique:users',
            'email'=>'required|email|unique:users',
            'password'=>'required|min:6'
        ]);

        $user = User::create([
            'nama_lengkap'=>$request->nama_lengkap,
            'username'=>$request->username,
            'email'=>$request->email,
            'password'=>Hash::make($request->password)
        ]);

        return response()->json($user,201);
    }

    public function login(Request $request)
    {
        $user = User::where('email',$request->email)->first();

        if(!$user || !Hash::check($request->password,$user->password))
        {
            return response()->json([
                'message'=>'Login gagal'
            ],401);
        }

        $token = $user->createToken('android')->plainTextToken;

        return response()->json([
            'token'=>$token,
            'user'=>$user
        ]);
    }

    public function profile(Request $request)
{
    return response()->json([
        'user' => $request->user()
    ]);
}

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();


        return response()->json([
            'message'=>'Logout berhasil'
        ]);
    }

    public function uploadProfile(Request $request)
{
    $request->validate([
        'foto_profil' => 'required|image|mimes:jpg,jpeg,png|max:2048'
    ]);

    $user = $request->user();

    $path = $request
        ->file('foto_profil')
        ->store('profiles','public');

    $user->foto_profil = $path;
    $user->save();

    return response()->json([
        'message'=>'Foto profil berhasil diupload',
        'foto'=>$path
    ]);
}
}