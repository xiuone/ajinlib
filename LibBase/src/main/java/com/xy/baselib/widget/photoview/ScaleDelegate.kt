package com.xy.baselib.widget.photoview

import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener

class ScaleDelegate(private val photoView: PhotoView) : OnScaleGestureListener {
    var mScale = 1.0f


    var mMaxScale = 2.5f
        set(value) {
            if (value > this.mMaxScale) {
                field = value
            }
        }


    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor
        if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor)) return false
        setScale(scaleFactor, detector.focusX, detector.focusY)
        photoView.executeTranslate()
        return true
    }

    fun setScale(scaleFactor: Float, x: Float, y: Float) {
        mScale *= scaleFactor
        photoView.mAnimaMatrix.postScale(scaleFactor, scaleFactor, x, y)
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean  = true

    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    /**
     * 纠正缩放
     */
    fun correctScale() {
        var scale = mScale
        if (mScale < 0.5) {
            scale = 0.5f
        } else if (mScale > mMaxScale) {
            scale = mMaxScale
        }
        photoView.mTranslate.withScale(mScale, scale)
        mScale = scale
    }
}