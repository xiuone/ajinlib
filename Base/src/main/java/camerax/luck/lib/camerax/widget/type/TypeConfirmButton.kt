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
class TypeConfirmButton(context: Context?, buttonSize: Int) : TypeBaseButton(context,buttonSize) {


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.isAntiAlias = true
        mPaint.color = -0x1
        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, buttonRadius, mPaint)
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.color = -0xff3400
        mPaint.strokeWidth = strokeWidth
        path.moveTo(centerX - buttonSize / 6f, centerY)
        path.lineTo(centerX - buttonSize / 21.2f, centerY + buttonSize / 7.7f)
        path.lineTo(centerX + buttonSize / 4.0f, centerY - buttonSize / 8.5f)
        path.lineTo(centerX - buttonSize / 21.2f, centerY + buttonSize / 9.4f)
        path.close()
        canvas.drawPath(path, mPaint)
    }
}