package com.xy.baselib.widget.photoview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector

/**
 * Created by liuheng on 2015/6/21.
 *
 *
 * 如有任何意见和建议可邮件  bmme@vip.qq.com
 */
open class PhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    PhotoBaseView(context, attrs, defStyleAttr) {
    var mSynthesisMatrix = Matrix()
    var mTmpMatrix = Matrix()
    var hasOverTranslate = false
    var imgLargeWidth = false
    var imgLargeHeight = false
    var mTmpRect = RectF()
    var mClip :RectF ?= null
    val mTranslate: TransformRunnable by lazy { TransformRunnable(this) }
    val mGestureListener: GestureController by lazy { GestureController(this) }
    val rotateDetector: RotateDelegate by lazy { RotateDelegate(this) }
    val mDetector: GestureDetector by lazy { GestureDetector(context, mGestureListener) }
    val mScaleDetector: ScaleGestureDetector by lazy { ScaleGestureDetector(context, scaleDelegate) }
    val scaleDelegate: ScaleDelegate by lazy { ScaleDelegate(this) }
    var isUserScroll2 = false
        set(value) {
            field = value
            mGestureListener.isUserScroll2 = isUserScroll2
        }

    init {
        super.setScaleType(ScaleType.MATRIX)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        mGestureListener.mClickListener = l
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        super.setOnLongClickListener(l)
        mGestureListener.mLongClick = l
    }


    /**
     * 重新设置
     */
    open fun reset() {
        mAnimaMatrix.reset()
        executeTranslate()
        scaleDelegate.mScale = 1F
        mGestureListener.mTranslateX = 0
        mGestureListener.mTranslateY = 0
    }

    override fun executeTranslate() {
        mSynthesisMatrix.set(mBaseMatrix)
        mSynthesisMatrix.postConcat(mAnimaMatrix)
        imageMatrix = mSynthesisMatrix
        mAnimaMatrix.mapRect(mImgRect, mBaseRect)
        imgLargeWidth = mImgRect.width() > mWidgetRect.width()
        imgLargeHeight = mImgRect.height() > mWidgetRect.height()
    }

    override fun draw(canvas: Canvas) {
        mClip?.run {
            canvas.clipRect(this)
            mClip = null
        }
        super.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnable) {
            val action = event.actionMasked
            mDetector.onTouchEvent(event)
            if (isRotateEnable) {
                rotateDetector.onTouchEvent(event)
            }
            mScaleDetector.onTouchEvent(event)
            if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) && !isUserScroll2) {
                actionUp()
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun actionUp() {
        if (mTranslate.isRuning) return
        rotateDetector.correctRotate()
        scaleDelegate.correctScale()
        val cx = mImgRect.left + mImgRect.width() / 2
        val cy = mImgRect.top + mImgRect.height() / 2
        mScaleCenter[cx] = cy
        mRotateCenter[cx] = cy
        mGestureListener.mTranslateX = 0
        mGestureListener.mTranslateY = 0
        mTmpMatrix.reset()
        mTmpMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top)
        mTmpMatrix.postTranslate(cx - mHalfBaseRectWidth, cy - mHalfBaseRectHeight)
        mTmpMatrix.postScale(scaleDelegate.mScale, scaleDelegate.mScale, cx, cy)
        mTmpMatrix.postRotate(rotateDetector.mDegrees, cx, cy)
        mTmpMatrix.mapRect(mTmpRect, mBaseRect)
        doTranslateReset(mTmpRect)
        mTranslate.start()
    }

    fun doTranslateReset(imgRect: RectF) {
        var tx = 0
        var ty = 0
        if (imgRect.width() <= mWidgetRect.width()) {
            if (!isImageCenterWidth(imgRect)) tx =
                (-((mWidgetRect.width() - imgRect.width()) / 2 - imgRect.left)).toInt()
        } else {
            if (imgRect.left > mWidgetRect.left) {
                tx = (imgRect.left - mWidgetRect.left).toInt()
            } else if (imgRect.right < mWidgetRect.right) {
                tx = (imgRect.right - mWidgetRect.right).toInt()
            }
        }
        if (imgRect.height() <= mWidgetRect.height()) {
            if (!isImageCenterHeight(imgRect)) ty =
                (-((mWidgetRect.height() - imgRect.height()) / 2 - imgRect.top)).toInt()
        } else {
            if (imgRect.top > mWidgetRect.top) {
                ty = (imgRect.top - mWidgetRect.top).toInt()
            } else if (imgRect.bottom < mWidgetRect.bottom) {
                ty = (imgRect.bottom - mWidgetRect.bottom).toInt()
            }
        }
        if (tx != 0 || ty != 0) {
            if (!mTranslate.mFlingScroller.isFinished)
                mTranslate.mFlingScroller.abortAnimation()
            mTranslate.withTranslate(-tx, -ty)
        }
    }
}