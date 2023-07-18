package camerax.luck.lib.camerax.widget

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
class TypeButton(context: Context?, private val buttonType: TYPE, private val buttonSize: Int) : View(context) {
    private val centerX by lazy { buttonSize/2.0F }
    private val centerY by lazy { buttonSize/2.0F }
    private val buttonRadius by lazy { buttonSize/2.0F }
    
    private val mPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val path by lazy { Path() }
    
    private val strokeWidth by lazy { buttonSize / 50f }
    private val index by lazy { buttonSize / 12f }
    private val rectF by lazy { RectF(centerX, centerY - index, centerX + index * 2, centerY + index) }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(buttonSize, buttonSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //如果类型为取消，则绘制内部为返回箭头
        if (buttonType == TYPE.TYPE_CANCEL) {
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
        //如果类型为确认，则绘制绿色勾
        if (buttonType == TYPE.TYPE_CONFIRM) {
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

    enum class TYPE{
        TYPE_CANCEL,
        TYPE_CONFIRM,
    }
}