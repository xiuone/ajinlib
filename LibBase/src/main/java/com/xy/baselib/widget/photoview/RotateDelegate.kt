package com.xy.baselib.widget.photoview

import android.view.MotionEvent
import kotlin.math.abs

class RotateDelegate(private val photoView: PhotoView) {
    //是否能旋转当前角度
    var canRotate = false

    //选中角度
    var mDegrees = 0f
    private var mRotateFlag = 0f
    private val mMinRotate = 35
    private var mPrevSlope = 0f
    private var mCurrSlope = 0f
    private val MAX_DEGREES_STEP = 120
    private var x1 = 0f
    private var y1 = 0f
    private var x2 = 0f
    private var y2 = 0f

    fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP ->{
                if (event.pointerCount == 2)
                    mPrevSlope = caculateSlope(event)
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount > 1) {
                    mCurrSlope = caculateSlope(event)
                    val currDegrees = Math.toDegrees(Math.atan(mCurrSlope.toDouble()))
                    val prevDegrees = Math.toDegrees(Math.atan(mPrevSlope.toDouble()))
                    val deltaSlope = currDegrees - prevDegrees
                    if (abs(deltaSlope) <= MAX_DEGREES_STEP) {
                        onRotate(deltaSlope.toFloat(), (x2 + x1) / 2, (y2 + y1) / 2)
                    }
                    mPrevSlope = mCurrSlope
                }
            }
            else -> {}
        }
    }

    private fun caculateSlope(event: MotionEvent): Float {
        x1 = event.getX(0)
        y1 = event.getY(0)
        x2 = event.getX(1)
        y2 = event.getY(1)
        return (y2 - y1) / (x2 - x1)
    }

    /**
     * 指定中心点旋转
     * @param degrees
     * @param focusX
     * @param focusY
     */
    fun onRotate(degrees: Float, focusX: Float, focusY: Float) {
        mRotateFlag += degrees
        if (canRotate) {
            mDegrees += degrees
            photoView.mAnimaMatrix.postRotate(degrees, focusX, focusY)
        } else {
            if (Math.abs(mRotateFlag) >= mMinRotate) {
                canRotate = true
                mRotateFlag = 0f
            }
        }
    }

    /**
     * 直接旋转
     * @param degrees
     */
    fun rotate(degrees: Float) {
        mDegrees += degrees
        val centerX = (photoView.mWidgetRect.left + photoView.mWidgetRect.width() / 2).toInt()
        val centerY = (photoView.mWidgetRect.top + photoView.mWidgetRect.height() / 2).toInt()
        photoView.mAnimaMatrix.postRotate(degrees, centerX.toFloat(), centerY.toFloat())
        photoView.executeTranslate()
    }

    /**
     * 纠正旋转
     */
    fun correctRotate() {
        if (canRotate || mDegrees % 90 != 0f) {
            var toDegrees = ((mDegrees / 90).toInt() * 90).toFloat()
            val remainder = mDegrees % 90
            if (remainder > 45) toDegrees += 90f else if (remainder < -45) toDegrees -= 90f
            photoView.mTranslate.withRotate(mDegrees.toInt(), toDegrees.toInt())
            mDegrees = toDegrees
        }
    }
}