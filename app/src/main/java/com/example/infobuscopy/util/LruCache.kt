package com.example.infobuscopy.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class LruCacheImpl {

    private val lruCache: LruCache<String, Bitmap?> by lazy {
        LruCache<String, Bitmap?>(1024)
    }

    fun saveBitmapDescriptor(context: Context, vectorResId: Int) {
        val bitmapDescriptor = lruCache.get("bitmap_image")
        Log.e(TAG, "saveBitmapDescriptor: ${bitmapDescriptor == null}")
        if(bitmapDescriptor != null){
            return
        }
        val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return
        drawable.setBounds(0, 0, 80, 80)
        val bitmap = Bitmap.createBitmap(
            80,
            80,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        drawable.draw(canvas)
        Log.e(TAG, "saveBitmapDescriptor SAVED DONE: ${bitmapDescriptor == null}")
        lruCache.put("bitmap_image", bitmap).also {
            Log.d(TAG, "saveBitmapDescriptor: $it")
        }
    }

    var count = 0
    fun getBitmapDescriptor(): Bitmap? {
        val bitmapDescriptor = lruCache.get("bitmap_image")
        Log.e(TAG, "getBitmapDescriptor: ${++count} $bitmapDescriptor", )
        return bitmapDescriptor
    }

    companion object {
        private const val TAG = "LruCache"
    }
}