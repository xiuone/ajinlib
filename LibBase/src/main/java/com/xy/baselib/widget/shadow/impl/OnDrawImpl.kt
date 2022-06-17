package com.xy.baselib.widget.shadow.impl

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import com.xy.baselib.widget.shadow.ShadowPaint
import com.xy.baselib.widget.shadow.ShadowPath

class OnDrawImpl(view: View, builderImpl: ShadowBuilderImpl) : OnSizeChangeImpl(view, builderImpl),
    OnDrawListener {
    private val shadowPaint: Paint by lazy { ShadowPaint(builderImpl) }


    override fun onDraw(canvas: Canvas?) {
        shadowPaint.reset()
        val path = ShadowPath(builderImpl, view)
        path.reset()
        canvas?.drawPath(path, shadowPaint)
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