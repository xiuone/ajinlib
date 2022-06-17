package com.xy.baselib.widget.indicator

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.viewpager.widget.ViewPager
import com.xy.baselib.R
import com.xy.baselib.exp.getResColor
import com.xy.baselib.exp.getResDimension
import com.xy.baselib.viewpager.AppViewPagerChangeListener
import com.xy.baselib.viewpager.ViewPagerListenerImpl
import kotlin.math.abs

/**
 * 指示器
 */
class IndicatorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, ) :
    View(context, attrs, defStyleAttr), AppViewPagerChangeListener,
    AnimatorUpdateListener {
    //页码数
    private var pageCount = 3

    //选中的位置
    private var selectPosition = 0

    //选中的颜色
    var selectColor = context.getResColor(R.color.blue_078a)
        set(value) {
            field = value
            invalidate()
        }

    //未选中的颜色
    var unSelectColor = context.getResColor(R.color.gray_9999)
        set(value) {
            field = value
            invalidate()
        }

    //选中的宽度
    var selectWidth = context.getResDimension(R.dimen.dp_12)
        set(value) {
            field = value
            currentPoint.x = selectWidth
        }
    //大小
    var size: Int = context.getResDimension(R.dimen.dp_6)
        set(value) {
            field = value
            invalidate()
        }

    //间距
    var space = context.getResDimension(R.dimen.dp_20)
        set(value) {
            field = value
            invalidate()
        }
    private var prefixCount = 0
    private var suffixCount = 0

    //当前位置
    private val currentPoint = Point()

    //目标位置
    private val targetPoint = Point()
    private var valueAnimator: ValueAnimator? = null


    init {
        attrs?.run {
            val ta = getContext().obtainStyledAttributes(attrs, R.styleable.IndicatorView)
            selectColor = ta.getColor(R.styleable.IndicatorView_indicator_selected_color, -0x1)
            unSelectColor = ta.getColor(R.styleable.IndicatorView_indicator_un_select_color, -0x1)
            selectWidth = ta.getDimensionPixelSize(R.styleable.IndicatorView_indicator_select_width, 36)
            size = ta.getDimensionPixelSize(R.styleable.IndicatorView_indicator_size, 18)
            space = ta.getDimensionPixelSize(R.styleable.IndicatorView_indicator_space, 51)
            currentPoint.x = selectWidth
        }
    }

    /**
     * 根据viewpager改变儿便便位置
     * @param viewPager
     */
    fun bindViewPager(viewPager: ViewPager?) {
        ViewPagerListenerImpl(viewPager!!, this)
    }

    fun resetVisibility() {
        setPageCount(pageCount, prefixCount, suffixCount)
    }

    /**
     * 设置页面数量
     * @param pageCount
     */
    fun setPageCount(pageCount: Int, prefixCount: Int, suffixCount: Int) {
        this.prefixCount = prefixCount
        this.suffixCount = suffixCount
        this.pageCount = pageCount
        val params = layoutParams ?: return
        params.height = size
        params.width = space * (pageCount - 1) + selectWidth * 2
        layoutParams = params
        visibility = if (pageCount > 1) VISIBLE else GONE
        invalidate()
    }

    override fun onPageSelected(position: Int) {
        selectPosition = when {
            position < prefixCount -> { abs(pageCount - position - 1) }
            position < pageCount + prefixCount -> { abs(position - prefixCount) }
            else -> { abs(position - pageCount - prefixCount) }
        }
        targetPoint.x = selectPosition * (space - 1) + selectWidth
        valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofInt(currentPoint.x, targetPoint.x)
        valueAnimator?.interpolator = OvershootInterpolator(3F)
        valueAnimator?.duration = 400
        valueAnimator?.removeAllListeners()
        valueAnimator?.addUpdateListener(this)
        valueAnimator?.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == h) return
        targetPoint.y = h / 2
        currentPoint.y = h / 2
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val unSelectPoint = Paint(Paint.ANTI_ALIAS_FLAG)
        unSelectPoint.color = unSelectColor
        for (index in 0 until pageCount) {
            canvas.drawCircle((space * index + selectWidth).toFloat(),
                currentPoint.y.toFloat(),
                (size / 2).toFloat(),
                unSelectPoint)
        }
        val selectPoint = Paint(Paint.ANTI_ALIAS_FLAG)
        selectPoint.color = selectColor
        val rectF = RectF()
        rectF.top = 0f
        rectF.left = (currentPoint.x - selectWidth / 2).toFloat()
        rectF.right = (currentPoint.x + selectWidth / 2).toFloat()
        rectF.bottom = height.toFloat()
        canvas.drawRoundRect(rectF, (size / 2).toFloat(), (size / 2).toFloat(), selectPoint)
    }

    /**
     * 更新动画
     * @param animation
     */
    override fun onAnimationUpdate(animation: ValueAnimator) {
        val x = animation.animatedValue as Int
        currentPoint.x = x
        invalidate()
    }


}