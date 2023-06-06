package com.xy.base.utils.transformation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.xy.base.utils.exp.blur
import java.security.MessageDigest

class BlurTransformation (private val radius: Int , private val sampling: Int) : BitmapTransformation() {
    private val VERSION = 1
    private val ID = "BlurTransformation.$VERSION"

    override fun toString(): String = "BlurTransformation(radius=$radius, sampling=$sampling)"

    override fun equals(o: Any?): Boolean = o is BlurTransformation && o.radius == radius && o.sampling == sampling

    override fun hashCode(): Int = ID.hashCode() + radius * 1000 + sampling * 10

    override fun updateDiskCacheKey(messageDigest: MessageDigest) =
        messageDigest.update((ID + radius + sampling).toByteArray(CHARSET))

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val width = toTransform.width
        val height = toTransform.height
        val scaledWidth = width / sampling
        val scaledHeight = height / sampling
        var bitmap = pool[scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888]
        bitmap.density = toTransform.density
        val canvas = Canvas(bitmap)
        canvas.scale(1 / sampling.toFloat(), 1 / sampling.toFloat())
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(toTransform, 0f, 0f, paint)
        bitmap = bitmap.blur(radius, true)?:bitmap
        return bitmap
    }
}