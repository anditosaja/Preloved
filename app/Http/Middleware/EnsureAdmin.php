<?php

namespace App\Http\Middleware;

use App\Models\Admin;
use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class EnsureAdmin
{
    /**
     * Pastikan personal access token yang dipakai pada request ini
     * memang milik model Admin (bukan token milik User biasa), dan
     * akun admin tersebut masih aktif.
     */
    public function handle(Request $request, Closure $next): Response
    {
        $authable = $request->user();

        if (! $authable instanceof Admin) {
            return response()->json([
                'message' => 'Akses ditolak. Hanya admin yang dapat mengakses resource ini.',
            ], 403);
        }

        if (! $authable->is_active) {
            return response()->json([
                'message' => 'Akun admin ini telah dinonaktifkan.',
            ], 403);
        }

        return $next($request);
    }
}
