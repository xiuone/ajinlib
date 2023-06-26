package com.xy.base.widget.image.circle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.xy.base.utils.exp.cropBitmap
import kotlin.math.min

open class CircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RoundBaseImageView(context, attrs, defStyleAttr){

    override fun drawBitmap(canvas: Canvas?, rawBitmap: Bitmap,paintBitmap:Paint) {
        val dstWidth = builder.getViewMinSize().toFloat()

        val bitmapMinSize = min(rawBitmap.width,rawBitmap.height)
        val resetBitmapStartX = (rawBitmap.width - bitmapMinSize)/2
        val resetBitmapStartY = (rawBitmap.height - bitmapMinSize)/2
        val newRawBitmap = rawBitmap.cropBitmap(resetBitmapStartX,resetBitmapStartY,bitmapMinSize,bitmapMinSize)

        val mDrawableRectWidth = width - paddingLeft - paddingRight
        val mDrawableRectHeight = height - paddingTop - paddingBottom

        if (mShader == null || rawBitmap != mRawBitmap) {
            mRawBitmap = rawBitmap
            mShader = BitmapShader(newRawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            val scale = dstWidth / bitmapMinSize
            mMatrix.setScale(scale, scale)
            val dx = (mDrawableRectWidth - bitmapMinSize * scale) * 0.5f;
            val dy = (mDrawableRectHeight - bitmapMinSize * scale) * 0.5f;
            mMatrix.postTranslate( (dx + 0.5f) + paddingLeft,  (dy + 0.5f) + paddingTop)
            mShader?.setLocalMatrix(mMatrix)
        }

        paintBitmap.shader = mShader

        val centerX = paddingLeft + (width - paddingLeft - paddingRight)/2F
        val centerY = paddingTop + (height - paddingTop - paddingBottom)/2F

        val radius = builder.getViewMinSize()/2F - builder.frameSize
        canvas?.drawCircle(centerX, centerY, radius, paintBitmap)
    }

    override fun drawFrame(canvas: Canvas?, paint: Paint) {
        var radius = builder.getViewMinSize()/2F
        radius -= paint.strokeWidth / 2
        canvas?.drawCircle(width/2F, height/2F, radius, paint)
    }
}