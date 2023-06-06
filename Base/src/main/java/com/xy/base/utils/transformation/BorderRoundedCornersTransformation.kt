package com.xy.base.utils.transformation

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class BorderRoundedCornersTransformation(private val radius: Float,private val margin: Float,
                                         private val borderColor: Int = -1,private val borderWidth:Int = 0) :
    BitmapTransformation() {
    private val VERSION = 1
    private val ID = "com.xy.base.utils.RoundedCornersTransformation.$VERSION"
    private val diameter: Float by lazy { radius * 2 }
    private val borderPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val width = toTransform.width
        val height = toTransform.height
        val bitmap = pool[width, height, Bitmap.Config.ARGB_8888]
        bitmap.setHasAlpha(true)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        drawRoundRect(canvas, paint, width.toFloat(), height.toFloat())
        return bitmap
    }

    private fun drawRoundRect(canvas: Canvas, paint: Paint, width: Float, height: Float) {
        val right = width - margin
        val bottom = height - margin
        canvas.drawRoundRect(RectF(margin, margin, right, bottom), radius, radius, paint)

        if (borderColor != -1 && borderWidth >0) {
            borderPaint.style = Paint.Style.STROKE
            borderPaint.strokeWidth = borderWidth.toFloat()
            borderPaint.color = borderColor
            canvas.drawRoundRect(RectF(margin + borderWidth / 2, margin + borderWidth / 2, right - borderWidth / 2,
                bottom - borderWidth / 2), radius, radius, borderPaint
            )
        }
    }

    override fun toString(): String {
        return ("RoundedTransformation(radius=" + radius + ", margin=" + margin + ", diameter="
                + diameter + ")")
    }

    override fun equals(o: Any?): Boolean {
        return o is BorderRoundedCornersTransformation && o.radius == radius && o.diameter == diameter && o.margin == margin
    }

    override fun hashCode(): Int {
        return (ID.hashCode() + radius * 10000 + diameter * 1000 + margin * 100).toInt()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + radius + diameter + margin).toByteArray(CHARSET))
    }


}