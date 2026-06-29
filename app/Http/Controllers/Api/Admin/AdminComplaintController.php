<?php

namespace App\Http\Controllers\Api\Admin;

use App\Http\Controllers\Controller;
use App\Models\Complaint;
use App\Services\ApiResponse;
use Illuminate\Http\Request;

class AdminComplaintController extends Controller
{
    // Ambil daftar komplain + dukung filter tab (Semua, Menunggu, Diproses, Selesai)
    public function index(Request $request)
    {
        $query = Complaint::with(['user', 'product']);

        if ($request->filled('status')) {
            $query->where('status', $request->status);
        }

        $complaints = $query->latest()->get();

        return ApiResponse::success($complaints);
    }

    // Admin ubah status komplain
    public function updateStatus(Request $request, $id)
    {
        $request->validate([
            'status' => 'required|in:pending,processing,resolved',
        ]);

        $complaint = Complaint::findOrFail($id);
        $complaint->update(['status' => $request->status]);

        return ApiResponse::success($complaint, 'Status komplain berhasil diperbarui');
    }
}