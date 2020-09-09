package com.jianbian.baselib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.jianbian.baselib.R
import com.jianbian.baselib.mvp.impl.PickerListener
import com.jianbian.baselib.utils.AppUtil
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.pow


/**
 * 滚动选择器
 */
abstract class PickerScrollView <T>: View {
    var listener: PickerListener<T>? = null
    private var mDataList: MutableList<T> = ArrayList<T>()
    private val mMaxTextAlpha = 255f
    private val mMinTextAlpha = 120f
    private var mMaxTextSize = 20F
    private var mMinTextSize = 10f
    private var mViewHeight = 0
    private var mViewWidth = 0
    private var isInit = false

    private var selectedTextColor = 0
    private var selectedNotTextColor = 0
    private var loop:Boolean = false//是否循环
    private var speed = 2f
    private var spacing = 3.8f

    /**
     * 滑动的距离
     */
    private var mCurrentSelected = 0
    private var downSelected = 0
    private var mLastDownY = 0f
    private var mMoveLen = 0f
    private var updateHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            mMoveLen = 0F
            if (0 <=mCurrentSelected &&mCurrentSelected < mDataList.size && downSelected != mCurrentSelected){
                listener?.onSelect(this@PickerScrollView,mDataList[mCurrentSelected])
            }
            invalidate()
        }
    }
    constructor(context: Context) : super(context) {
        initView(null)
    }
    constructor(context: Context,attributeSet: AttributeSet?) : super(context,attributeSet) {
        initView(attributeSet)
    }
    constructor(context: Context,attributeSet: AttributeSet?, defStyleAttr: Int) : super(context,attributeSet,defStyleAttr) {
        initView(attributeSet)
    }

    private fun initView(attrs: AttributeSet?){
        if (attrs == null)return
        val array = context.obtainStyledAttributes(attrs, R.styleable.PickerScrollView)
        selectedTextColor = array.getColor(R.styleable.PickerScrollView_aj_pick_select_color,AppUtil.getColor(context,R.color.gray_3333))
        selectedNotTextColor = array.getColor(R.styleable.PickerScrollView_aj_pick_select_not_color,AppUtil.getColor(context,R.color.gray_3333))
        loop = array.getBoolean(R.styleable.PickerScrollView_aj_pick_loop,false)
        speed = array.getFloat(R.styleable.PickerScrollView_aj_pick_speed,2F)
        spacing = array.getFloat(R.styleable.PickerScrollView_aj_pick_spacing,3.8F)
        mMaxTextSize = array.getDimensionPixelOffset(R.styleable.PickerScrollView_aj_pick_max_textSize,AppUtil.dp2px(context,20F)).toFloat()
        mMinTextSize = array.getDimensionPixelOffset(R.styleable.PickerScrollView_aj_pick_min_textSize,AppUtil.dp2px(context,10F)).toFloat()
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
    fun setSelected(selected: Int) {
        mCurrentSelected = selected
        invalidate()
    }

    /**
     * 选择选中的内容
     *
     * @param mSelectItem
     */
    fun setSelected(mSelectItem: String) {
        for (index in mDataList.indices) {
            if (drawText(mDataList[index],false) == mSelectItem){
                setSelected(index)
                break
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mViewHeight = measuredHeight
        mViewWidth = measuredWidth
        isInit = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInit){
            if (0 <=mCurrentSelected &&mCurrentSelected < mDataList.size){
                drawText(mCurrentSelected,canvas)
            }
            for (index in 1..mCurrentSelected){
                drawOtherText(canvas, index, -1)
            }
            for (index in 1 until mDataList.size - mCurrentSelected){
                drawOtherText(canvas, index, 1)
            }
        }
    }

    private fun  drawText(index:Int,canvas: Canvas){
        var mPaint = getPaint(selectedTextColor)
        mPaint.textSize = mMaxTextSize
        mPaint.alpha = mMaxTextAlpha.toInt()
        val textData: String = drawText(mDataList[index],true)
        val fmi: Paint.FontMetricsInt = mPaint.fontMetricsInt
        val baseline = ((mViewHeight+mMoveLen - fmi.bottom - fmi.top)/2.0).toFloat()
        canvas.drawText(textData, (mViewWidth / 2.0).toFloat(),baseline, mPaint)
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private fun drawOtherText(canvas: Canvas, position: Int, type: Int) {
        var mPaint = getPaint(selectedNotTextColor)
        val d = (spacing * mMinTextSize * position + type * mMoveLen)
        val scale = parabola(mViewHeight / 4.0f, d)
        val size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize
        mPaint.textSize = size
        mPaint.alpha = ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha).toInt()
        val y = (mViewHeight / 2.0 + type * d).toFloat()
        val indexs = mCurrentSelected + type * position
        val textData: String = drawText(mDataList[indexs],false)
        val fmi: Paint.FontMetricsInt = mPaint.fontMetricsInt
        val baseline = ((y - (fmi.bottom + fmi.top)/2.0)).toFloat()
        canvas.drawText(textData, (mViewWidth / 2.0).toFloat(),baseline, mPaint)
    }

    private fun getPaint(mColorText: Int):Paint{
        var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.style = Paint.Style.FILL
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.color = mColorText
        return mPaint
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private fun parabola(zero: Float, x: Float): Float {
        val f = (1 - (x / zero.toDouble()).pow(2.0)).toFloat()
        return if (f < 0) 0F else f
    }

    /*----------------------移动---------------------------------------*/
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downSelected = mCurrentSelected
                updateHandler.removeCallbacksAndMessages(null)
                mLastDownY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                mMoveLen += event.y - mLastDownY
                actionMove()
                mLastDownY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP ,MotionEvent.ACTION_CANCEL-> {
                updateHandler.removeCallbacksAndMessages(null)
                updateHandler.sendEmptyMessageDelayed(1,10)
            }
        }
        return true
    }

    private fun actionMove(){
        if (mMoveLen > spacing * mMinTextSize / 2) {
            mMoveLen -= spacing * mMinTextSize
            mCurrentSelected--
        } else if (mMoveLen < -spacing * mMinTextSize / 2) {
            mMoveLen += spacing * mMinTextSize
            mCurrentSelected++
        }
        if (mCurrentSelected < 0) {
            mCurrentSelected = 0
            mMoveLen = -mMoveLen
        } else if (mCurrentSelected >= mDataList.size) {
            mCurrentSelected = mDataList.size - 1
            mMoveLen = -mMoveLen
        }
    }

    abstract fun drawText(data:T,selected: Boolean) : String

    fun getData():MutableList<T>{
        return mDataList
    }

    fun getSelectItem():T?{
        if (0 <=mCurrentSelected &&mCurrentSelected < mDataList.size){
            return mDataList[mCurrentSelected]
        }
        return null
    }
}
