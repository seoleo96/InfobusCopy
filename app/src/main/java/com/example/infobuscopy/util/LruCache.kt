package com.example.infobuscopy.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.util.LruCache
import androidx.core.content.ContextCompat

class LruCacheImpl {

    private val lruCache: LruCache<String, Bitmap?> by lazy {
        LruCache<String, Bitmap?>(1024)
    }

    fun saveBitmapDescriptor(context: Context, vectorResId: Int, busNumber: String, invalidAdapted: Boolean, cacheName : String) {
        val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return
        drawable.setBounds(60, 0, 160, 100)
        val bitmap = Bitmap.createBitmap(
            if(invalidAdapted) 200 else 124,
            100,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        val paintContent = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintContent.color = Color.BLACK
        paintContent.style = Paint.Style.FILL
        paintStroke.strokeWidth = 4f
        paintStroke.color = Color.WHITE
        paintStroke.style = Paint.Style.FILL
        paintText.textSize = 24f
        paintText.color = Color.BLACK
        canvas.drawRoundRect(
            44f,
            26f,
            if(invalidAdapted) 194f else 124f,
            84f,
            44f,
            44f,
            paintContent
        )
        canvas.drawRoundRect(
            40f,
            30f,
            if(invalidAdapted) 190f else 120f,
            80f,
            40f,
            40f,
            paintStroke
        )
        val busNumberAndInvalidAdapted = if(invalidAdapted) "$busNumber | new" else "$busNumber"
        canvas.drawText(busNumberAndInvalidAdapted, 80f, 64f, paintText)
        drawable.draw(canvas)
        lruCache.put(cacheName, bitmap).also {
            Log.d(TAG, "saveBitmapDescriptor: ${it == null}")
        }
    }

    private var count = 0
    fun getBitmapDescriptor(busNumber: String): Bitmap? {
        val bitmapDescriptor = lruCache.get(busNumber)
        Log.e(TAG, "getBitmapDescriptor: ${++count} $bitmapDescriptor")
        return bitmapDescriptor
    }

    companion object {
        private const val TAG = "LruCache"
    }
}