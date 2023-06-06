package com.xy.base.widget.bar.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.xy.base.utils.exp.getResDimension
import com.xy.base.R

open class ProgressBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ProgressBarBaseView(context, attrs, defStyleAttr) {
    private var thumbHeight :Int =  -1
    private var thumbWidth : Int = -1
    private var thumbRadius : Float = 0F

    private var minHeight = -1
            get() {
                return if (field <=0 ) (height/2) else field
            }
    private var maxHeight = -1
            get() {
                return if (field <=0 ) (height/2) else field
            }

    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView)
            thumbHeight = typedArray.getDimensionPixelOffset(R.styleable.ProgressView_progress_thumb_height, context.getResDimension(R.dimen.dp_10))
            thumbWidth = typedArray.getDimensionPixelOffset(R.styleable.ProgressView_progress_thumb_width, thumbWidth)
            thumbRadius = typedArray.getDimension(R.styleable.ProgressView_progress_thumb_radius, thumbRadius)

            maxHeight = typedArray.getDimensionPixelOffset(R.styleable.ProgressView_progress_max_height, -1)
            minHeight = typedArray.getDimensionPixelOffset(R.styleable.ProgressView_progress_min_height, -1)
        }
    }

    override fun startLeft(): Float  = thumbWidth/2F+thumbShadowRadius*2
    override fun startTop(): Float  = (height-getProgressHeight())/2
    override fun startBottom(): Float = startTop() + getProgressHeight();

    /**
     * 获取当前进度条的高度
     */
    private fun getProgressHeight():Float = minHeight+(maxHeight-minHeight)*animHelper.animValue

    override fun drawThumb(canvas: Canvas, thumbX: Float, thumbPaint: Paint) {
        if (animHelper.animValue > 0) {
            val left = thumbX - startLeft() + thumbShadowRadius*2
            val right = thumbX + startLeft() - thumbShadowRadius*2

            var currentThumbHeight = if (thumbHeight <= 0 || thumbHeight > height) height else thumbHeight;
            currentThumbHeight = (currentThumbHeight*animHelper.animValue).toInt()
            val top = (height - currentThumbHeight) / 2F
            val bottom = top + currentThumbHeight
            val rectF = RectF(left, top, right, bottom)
            canvas.drawRoundRect(rectF, thumbRadius, thumbRadius, thumbPaint)
        }
    }
}