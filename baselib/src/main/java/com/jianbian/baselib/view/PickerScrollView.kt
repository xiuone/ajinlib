package com.jianbian.baselib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jianbian.baselib.mvp.impl.PickerListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


/**
 * 滚动选择器
 */
abstract class PickerScrollView <T>(context: Context, attrs: AttributeSet): View(context,attrs) {

    val MARGIN_ALPHA = 2.8f
    val SPEED = 2f

    private var mDataList: MutableList<T> = ArrayList<T>()

    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private var mCurrentSelected = 0
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mMaxTextSize = 20f
    private var mMinTextSize = 10f
    private val mMaxTextAlpha = 255f
    private val mMinTextAlpha = 120f
    private val mColorText = 0x333333
    private var mViewHeight = 0
    private var mViewWidth = 0
    private var mLastDownY = 0f
    /**
     * 滑动的距离
     */
    private var mMoveLen = 0f
    private var isInit = false
    private var mSelectListener: PickerListener<T>? = null
    private var timer: Timer = Timer()
    private var mTask: MyTimerTask? = null
    var updateHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (abs(mMoveLen) < SPEED) {
                mMoveLen = 0f
                if (mTask != null) {
                    mTask?.cancel()
                    mTask = null
                    performSelect()
                }
            } else  // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen -= mMoveLen / Math.abs(mMoveLen) * SPEED
            invalidate()
        }
    }


    init {
        mDataList = ArrayList()
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.color = mColorText
    }

    private fun performSelect() {
        mSelectListener?.onSelect(mDataList[mCurrentSelected])
    }

    fun setData(datas: ArrayList<T>) {
        if (datas == null)
            return
        mDataList.clear()
        mDataList.addAll(datas)
        mCurrentSelected = datas.size / 2
        invalidate()
    }

    /**
     * 选择选中的item的index
     *
     * @param selected
     */
    public fun setSelected(selected: Int) {
        mCurrentSelected = selected
        val distance = mDataList!!.size / 2 - mCurrentSelected
        if (distance < 0) {
            for (i in 0 until -distance) {
                moveHeadToTail()
                mCurrentSelected--
            }
        } else if (distance > 0) {
            for (i in 0 until distance) {
                moveTailToHead()
                mCurrentSelected++
            }
        }
        invalidate()
    }

    /**
     * 选择选中的内容
     *
     * @param mSelectItem
     */
    fun setSelected(mSelectItem: String) {
        for (index in mDataList.indices) {
            if (drawText(mDataList[index]) == mSelectItem){
                setSelected(index)
                break
            }
        }
    }

    /**
     * 数据将最后一个移动到最后一个
     */
    private fun moveHeadToTail() {
        if (!mDataList.isNullOrEmpty()){
            val datasBean:T = mDataList[0]
            mDataList.removeAt(0)
            mDataList.add(datasBean)
        }
    }

    /**
     * 数据讲最后一个移动到第一个
     */
    private fun moveTailToHead() {
        if (!mDataList.isNullOrEmpty()){
            val datasBean:T = mDataList[mDataList.size - 1]
            mDataList.removeAt(mDataList.size - 1)
            mDataList.add(0, datasBean)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mViewHeight = measuredHeight
        mViewWidth = measuredWidth
        mMaxTextSize = mViewHeight / 8.0f
        mMinTextSize = mMaxTextSize / 2f
        isInit = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInit){
            val scale = parabola(mViewHeight / 4.0f, mMoveLen)
            val size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize
            mPaint.textSize = size
            mPaint.alpha = ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha).toInt()
            val x = (mViewWidth / 2.0).toFloat()
            val y = (mViewHeight / 2.0 + mMoveLen).toFloat()
            val fmi: Paint.FontMetricsInt = mPaint.fontMetricsInt
            val baseline = (y - (fmi.bottom / 2.0 + fmi.top / 2.0)) as Float
            val textData: String = drawText(mDataList[mCurrentSelected])
            canvas.drawText(textData, x, baseline, mPaint)

            for (index in 1..mCurrentSelected){
                drawOtherText(canvas, index, -1)
            }
            for (index in 1 until mDataList.size - mCurrentSelected){
                drawOtherText(canvas, index, 1)
            }
        }
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private fun drawOtherText(canvas: Canvas, position: Int, type: Int) {
        val d = (MARGIN_ALPHA * mMinTextSize * position + type * mMoveLen)
        val scale = parabola(mViewHeight / 4.0f, d)
        val size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize
        mPaint.textSize = size
        mPaint.alpha = ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha).toInt()
        val y = (mViewHeight / 2.0 + type * d).toFloat()
        val fmi: Paint.FontMetricsInt = mPaint.fontMetricsInt
        val baseline = (y - (fmi.bottom / 2.0 + fmi.top / 2.0)) as Float
        val indexs = mCurrentSelected + type * position
        val textData: String = drawText(mDataList[indexs])
         canvas.drawText(textData, (mViewWidth / 2.0).toFloat(), baseline, mPaint)
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private fun parabola(zero: Float, x: Float): Float {
        val f = (1 - Math.pow(x / zero.toDouble(), 2.0)).toFloat()
        return if (f < 0) 0F else f
    }

    /*----------------------移动---------------------------------------*/
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> doDown(event)
            MotionEvent.ACTION_MOVE -> doMove(event)
            MotionEvent.ACTION_UP -> doUp(event)
        }
        return true
    }

    private fun doDown(event: MotionEvent) {
        if (mTask != null) {
            mTask?.cancel()
            mTask = null
        }
        mLastDownY = event.y
    }

    private fun doMove(event: MotionEvent) {
        mMoveLen += event.y - mLastDownY
        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
            // 往下滑超过离开距离
            moveTailToHead()
            mMoveLen -= MARGIN_ALPHA * mMinTextSize
        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
            // 往上滑超过离开距离
            moveHeadToTail()
            mMoveLen += MARGIN_ALPHA * mMinTextSize
        }
        mLastDownY = event.y
        invalidate()
    }

    private fun doUp(event: MotionEvent) {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (abs(mMoveLen) < 0.0001) {
            mMoveLen = 0f
            return
        }
        if (mTask != null) {
            mTask?.cancel()
            mTask = null
        }
        mTask = MyTimerTask(updateHandler)
        timer.schedule(mTask, 0, 10)
    }
    /*----------------------移动完成---------------------------------------*/

    internal inner class MyTimerTask(handler: Handler) : TimerTask() {
        var handler: Handler
        override fun run() {
            handler.sendMessage(handler.obtainMessage())
        }

        init {
            this.handler = handler
        }
    }

    abstract fun drawText(data:T) : String
}
