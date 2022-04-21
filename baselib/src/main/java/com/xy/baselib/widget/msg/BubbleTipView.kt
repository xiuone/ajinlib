package com.xy.baselib.widget.msg

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import com.xy.utils.dp2px

class BubbleTipView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr){
    private var mPaint: Paint =  Paint(Paint.ANTI_ALIAS_FLAG)
    internal var mColor = Color.RED
        set(value) {
            field = value
            invalidate()
        }
    //固定圆半径
    private var mFixCircleRadius = 0f

    //固定圆最大半径
    private var mFixCircleRadiusMax = 7f

    //固定圆最小半径
    private var mFixCircleRadiusMix = 2f

    //拖拽圆半径
    private var mDragCircleRadius = 10f

    //固定圆坐标
    private val mFixPoint = PointF()

    //拖拽圆坐标
    private val mDragPoint = PointF()

    //图片
    internal var mBitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }

    //动画时长
    private val mDuration = 350

    init {
        mDragCircleRadius =  context.dp2px(mDragCircleRadius).toFloat()
        mFixCircleRadiusMax =  context.dp2px(mFixCircleRadiusMax).toFloat()
        mFixCircleRadiusMix =  context.dp2px(mFixCircleRadiusMix).toFloat()
        mFixCircleRadius = mFixCircleRadiusMax
        mPaint.isDither = true
        mPaint.color = mColor
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(mDragPoint.x, mDragPoint.y, mDragCircleRadius, mPaint)
        createBethelPath()?.run {
            //画固定圆
            canvas.drawCircle(mFixPoint.x, mFixPoint.y, mFixCircleRadius, mPaint)
            //3.画贝塞尔曲线
            canvas.drawPath(this, mPaint)
        }
        mBitmap?.run {
            val width = mBitmap?.width?:0
            val height = mBitmap?.height?:0
            canvas.drawBitmap(this, mDragPoint.x - width / 2, mDragPoint.y - height / 2, mPaint)
        }

    }

    /**
     * 创建贝塞尔曲线
     */
    private fun createBethelPath(): Path? {
        val dx = mDragPoint.x - mFixPoint.x
        val dy = mDragPoint.y - mFixPoint.y
        val distance = Math.sqrt((dx * dx + dy * dy).toDouble())
        mFixCircleRadius = (mFixCircleRadiusMax - distance / 20f).toFloat()
        if (mFixCircleRadius < mFixCircleRadiusMix) {
            //半径小于最小值，不用画
            return null
        }
        //对比邻
        val tanA = dy / dx
        //获取A的角度
        val atan = Math.atan(tanA.toDouble())
        //p0的x轴位置=固定圆x轴+固定圆半径*sinA
        val p0x = (mFixPoint.x + mFixCircleRadius * Math.sin(atan)).toFloat()
        //p0的y轴位置=固定圆y轴-固定圆半径*cosA
        val p0y = (mFixPoint.y - mFixCircleRadius * Math.cos(atan)).toFloat()

        //p1的x轴位置=拖拽圆x轴+拖拽圆半径*sinA
        val p1x = (mDragPoint.x + mDragCircleRadius * Math.sin(atan)).toFloat()
        //p1的y轴位置=拖拽圆y轴-拖拽圆半径*cosA
        val p1y = (mDragPoint.y - mDragCircleRadius * Math.cos(atan)).toFloat()

        //p2的x轴位置=拖拽圆x轴-拖拽圆半径*sinA
        val p2x = (mDragPoint.x - mDragCircleRadius * Math.sin(atan)).toFloat()
        //p2的y轴位置=拖拽圆y轴+拖拽圆半径*cosA
        val p2y = (mDragPoint.y + mDragCircleRadius * Math.cos(atan)).toFloat()

        //p3的x轴位置=固定圆x轴-固定圆半径*sinA
        val p3x = (mFixPoint.x - mFixCircleRadius * Math.sin(atan)).toFloat()
        //p3的y轴位置=固定圆y轴+固定圆半径*cosA
        val p3y = (mFixPoint.y + mFixCircleRadius * Math.cos(atan)).toFloat()
        val mBethelPath = Path()
        mBethelPath.moveTo(p0x, p0y)
        //固定点x0,y0，取中心点
        val x0 = (mDragPoint.x + mFixPoint.x) / 2
        val y0 = (mDragPoint.y + mFixPoint.y) / 2
        mBethelPath.quadTo(x0, y0, p1x, p1y)
        mBethelPath.lineTo(p2x, p2y)
        mBethelPath.quadTo(x0, y0, p3x, p3y)
        mBethelPath.close()
        return mBethelPath
    }

    fun initPoint(x: Float, y: Float) {
        mFixPoint.x = x
        mFixPoint.y = y
        mDragPoint.x = x
        mDragPoint.y = y
        invalidate()
    }

    fun updateDragPoint(x: Float, y: Float) {
        mDragPoint.x = x
        mDragPoint.y = y
        invalidate()
    }



    /**
     * 手指抬起
     */
    fun actionUp(listener: OnBubbleTouchListener?) {
        //手指抬起时判断是要回弹还是开启爆炸动画
        if (mFixCircleRadius < mFixCircleRadiusMix) {
            //半径小于最小值，开启爆炸动画
            listener?.dismiss(mDragPoint)
        } else {
            //回弹,开启动画回弹
            springBackAnimation(listener)
        }
    }

    /**
     * 回弹,开启动画回弹
     */
    private fun springBackAnimation(listener: OnBubbleTouchListener?) {
        val valueAnimator = ObjectAnimator.ofFloat(1f, 0f)
        valueAnimator.duration = mDuration.toLong()
        valueAnimator.interpolator = OvershootInterpolator(3f)
        val endPointF = PointF(mDragPoint.x, mDragPoint.y)
        valueAnimator.addUpdateListener { animation -> //不断更新拖拽点坐标
            val percent = animation.animatedValue as Float
            val endX = mFixPoint.x + (endPointF.x - mFixPoint.x) * percent
            val endY = mFixPoint.y + (endPointF.y - mFixPoint.y) * percent
            updateDragPoint(endX, endY)
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                listener?.springBack()
            }
        })
        valueAnimator.start()
    }
}