package com.xy.baselib.widget.shadow.impl

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import com.xy.baselib.widget.shadow.ShadowPaint
import com.xy.baselib.widget.shadow.ShadowPath

class OnDrawImpl(view: View, builderImpl: ShadowBuilderImpl) : OnSizeChangeImpl(view, builderImpl),
    OnDrawListener {
    private val shadowPaint: Paint by lazy { ShadowPaint(builderImpl) }
    private val stokePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    override fun onDraw(canvas: Canvas?) {
        shadowPaint.reset()
        val path = ShadowPath(builderImpl, view)
        path.reset()
        canvas?.drawPath(path, shadowPaint)
    }

    override fun onDrawStoke(canvas: Canvas?) {
        stokePaint.isAntiAlias = true
        stokePaint.style = Paint.Style.STROKE
        stokePaint.strokeWidth = builder.stokeSize
        stokePaint.color = builder.stokeColor


        val path = ShadowPath(builderImpl, view)
        val rectF = path.getShadowRect()
        rectF.left = rectF.left + builder.stokeSize/2
        rectF.right = rectF.right - builder.stokeSize/2
        rectF.top = rectF.top + builder.stokeSize/2
        rectF.bottom = rectF.bottom - builder.stokeSize/2

        val newPath = Path()
        newPath.addRoundRect(rectF,path.getCornerValue(), Path.Direction.CW)

        canvas?.drawPath(newPath,stokePaint)
    }


    /**
     * 直接裁剪view
     * @param canvas
     */
    override fun onClipPathDraw(canvas: Canvas?) {
        val path: Path = ShadowPath(builderImpl, view)
        path.reset()
        canvas?.clipPath(path)
    }
}