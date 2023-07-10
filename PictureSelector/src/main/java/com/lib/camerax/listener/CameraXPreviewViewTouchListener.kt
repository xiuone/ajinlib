package com.lib.camerax.listener

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.lib.camerax.listener.CameraXOrientationEventListener.OnOrientationChangedListener
import com.lib.camerax.listener.CameraXPreviewViewTouchListener.CustomTouchListener

/**
 * @author：luck
 * @date：2022/2/16 9:41 上午
 * @describe：CameraXPreviewViewTouchListener
 */
class CameraXPreviewViewTouchListener(context: Context?) : View.OnTouchListener {
    private val mGestureDetector: GestureDetector
    private val mScaleGestureDetector: ScaleGestureDetector
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        mScaleGestureDetector.onTouchEvent(event)
        if (!mScaleGestureDetector.isInProgress) {
            mGestureDetector.onTouchEvent(event)
        }
        return true
    }

    /**
     * 缩放监听
     */
    var onScaleGestureListener: ScaleGestureDetector.OnScaleGestureListener =
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val delta = detector.scaleFactor
                if (mCustomTouchListener != null) {
                    mCustomTouchListener!!.zoom(delta)
                }
                return true
            }
        }
    var onGestureListener: GestureDetector.SimpleOnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {}
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (mCustomTouchListener != null) {
                    mCustomTouchListener!!.click(e.x, e.y)
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (mCustomTouchListener != null) {
                    mCustomTouchListener!!.doubleClick(e.x, e.y)
                }
                return true
            }
        }
    private var mCustomTouchListener: CustomTouchListener? = null

    interface CustomTouchListener {
        /**
         * 放大
         */
        fun zoom(delta: Float)

        /**
         * 点击
         */
        fun click(x: Float, y: Float)

        /**
         * 双击
         */
        fun doubleClick(x: Float, y: Float)
    }

    fun setCustomTouchListener(customTouchListener: CustomTouchListener?) {
        mCustomTouchListener = customTouchListener
    }

    init {
        mGestureDetector = GestureDetector(context, onGestureListener)
        mScaleGestureDetector = ScaleGestureDetector(context!!, onScaleGestureListener)
    }
}