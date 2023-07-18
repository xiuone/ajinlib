package camerax.luck.lib.camerax.listener

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener

/**
 * @author：luck
 * @date：2022/2/16 9:41 上午
 * @describe：CameraXPreviewViewTouchListener
 */
class PreviewViewTouchListener(context: Context, val mCustomTouchListener: CustomTouchListener?) : OnTouchListener {

    private val mGestureDetector by lazy { GestureDetector(context, object : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {}
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            mCustomTouchListener?.click(e.x, e.y)
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            mCustomTouchListener?.doubleClick(e.x, e.y)
            return true
        }
    }) }

    private val mScaleGestureDetector by lazy { ScaleGestureDetector(context, object : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val delta = detector.scaleFactor
            mCustomTouchListener?.zoom(delta)
            return true
        }
    }) }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        mScaleGestureDetector.onTouchEvent(event)
        if (!mScaleGestureDetector.isInProgress) {
            mGestureDetector.onTouchEvent(event)
        }
        return true
    }


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
}