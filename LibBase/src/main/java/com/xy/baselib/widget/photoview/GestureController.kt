package com.xy.baselib.widget.photoview

import android.graphics.RectF
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnLongClickListener
import com.xy.baselib.exp.dp2px

class GestureController(private val photoView: PhotoView) : SimpleOnGestureListener(), Runnable {
    var mClickListener: View.OnClickListener? = null
    var mLongClick: OnLongClickListener? = null
    var mTranslateX = 0
    var mTranslateY = 0
    val MAX_OVER_RESISTANCE  by lazy { photoView.context.dp2px(140F) }
    var mCommonRect = RectF()
    var isUserScroll2 = false



    /**
     * 长按回调
     * @param e
     */
    override fun onLongPress(e: MotionEvent) {
        mLongClick?.onLongClick(photoView)
    }

    /**
     * 殿下回调
     * @param e
     * @return
     */
    override fun onDown(e: MotionEvent): Boolean {
        photoView.hasOverTranslate = false
        photoView.hasMultiTouch = false
        photoView.rotateDetector.canRotate = false
        photoView.removeCallbacks(this)
        return false
    }

    /**
     * 单点的时候
     * @param e
     * @return
     */
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        photoView.postDelayed(this, 250)
        return false
    }

    fun onScroll2(distanceX: Float, distanceY: Float) {
        photoView.mAnimaMatrix.postTranslate(-distanceX, 0f)
        mTranslateX -= distanceX.toInt()
        photoView.mAnimaMatrix.postTranslate(0f, -distanceY)
        mTranslateY -= distanceY.toInt()
        photoView.executeTranslate()
    }

    fun onScroll1(distanceX: Float, distanceY: Float) {
        var distanceX = distanceX
        var distanceY = distanceY
        if (photoView.canScrollHorizontallySelf(distanceX)) {
            if (distanceX < 0 && photoView.mImgRect.left - distanceX > photoView.mWidgetRect.left) distanceX =
                photoView.mImgRect.left
            if (distanceX > 0 && photoView.mImgRect.right - distanceX < photoView.mWidgetRect.right) distanceX =
                photoView.mImgRect.right - photoView.mWidgetRect.right
            photoView.mAnimaMatrix.postTranslate(-distanceX, 0f)
            mTranslateX -= distanceX.toInt()
        } else if (photoView.imgLargeWidth || photoView.hasMultiTouch || photoView.hasOverTranslate) {
            checkRect()
            if (!photoView.hasMultiTouch) {
                if (distanceX < 0 && photoView.mImgRect.left - distanceX > mCommonRect.left) distanceX =
                    resistanceScrollByX(
                        photoView.mImgRect.left - mCommonRect.left, distanceX)
                if (distanceX > 0 && photoView.mImgRect.right - distanceX < mCommonRect.right) distanceX =
                    resistanceScrollByX(
                        photoView.mImgRect.right - mCommonRect.right, distanceX)
            }
            photoView.mAnimaMatrix.postTranslate(-distanceX, 0f)
            mTranslateX -= distanceX.toInt()
            photoView.hasOverTranslate = true
        }
        if (photoView.canScrollVerticallySelf(distanceY)) {
            if (distanceY < 0 && photoView.mImgRect.top - distanceY > photoView.mWidgetRect.top) distanceY =
                photoView.mImgRect.top
            if (distanceY > 0 && photoView.mImgRect.bottom - distanceY < photoView.mWidgetRect.bottom) distanceY =
                photoView.mImgRect.bottom - photoView.mWidgetRect.bottom
            photoView.mAnimaMatrix.postTranslate(0f, -distanceY)
            mTranslateY -= distanceY.toInt()
        } else if (photoView.imgLargeHeight || photoView.hasOverTranslate || photoView.hasMultiTouch) {
            checkRect()
            if (!photoView.hasMultiTouch) {
                if (distanceY < 0 && photoView.mImgRect.top - distanceY > mCommonRect.top) distanceY =
                    resistanceScrollByY(
                        photoView.mImgRect.top - mCommonRect.top, distanceY)
                if (distanceY > 0 && photoView.mImgRect.bottom - distanceY < mCommonRect.bottom) distanceY =
                    resistanceScrollByY(
                        photoView.mImgRect.bottom - mCommonRect.bottom, distanceY)
            }
            photoView.mAnimaMatrix.postTranslate(0f, -distanceY)
            mTranslateY -= distanceY.toInt()
            photoView.hasOverTranslate = true
        }
        photoView.executeTranslate()
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        if (photoView.mTranslate.isRuning) {
            photoView.mTranslate.stop()
        }
        if (isUserScroll2) {
            onScroll2(distanceX, distanceY)
        } else {
            onScroll1(distanceX, distanceY)
        }
        return true
    }

    /**
     * 双击放大缩小
     * @param e
     * @return
     */
    override fun onDoubleTap(e: MotionEvent): Boolean {
        photoView.mTranslate.stop()
        var from = 1f
        var to = 1f
        val imgCenterX = photoView.mImgRect.left + photoView.mImgRect.width() / 2
        val imgCenterY = photoView.mImgRect.top + photoView.mImgRect.height() / 2
        photoView.mScaleCenter[imgCenterX] = imgCenterY
        photoView.mRotateCenter[imgCenterX] = imgCenterY
        mTranslateX = 0
        mTranslateY = 0
        from = photoView.scaleDelegate.mScale
        if (photoView.scaleDelegate.mScale != 1f) {
            to = 1f
        } else {
            to = photoView.scaleDelegate.mMaxScale
            photoView.mScaleCenter[e.x] = e.y
        }
        photoView.mTmpMatrix.reset()
        photoView.mTmpMatrix.postTranslate(-photoView.mBaseRect.left, -photoView.mBaseRect.top)
        photoView.mTmpMatrix.postTranslate(photoView.mRotateCenter.x, photoView.mRotateCenter.y)
        photoView.mTmpMatrix.postTranslate(-photoView.mHalfBaseRectWidth,
            -photoView.mHalfBaseRectHeight)
        photoView.mTmpMatrix.postRotate(photoView.rotateDetector.mDegrees,
            photoView.mRotateCenter.x,
            photoView.mRotateCenter.y)
        photoView.mTmpMatrix.postScale(to, to, photoView.mScaleCenter.x, photoView.mScaleCenter.y)
        photoView.mTmpMatrix.postTranslate(mTranslateX.toFloat(), mTranslateY.toFloat())
        photoView.mTmpMatrix.mapRect(photoView.mTmpRect, photoView.mBaseRect)
        photoView.doTranslateReset(photoView.mTmpRect)
        photoView.mTranslate.withScale(from, to)
        photoView.mTranslate.start()
        return false
    }

    /**
     * 重新计算x的滚动距离
     * @param overScroll
     * @param detalX
     * @return
     */
    fun resistanceScrollByX(overScroll: Float, detalX: Float): Float {
        return detalX * (Math.abs(Math.abs(overScroll) - MAX_OVER_RESISTANCE) / MAX_OVER_RESISTANCE.toFloat())
    }

    /**
     * 重新计算Y的滚动距离
     * @param overScroll
     * @return
     */
    fun resistanceScrollByY(overScroll: Float, detalY: Float): Float {
        return detalY * (Math.abs(Math.abs(overScroll) - MAX_OVER_RESISTANCE) / MAX_OVER_RESISTANCE.toFloat())
    }

    fun checkRect() {
        if (!photoView.hasOverTranslate) {
            mapRect(photoView.mWidgetRect, photoView.mImgRect, mCommonRect)
        }
    }

    /**
     * 匹配两个Rect的共同部分输出到out，若无共同部分则输出0，0，0，0
     */
    private fun mapRect(r1: RectF, r2: RectF, out: RectF) {
        val l: Float = if (r1.left > r2.left) r1.left else r2.left
        val r: Float = if (r1.right < r2.right) r1.right else r2.right
        if (l > r) {
            out[0f, 0f, 0f] = 0f
            return
        }
        val t: Float = if (r1.top > r2.top) r1.top else r2.top
        val b: Float = if (r1.bottom < r2.bottom) r1.bottom else r2.bottom
        if (t > b) {
            out[0f, 0f, 0f] = 0f
            return
        }
        out[l, t, r] = b
    }

    override fun run() {
        mClickListener?.onClick(photoView)
    }
}