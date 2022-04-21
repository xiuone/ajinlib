package com.xy.baselib.widget.msg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.xy.baselib.R
import com.xy.utils.getResColor
import kotlin.math.min

/** 用于需要圆角矩形框背景的TextView的情况,减少直接使用TextView时引入的shape资源文件  */
class MsgView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatTextView(context, attrs, defStyleAttr) {
    internal var msgBackgroundColor = 0
        set(value) {
            field = value
            invalidate()
        }
    internal var cornerRadius = -1
        set(value) {
            field = value
            invalidate()
        }
    internal var strokeWidth = 0
        set(value) {
            field = value
            invalidate()
        }
    internal var strokeColor = 0
        set(value) {
            field = value
            invalidate()
        }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MsgView)
        msgBackgroundColor = ta.getColor(R.styleable.MsgView_mv_backgroundColor, Color.TRANSPARENT)
        cornerRadius = ta.getDimensionPixelSize(R.styleable.MsgView_mv_cornerRadius, -1)
        strokeWidth = ta.getDimensionPixelSize(R.styleable.MsgView_mv_strokeWidth, 0)
        strokeColor = ta.getColor(R.styleable.MsgView_mv_strokeColor, Color.TRANSPARENT)
        setBackgroundColor(context.getResColor(R.color.transparent))
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        var radius = min(width,height)/2
        radius = if (cornerRadius <= 0) radius else min(cornerRadius,radius)
        drawBackground(canvas,radius.toFloat())
        drawStroke(canvas,radius.toFloat())
        super.onDraw(canvas)
    }

    /**
     * 绘制背景
     */
    private fun drawBackground(canvas: Canvas?,radius:Float){
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = msgBackgroundColor
        paint.style = Paint.Style.FILL
        val rectF = RectF(0F,0F,width.toFloat(),height.toFloat())
        canvas?.drawRoundRect(rectF,radius,radius,paint)
    }

    /**
     * 绘制线
     */
    private fun drawStroke(canvas: Canvas?,radius: Float){
        if (strokeWidth<=0)return
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = strokeColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth.toFloat()
        val rectF = RectF(0F,0F,width.toFloat(),height.toFloat())
        rectF.left = rectF.left + strokeWidth/2
        rectF.top = rectF.left + strokeWidth/2
        rectF.right = rectF.left - strokeWidth/2
        rectF.bottom = rectF.left - strokeWidth/2
        canvas?.drawRoundRect(rectF,radius,radius,paint)
    }

    fun setBubbleRemove(isBubble: Boolean,onDisappearListener: OnDisappearListener?=null){
        if (isBubble) {
            val bubbleController = OnBubbleTouchController(this, onDisappearListener)
            setOnTouchListener(bubbleController)
            bubbleController.mBethelView?.mColor = msgBackgroundColor
        }else
            setOnTouchListener(null)
    }
}
