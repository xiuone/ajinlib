package xy.xy.base.widget.photoview

import android.graphics.RectF
import android.view.animation.Interpolator
import android.widget.OverScroller
import android.widget.Scroller
import xy.xy.base.utils.exp.dp2px

class TransformRunnable(private val photoView: PhotoView) : Runnable {
    private val ctx = photoView.context
    var mInterpolatorProxy = InterpolatorProxy()
    var MAX_FLING_OVER_SCROLL = ctx.dp2px(30F)
    private val mAnimaDuring = 340

    var isRuning = false
    var mTranslateScroller: OverScroller = OverScroller(ctx, mInterpolatorProxy)
    var mFlingScroller: OverScroller = OverScroller(ctx, mInterpolatorProxy)
    var mScaleScroller: Scroller = Scroller(ctx, mInterpolatorProxy)
    var mClipScroller: Scroller = Scroller(ctx, mInterpolatorProxy)
    var mRotateScroller: Scroller = Scroller(ctx, mInterpolatorProxy)
    var mLastFlingX = 0
    var mLastFlingY = 0
    var mLastTranslateX = 0
    var mLastTranslateY = 0
    var mClipRect = RectF()

    fun setInterpolator(interpolator: Interpolator) {
        mInterpolatorProxy.mTarget = interpolator
    }

    fun withTranslate(deltaX: Int, deltaY: Int) {
        mLastTranslateX = 0
        mLastTranslateY = 0
        mTranslateScroller.startScroll(0, 0, deltaX, deltaY, mAnimaDuring)
    }

    fun withScale(form: Float, to: Float) {
        mScaleScroller.startScroll((form * 10000).toInt(), 0, ((to - form) * 10000).toInt(), 0, mAnimaDuring)
    }

    fun withRotate(fromDegrees: Int, toDegrees: Int) {
        mRotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, mAnimaDuring)
    }

    fun start() {
        isRuning = true
        postExecute()
    }

    fun stop() {
        photoView.removeCallbacks(this)
        mTranslateScroller.abortAnimation()
        mScaleScroller.abortAnimation()
        mFlingScroller.abortAnimation()
        mRotateScroller.abortAnimation()
        isRuning = false
    }

    override fun run() {
        var endAnima = true
        if (mScaleScroller.computeScrollOffset()) {
            photoView.scaleDelegate.mScale = mScaleScroller.currX / 10000f
            endAnima = false
        }
        if (mTranslateScroller.computeScrollOffset()) {
            val tx = mTranslateScroller.currX - mLastTranslateX
            val ty = mTranslateScroller.currY - mLastTranslateY
            photoView.mGestureListener.mTranslateX += tx
            photoView.mGestureListener.mTranslateY += ty
            mLastTranslateX = mTranslateScroller.currX
            mLastTranslateY = mTranslateScroller.currY
            endAnima = false
        }
        if (mFlingScroller.computeScrollOffset()) {
            val x = mFlingScroller.currX - mLastFlingX
            val y = mFlingScroller.currY - mLastFlingY
            mLastFlingX = mFlingScroller.currX
            mLastFlingY = mFlingScroller.currY
            photoView.mGestureListener.mTranslateX += x
            photoView.mGestureListener.mTranslateY += y
            endAnima = false
        }
        if (mRotateScroller.computeScrollOffset()) {
            photoView.rotateDetector.mDegrees = mRotateScroller.currX.toFloat()
            endAnima = false
        }
        if (mClipScroller.computeScrollOffset() || photoView.mClip != null) {
            val sx = mClipScroller.currX / 10000f
            val sy = mClipScroller.currY / 10000f
            photoView.mTmpMatrix.setScale(sx, sy, (photoView.mImgRect.left + photoView.mImgRect.right) / 2, 0F)
            photoView.mTmpMatrix.mapRect(mClipRect, photoView.mImgRect)
            if (sx == 1f) {
                mClipRect.left = photoView.mWidgetRect.left
                mClipRect.right = photoView.mWidgetRect.right
            }
            if (sy == 1f) {
                mClipRect.top = photoView.mWidgetRect.top
                mClipRect.bottom = photoView.mWidgetRect.bottom
            }
            photoView.mClip = mClipRect
        }
        if (!endAnima) {
            applyAnima()
            postExecute()
        } else {
            isRuning = false

            // 修复动画结束后边距有些空隙，
            var needFix = false
            if (photoView.imgLargeWidth) {
                if (photoView.mImgRect.left > 0) {
                    photoView.mGestureListener.mTranslateX -= (photoView.mImgRect.left).toInt()
                } else if (photoView.mImgRect.right < photoView.mWidgetRect.width()) {
                    photoView.mGestureListener.mTranslateX -= (photoView.mWidgetRect.width() - photoView.mImgRect.right).toInt()
                }
                needFix = true
            }
            if (photoView.imgLargeHeight) {
                if (photoView.mImgRect.top > 0) {
                    photoView.mGestureListener.mTranslateY -= (photoView.mImgRect.top).toInt()
                } else if (photoView.mImgRect.bottom < photoView.mWidgetRect.height()) {
                    photoView.mGestureListener.mTranslateY -= (photoView.mWidgetRect.height() - photoView.mImgRect.bottom).toInt()
                }
                needFix = true
            }
            if (needFix) {
                applyAnima()
            }
            photoView.invalidate()
        }
    }

    private fun applyAnima() {
        photoView.mAnimaMatrix.reset()
        photoView.mAnimaMatrix.postTranslate(-photoView.mBaseRect.left, -photoView.mBaseRect.top)
        photoView.mAnimaMatrix.postTranslate(photoView.mRotateCenter.x, photoView.mRotateCenter.y)
        photoView.mAnimaMatrix.postTranslate(-photoView.mHalfBaseRectWidth, -photoView.mHalfBaseRectHeight)
        photoView.mAnimaMatrix.postRotate(photoView.rotateDetector.mDegrees, photoView.mRotateCenter.x, photoView.mRotateCenter.y)
        photoView.mAnimaMatrix.postScale(photoView.scaleDelegate.mScale, photoView.scaleDelegate.mScale, photoView.mScaleCenter.x, photoView.mScaleCenter.y)
        photoView.mAnimaMatrix.postTranslate(photoView.mGestureListener.mTranslateX.toFloat(), photoView.mGestureListener.mTranslateY.toFloat())
        photoView.executeTranslate()
    }

    private fun postExecute() {
        if (isRuning)
            photoView.post(this)
    }
}