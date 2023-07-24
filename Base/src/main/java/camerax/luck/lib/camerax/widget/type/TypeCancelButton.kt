package camerax.luck.lib.camerax.widget.type

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：TypeButton
 */
class TypeCancelButton(context: Context?, buttonSize: Int) : TypeBaseButton(context,buttonSize) {
    private val index by lazy { buttonSize / 12f }
    private val rectF by lazy { RectF(centerX, centerY - index, centerX + index * 2, centerY + index) }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.isAntiAlias = true
        mPaint.color = -0x11232324
        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, buttonRadius, mPaint)
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = strokeWidth
        path.moveTo(centerX - index / 7, centerY + index)
        path.lineTo(centerX + index, centerY + index)
        path.arcTo(rectF, 90f, -180f)
        path.lineTo(centerX - index, centerY - index)
        canvas.drawPath(path, mPaint)
        mPaint.style = Paint.Style.FILL
        path.reset()
        path.moveTo(centerX - index, (centerY - index * 1.5).toFloat())
        path.lineTo(centerX - index, (centerY - index / 2.3).toFloat())
        path.lineTo((centerX - index * 1.6).toFloat(), centerY - index)
        path.close()
        canvas.drawPath(path, mPaint)
    }
}