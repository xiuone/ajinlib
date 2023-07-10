package com.luck.picture.lib.magical

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.transition.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.basic.InterpolatorFactory.newInterpolator
import kotlin.jvm.JvmOverloads
import com.luck.picture.lib.config.SelectorConfig
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.DensityUtil

/**
 * @author：luck
 * @date：2021/12/15 11:06 上午
 * @describe：MagicalView
 */
class MagicalView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context!!, attrs, defStyleAttr
) {
    private var mAlpha = 0.0f
    private val animationDuration: Long = 250
    private var mOriginLeft = 0
    private var mOriginTop = 0
    private var mOriginHeight = 0
    private var mOriginWidth = 0
    private var screenWidth = 0
    private var screenHeight = 0
    private val appInScreenHeight: Int
    private var targetImageTop = 0
    private var targetImageWidth = 0
    private var targetImageHeight = 0
    private var targetEndLeft = 0
    private var realWidth = 0
    private var realHeight = 0
    private var isAnimating = false
    private val contentLayout: FrameLayout
    private val backgroundView: View
    private val magicalWrapper: MagicalViewWrapper
    private val isPreviewFullScreenMode: Boolean
    private val selectorConfig: SelectorConfig

    /**
     * setBackgroundColor
     *
     * @param color
     */
    override fun setBackgroundColor(color: Int) {
        backgroundView.setBackgroundColor(color)
    }

    fun startNormal(realWidth: Int, realHeight: Int, showImmediately: Boolean) {
        this.realWidth = realWidth
        this.realHeight = realHeight
        mOriginLeft = 0
        mOriginTop = 0
        mOriginWidth = 0
        mOriginHeight = 0
        visibility = VISIBLE
        setOriginParams()
        showNormalMin(
            targetImageTop.toFloat(),
            targetEndLeft.toFloat(),
            targetImageWidth.toFloat(),
            targetImageHeight.toFloat()
        )
        if (showImmediately) {
            mAlpha = 1f
            backgroundView.alpha = mAlpha
        } else {
            mAlpha = 0f
            backgroundView.alpha = mAlpha
            contentLayout.alpha = 0f
            contentLayout.animate().alpha(1f).setDuration(animationDuration).start()
            backgroundView.animate().alpha(1f).setDuration(animationDuration).start()
        }
        setShowEndParams()
    }

    fun start(showImmediately: Boolean) {
        mAlpha = if (showImmediately) 1f.also { mAlpha = it } else 0f
        backgroundView.alpha = mAlpha
        visibility = VISIBLE
        setOriginParams()
        beginShow(showImmediately)
    }

    fun resetStart() {
        screenSize
        start(true)
    }

    /**
     * getScreenSize
     */
    private val screenSize: Unit
        private get() {
            screenWidth = DensityUtil.getRealScreenWidth(context)
            screenHeight = if (isPreviewFullScreenMode) {
                DensityUtil.getRealScreenHeight(context)
            } else {
                DensityUtil.getScreenHeight(context)
            }
        }

    /**
     * changeRealScreenHeight
     *
     * @param imageWidth  image width
     * @param imageHeight image height
     */
    fun changeRealScreenHeight(imageWidth: Int, imageHeight: Int, showImmediately: Boolean) {
        if (isPreviewFullScreenMode || screenWidth > screenHeight) {
            return
        }
        val ratio = imageWidth.toFloat() / imageHeight.toFloat()
        val displayHeight = (screenWidth / ratio).toInt()
        if (displayHeight > screenHeight) {
            screenHeight = appInScreenHeight
            if (showImmediately) {
                magicalWrapper.setWidth(screenWidth.toFloat())
                magicalWrapper.setHeight(screenHeight.toFloat())
            }
        }
    }

    fun resetStartNormal(realWidth: Int, realHeight: Int, showImmediately: Boolean) {
        screenSize
        startNormal(realWidth, realHeight, showImmediately)
    }

    fun setViewParams(
        left: Int,
        top: Int,
        originWidth: Int,
        originHeight: Int,
        realWidth: Int,
        realHeight: Int
    ) {
        this.realWidth = realWidth
        this.realHeight = realHeight
        mOriginLeft = left
        mOriginTop = top
        mOriginWidth = originWidth
        mOriginHeight = originHeight
    }

    private fun setOriginParams() {
        val locationImage = IntArray(2)
        contentLayout.getLocationOnScreen(locationImage)
        targetEndLeft = 0
        if (screenWidth / screenHeight.toFloat() < realWidth / realHeight.toFloat()) {
            targetImageWidth = screenWidth
            targetImageHeight = (targetImageWidth * (realHeight / realWidth.toFloat())).toInt()
            targetImageTop = (screenHeight - targetImageHeight) / 2
        } else {
            targetImageHeight = screenHeight
            targetImageWidth = (targetImageHeight * (realWidth / realHeight.toFloat())).toInt()
            targetImageTop = 0
            targetEndLeft = (screenWidth - targetImageWidth) / 2
        }
        magicalWrapper.setWidth(mOriginWidth.toFloat())
        magicalWrapper.setHeight(mOriginHeight.toFloat())
        magicalWrapper.marginLeft = mOriginLeft
        magicalWrapper.marginTop = mOriginTop
    }

    private fun beginShow(showImmediately: Boolean) {
        if (showImmediately) {
            mAlpha = 1f
            backgroundView.alpha = mAlpha
            showNormalMin(
                targetImageTop.toFloat(),
                targetEndLeft.toFloat(),
                targetImageWidth.toFloat(),
                targetImageHeight.toFloat()
            )
            setShowEndParams()
        } else {
            val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator.addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                showNormalMin(
                    value,
                    mOriginTop.toFloat(),
                    targetImageTop.toFloat(),
                    mOriginLeft.toFloat(),
                    targetEndLeft.toFloat(),
                    mOriginWidth.toFloat(),
                    targetImageWidth.toFloat(),
                    mOriginHeight.toFloat(),
                    targetImageHeight.toFloat()
                )
            }
            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    setShowEndParams()
                }
            })
            if (selectorConfig.interpolatorFactory != null) {
                val interpolator = selectorConfig.interpolatorFactory!!.newInterpolator()
                if (interpolator != null) {
                    valueAnimator.interpolator = interpolator
                }
            }
            valueAnimator.setDuration(animationDuration).start()
            changeBackgroundViewAlpha(false)
        }
    }

    private fun setShowEndParams() {
        isAnimating = false
        changeContentViewToFullscreen()
        if (onMagicalViewCallback != null) {
            onMagicalViewCallback!!.onBeginMagicalAnimComplete(this@MagicalView, false)
        }
    }

    private fun showNormalMin(
        animRatio: Float, startY: Float, endY: Float, startLeft: Float, endLeft: Float,
        startWidth: Float, endWidth: Float, startHeight: Float, endHeight: Float
    ) {
        showNormalMin(
            false,
            animRatio,
            startY,
            endY,
            startLeft,
            endLeft,
            startWidth,
            endWidth,
            startHeight,
            endHeight
        )
    }

    private fun showNormalMin(endY: Float, endLeft: Float, endWidth: Float, endHeight: Float) {
        showNormalMin(true, 0f, 0f, endY, 0f, endLeft, 0f, endWidth, 0f, endHeight)
    }

    private fun showNormalMin(
        showImmediately: Boolean,
        animRatio: Float,
        startY: Float,
        endY: Float,
        startLeft: Float,
        endLeft: Float,
        startWidth: Float,
        endWidth: Float,
        startHeight: Float,
        endHeight: Float
    ) {
        if (showImmediately) {
            magicalWrapper.setWidth(endWidth)
            magicalWrapper.setHeight(endHeight)
            magicalWrapper.marginLeft = endLeft.toInt()
            magicalWrapper.marginTop = endY.toInt()
        } else {
            val xOffset = animRatio * (endLeft - startLeft)
            val widthOffset = animRatio * (endWidth - startWidth)
            val heightOffset = animRatio * (endHeight - startHeight)
            val topOffset = animRatio * (endY - startY)
            magicalWrapper.setWidth(startWidth + widthOffset)
            magicalWrapper.setHeight(startHeight + heightOffset)
            magicalWrapper.marginLeft = (startLeft + xOffset).toInt()
            magicalWrapper.marginTop = (startY + topOffset).toInt()
        }
    }

    fun backToMin() {
        if (isAnimating) {
            return
        }
        if (mOriginWidth == 0 || mOriginHeight == 0) {
            backToMinWithoutView()
            return
        }
        if (onMagicalViewCallback != null) {
            onMagicalViewCallback!!.onBeginBackMinAnim()
        }
        beginBackToMin(false)
        backToMinWithTransition()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun backToMinWithTransition() {
        contentLayout.post {
            TransitionManager.beginDelayedTransition(
                contentLayout.parent as ViewGroup, TransitionSet()
                    .setDuration(animationDuration)
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeTransform())
                    .addTransition(ChangeImageTransform())
            )
            beginBackToMin(true)
            contentLayout.translationX = 0f
            contentLayout.translationY = 0f
            magicalWrapper.setWidth(mOriginWidth.toFloat())
            magicalWrapper.setHeight(mOriginHeight.toFloat())
            magicalWrapper.marginTop = mOriginTop
            magicalWrapper.marginLeft = mOriginLeft
            changeBackgroundViewAlpha(true)
        }
    }

    private fun beginBackToMin(isResetSize: Boolean) {
        if (isResetSize) {
            onMagicalViewCallback!!.onBeginBackMinMagicalFinish(true)
        }
    }

    private fun backToMinWithoutView() {
        contentLayout.animate().alpha(0f).setDuration(animationDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (onMagicalViewCallback != null) {
                        onMagicalViewCallback!!.onMagicalViewFinish()
                    }
                }
            }).start()
        backgroundView.animate().alpha(0f).setDuration(animationDuration).start()
    }

    /**
     * @param isAlpha 是否透明
     */
    private fun changeBackgroundViewAlpha(isAlpha: Boolean) {
        val end: Float = if (isAlpha) 0 else 1f
        val valueAnimator = ValueAnimator.ofFloat(mAlpha, end)
        valueAnimator.addUpdateListener { animation ->
            isAnimating = true
            mAlpha = animation.animatedValue as Float
            backgroundView.alpha = mAlpha
            if (onMagicalViewCallback != null) {
                onMagicalViewCallback!!.onBackgroundAlpha(mAlpha)
            }
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
                if (isAlpha) {
                    if (onMagicalViewCallback != null) {
                        onMagicalViewCallback!!.onMagicalViewFinish()
                    }
                }
            }
        })
        valueAnimator.duration = animationDuration
        valueAnimator.start()
    }

    fun setMagicalContent(view: View?) {
        contentLayout.addView(view)
    }

    private fun changeContentViewToFullscreen() {
        targetImageHeight = screenHeight
        targetImageWidth = screenWidth
        targetImageTop = 0
        magicalWrapper.setHeight(screenHeight.toFloat())
        magicalWrapper.setWidth(screenWidth.toFloat())
        magicalWrapper.marginTop = 0
        magicalWrapper.marginLeft = 0
    }

    fun setBackgroundAlpha(mAlpha: Float) {
        this.mAlpha = mAlpha
        backgroundView.alpha = mAlpha
    }

    private var startX = 0
    private var startY = 0
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val childView = contentLayout.getChildAt(0)
        var viewPager2: ViewPager2? = null
        if (childView is ViewPager2) {
            // 如果MagicalView包含的是ViewPage2 需要处理一下滑动事件冲突，主要是针对长图可以上下滑动时会与左右滑动冲突
            viewPager2 = childView
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x.toInt()
                startY = event.y.toInt()
                if (viewPager2 != null) {
                    viewPager2.isUserInputEnabled = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = event.x.toInt()
                val endY = event.y.toInt()
                val disX = Math.abs(endX - startX)
                val disY = Math.abs(endY - startY)
                if (disX > disY) {
                    if (viewPager2 != null) {
                        viewPager2.isUserInputEnabled = true
                    }
                } else {
                    if (viewPager2 != null) {
                        viewPager2.isUserInputEnabled = canScrollVertically(startY - endY)
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> if (viewPager2 != null) {
                viewPager2.isUserInputEnabled = true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private var onMagicalViewCallback: OnMagicalViewCallback? = null
    fun setOnMojitoViewCallback(onMagicalViewCallback: OnMagicalViewCallback?) {
        this.onMagicalViewCallback = onMagicalViewCallback
    }

    init {
        selectorConfig = instance!!.selectorConfig
        isPreviewFullScreenMode = selectorConfig.isPreviewFullScreenMode
        appInScreenHeight = DensityUtil.getRealScreenHeight(getContext())
        screenSize
        backgroundView = View(context)
        backgroundView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        backgroundView.alpha = mAlpha
        addView(backgroundView)
        contentLayout = FrameLayout(context!!)
        contentLayout.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(contentLayout)
        magicalWrapper = MagicalViewWrapper(contentLayout)
    }
}