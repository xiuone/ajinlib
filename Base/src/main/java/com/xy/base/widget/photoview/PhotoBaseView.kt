package com.xy.base.widget.photoview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.roundToInt

open class PhotoBaseView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, ) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    //是否知道大小
    private var isKnowSize = false

    //是否有drawable  没有drawable你玩什么
    var hasDrawable = false

    //多点触碰
    var hasMultiTouch = false

    //view的中心点
    var mScreenCenter = PointF()

    //缩放的中心点
    var mScaleCenter = PointF()

    //旋转的中心点
    var mRotateCenter = PointF()

    //原本的位置
    var mBaseRect = RectF()

    //容器的大小
    var mWidgetRect = RectF()

    //img的当前位置
    var mImgRect = RectF()

    //Drawable一半宽度
    var mHalfBaseRectWidth = 0f

    //Drawable一半高度
    var mHalfBaseRectHeight = 0f

    //最开始的位置
    var mBaseMatrix = Matrix()

    //炫耀执行的动画的时候
    var mAnimaMatrix = Matrix()

    //是否缩放
    var isEnable = true

    //是否选装
    var isRotateEnable = false

    fun initData() {
        if (!hasDrawable) return
        if (!isKnowSize) return
        mBaseMatrix.reset()
        mAnimaMatrix.reset()
        val img = drawable
        val width = width
        val height = height
        setWidgetRect(0f, 0f, width.toFloat(), height.toFloat())
        mScreenCenter[(width / 2).toFloat()] = (height / 2).toFloat()
        val imgWidth = getDrawableWidth(img)
        val imgHeight = getDrawableHeight(img)
        mBaseRect[0f, 0f, imgWidth.toFloat()] = imgHeight.toFloat()
        // 以图片中心点居中位移
        val tx = (width - imgWidth) / 2
        val ty = (height - imgHeight) / 2
        var sx = 1f
        var sy = 1f

        // 缩放，默认不超过屏幕大小
        if (imgWidth > width) {
            sx = width.toFloat() / imgWidth
        }
        if (imgHeight > height) {
            sy = height.toFloat() / imgHeight
        }
        val scale = if (sx < sy) sx else sy
        mBaseMatrix.reset()
        mAnimaMatrix.reset()
        initShow(tx, ty, scale)
        executeTranslate()
    }

    /**
     * 初始化显示的位置
     * @param translateX
     * @param translateY
     * @param scale
     */
    open fun initShow(translateX: Int, translateY: Int, scale: Float) {
        mBaseMatrix.postTranslate(translateX.toFloat(), translateY.toFloat())
        mBaseMatrix.postScale(scale, scale, mScreenCenter.x, mScreenCenter.y)
        mBaseMatrix.mapRect(mBaseRect)
        mHalfBaseRectWidth = mBaseRect.width() / 2
        mHalfBaseRectHeight = mBaseRect.height() / 2
        mScaleCenter.set(mScreenCenter)
        mRotateCenter.set(mScaleCenter)
    }

    /**
     * 设置容器位置
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    fun setWidgetRect(left: Float, top: Float, right: Float, bottom: Float) {
        mWidgetRect[left, top, right] = bottom
    }

    /**
     * 开始旋转平移等操作
     */
    open fun executeTranslate() {}

    /**
     * 获取drawable宽度
     * @param d
     * @return
     */
    fun getDrawableWidth(d: Drawable): Int {
        var width = d.intrinsicWidth
        if (width <= 0) width = d.minimumWidth
        if (width <= 0) width = d.bounds.width()
        return width
    }

    /**
     * 获取drawable高度
     * @param d
     * @return
     */
    fun getDrawableHeight(d: Drawable): Int {
        var height = d.intrinsicHeight
        if (height <= 0) height = d.minimumHeight
        if (height <= 0) height = d.bounds.height()
        return height
    }

    /**
     * 是否是正中间   横向
     * @param rect
     * @return
     */
    fun isImageCenterWidth(rect: RectF): Boolean = abs(rect.left.roundToInt() - (mWidgetRect.width() - rect.width()) / 2) < 1

    /**
     * 是否是正中间   竖直
     * @param rect
     * @return
     */
    fun isImageCenterHeight(rect: RectF): Boolean = abs(rect.top.roundToInt() - (mWidgetRect.height() - rect.height()) / 2) < 1


    override fun setImageResource(resId: Int) {
        var drawable: Drawable? = null
        try {
            drawable = resources.getDrawable(resId)
        } catch (e: Exception) {
        }
        setImageDrawable(drawable)
    }

    override fun setImageBitmap(bm: Bitmap) {
        setImageDrawable(BitmapDrawable(bm))
        super.setImageBitmap(bm)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (drawable == null) {
            hasDrawable = false
            return
        }
        if ((drawable.intrinsicHeight <= 0 || drawable.intrinsicWidth <= 0)
            && (drawable.minimumWidth <= 0 || drawable.minimumHeight <= 0)
            && (drawable.bounds.width() <= 0 || drawable.bounds.height() <= 0)
        ) {
            return
        }
        if (!hasDrawable) {
            hasDrawable = true
        }
        initData()
    }

    override fun onSizeChanged(w: Int, h: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(w, h, oldWidth, oldHeight)
        if (!isKnowSize) {
            isKnowSize = true
            initData()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount >= 2) {
            hasMultiTouch = true
        }
        return super.onTouchEvent(event)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return if (hasMultiTouch) true else canScrollHorizontallySelf(direction.toFloat())
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return if (hasMultiTouch) true else canScrollVerticallySelf(direction.toFloat())
    }

    /**
     * 能自动滚动 横向
     * @param direction
     * @return
     */
    fun canScrollHorizontallySelf(direction: Float): Boolean {
        if (mImgRect.width() <= mWidgetRect.width()) return false
        if (direction < 0 && Math.round(mImgRect.left) - direction >= mWidgetRect.left) return false
        return !(direction > 0 && Math.round(mImgRect.right) - direction <= mWidgetRect.right)
    }

    /**
     * 能自动滚动   竖直
     * @param direction
     * @return
     */
    fun canScrollVerticallySelf(direction: Float): Boolean {
        if (mImgRect.height() <= mWidgetRect.height()) return false
        if (direction < 0 && Math.round(mImgRect.top) - direction >= mWidgetRect.top) return false
        return !(direction > 0 && Math.round(mImgRect.bottom) - direction <= mWidgetRect.bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!hasDrawable) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        val d = drawable
        val drawableW = getDrawableWidth(d)
        val drawableH = getDrawableHeight(d)
        val pWidth = MeasureSpec.getSize(widthMeasureSpec)
        val pHeight = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var width = 0
        var height = 0
        var p = layoutParams
        if (p == null) {
            p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        width = if (p.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            if (widthMode == MeasureSpec.UNSPECIFIED) {
                drawableW
            } else {
                pWidth
            }
        } else {
            if (widthMode == MeasureSpec.EXACTLY) {
                pWidth
            } else if (widthMode == MeasureSpec.AT_MOST) {
                if (drawableW > pWidth) pWidth else drawableW
            } else {
                drawableW
            }
        }
        height = if (p.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            if (heightMode == MeasureSpec.UNSPECIFIED) {
                drawableH
            } else {
                pHeight
            }
        } else {
            if (heightMode == MeasureSpec.EXACTLY) {
                pHeight
            } else if (heightMode == MeasureSpec.AT_MOST) {
                if (drawableH > pHeight) pHeight else drawableH
            } else {
                drawableH
            }
        }
        setMeasuredDimension(width, height)
    }
}