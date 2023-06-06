package com.xy.base.widget.image.circle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

open class CircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RoundBaseImageView(context, attrs, defStyleAttr){

    override fun drawBitmap(canvas: Canvas?, rawBitmap: Bitmap,paintBitmap:Paint) {
        val viewMinSize = builder.getViewMinSize()
        val dstWidth = viewMinSize.toFloat()
        val dstHeight = viewMinSize.toFloat()
        if (mShader == null || rawBitmap != mRawBitmap) {
            mRawBitmap = rawBitmap
            mShader = BitmapShader(rawBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        if (mShader != null) {
            mMatrix.setScale(dstWidth / rawBitmap.width, dstHeight / rawBitmap.height)
            mShader?.setLocalMatrix(mMatrix)
        }
        paintBitmap.shader = mShader
        val radius = builder.getViewMinSize()/2F - builder.frameSize
        canvas?.drawCircle(width/2F, height/2F, radius, paintBitmap)
    }

    override fun drawFrame(canvas: Canvas?, paint: Paint) {
        var radius = builder.getViewMinSize()/2F
        radius -= paint.strokeWidth / 2
        canvas?.drawCircle(width/2F, height/2F, radius, paint)
    }
}