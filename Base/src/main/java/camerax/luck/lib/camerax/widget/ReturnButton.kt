package camerax.luck.lib.camerax.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：ReturnButton
 */
class ReturnButton(context: Context?,private val size: Int) : View(context) {
    private val centerX by lazy { size / 2 }
    private val centerY by lazy { size / 2 }
    private val strokeWidth by lazy { size / 15f }
    private val paint by lazy { Paint() }
    private val path by lazy { Path() }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) =
        setMeasuredDimension(size, size / 2)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        path.moveTo(strokeWidth, strokeWidth / 2)
        path.lineTo(centerX.toFloat(), centerY - strokeWidth / 2)
        path.lineTo(size - strokeWidth, strokeWidth / 2)
        canvas.drawPath(path, paint)
    }
}