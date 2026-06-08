<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Banner extends Model
{
    protected $primaryKey = 'banner_id';

protected $fillable = [
    'title',
    'image_url',
    'link',
    'is_active'
];
}
