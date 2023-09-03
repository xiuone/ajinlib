package xy.xy.base.widget.shadow.impl

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import xy.xy.base.widget.shadow.ShadowPaint
import xy.xy.base.widget.shadow.ShadowPath

class OnDrawImpl(view: View, builderImpl: ShadowBuilderImpl,private val expDrawListener:OnDrawExpListener) : OnSizeChangeImpl(view, builderImpl),
    OnDrawListener {
    private val shadowPaint: Paint by lazy { ShadowPaint(builderImpl) }
    private val stokePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    override fun onDraw(canvas: Canvas?) {
        if (!expDrawListener.onExpDraw(canvas)) {
            shadowPaint.reset()
            val path = ShadowPath(builderImpl, view)
            path.reset()
            canvas?.drawPath(path, shadowPaint)
        }
    }

    override fun onDrawStoke(canvas: Canvas?) {
        if (!expDrawListener.onExpDrawStoke(canvas)) {
            stokePaint.isAntiAlias = true
            stokePaint.style = Paint.Style.STROKE
            stokePaint.strokeWidth = builder.stokeSize
            stokePaint.color = builder.stokeColor
            val path = ShadowPath(builderImpl, view, (builder.stokeSize / 2).toInt())
            path.reset()
            canvas?.drawPath(path, stokePaint)
        }
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