package com.xy.base.utils.anim

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator

object ViewAnimHelper {
    private const val mDuration: Long = 200

    /**
     * 隐藏的时候    缩放某个view
     * @param builder
     * @param views
     */
    fun alphaView(builder: AnimatorSet.Builder?,tag:Float, vararg views: View?) {
        for (view in views) {
            if (view != null) {
                val alphaAnim: ValueAnimator = ObjectAnimator.ofFloat(view, "alpha", view.alpha, tag)
                builder?.with(alphaAnim)
            }
        }
    }


    /**
     * 隐藏的时候    缩放某个view
     * @param builder
     * @param views
     */
    fun hintView(builder: AnimatorSet.Builder?, vararg views: View?) {
        for (view in views) {
            if (view != null) {
                val alphaAnim: ValueAnimator = ObjectAnimator.ofFloat(view, "alpha", view.alpha, 0f)
                val scaleX: ValueAnimator = ObjectAnimator.ofFloat(view, "scaleX", view.scaleX, 0f)
                val scaleY: ValueAnimator = ObjectAnimator.ofFloat(view, "scaleY", view.scaleX, 0f)
                builder?.with(alphaAnim)?.with(scaleX)?.with(scaleY)
            }
        }
    }

    /**
     * 显示的时候    缩放莫哥view
     * @param builder
     * @param views
     */
    fun showView(builder: AnimatorSet.Builder?, vararg views: View?) {
        for (view in views) {
            if (view != null) {
                val alphaAnim: ValueAnimator = ObjectAnimator.ofFloat(view, "alpha", view.alpha, 1f)
                val scaleX: ValueAnimator = ObjectAnimator.ofFloat(view, "scaleX", view.scaleX, 1f)
                val scaleY: ValueAnimator = ObjectAnimator.ofFloat(view, "scaleY", view.scaleX, 1f)
                builder?.with(alphaAnim)?.with(scaleX)?.with(scaleY)
            }
        }
    }

    /**
     * 显示的时候    缩放莫哥view
     * @param builder
     * @param views
     */
    fun scaleYView(builder: AnimatorSet.Builder?,targetValue: Float, vararg views: View?) {
        for (view in views) {
            if (view != null) {
                val scaleY: ValueAnimator = ObjectAnimator.ofFloat(view, "scaleY", view.scaleX, targetValue)
                builder?.with(scaleY)
            }
        }
    }

    /**
     * 隐藏的时候    缩放某个view
     * @param builder
     * @param views
     */
    fun rotationView(builder: AnimatorSet.Builder?,targetValue: Float, vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.rotation
                val rotationAnim: ValueAnimator = ObjectAnimator.ofFloat(view, "rotation", view.rotation, targetValue)
                builder?.with(rotationAnim)
            }
        }
    }

    /**
     * 显示的时候    缩放莫哥view
     * @param builder
     * @param views
     */
    fun setWidth(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator =
                ObjectAnimator.ofInt(viewWrapper, "Width", viewWrapper.width, targetValue)
            builder?.with(animator)
        }
    }

    /**
     * 显示的时候    缩放莫哥view
     * @param builder
     * @param views
     */
    fun setHeight(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(viewWrapper, "Height", viewWrapper.height, targetValue)
            builder?.with(animator)
        }
    }

    fun setAlpha(builder: AnimatorSet.Builder?, targetValue: Float, vararg views: View) {
        for (view in views) {
            val animator = ObjectAnimator.ofFloat(view, "Alpha", view.alpha, targetValue)
            builder?.with(animator)
        }
    }

    /**
     * 显示的时候    缩放莫哥view
     * @param builder
     * @param views
     */
    fun setScaleX(builder: AnimatorSet.Builder?, targetValue: Float, vararg views: View) {
        for (view in views) {
            val animator = ObjectAnimator.ofFloat(view, "ScaleX", view.scaleX, targetValue)
            builder?.with(animator)
        }
    }

    /**
     * 显示的时候    缩放莫哥view
     * @param builder
     * @param views
     */
    fun setScaleY(builder: AnimatorSet.Builder?, targetValue: Float, vararg views: View) {
        for (view in views) {
            val animator = ObjectAnimator.ofFloat(view, "ScaleY", view.scaleY, targetValue)
            builder?.with(animator)
        }
    }

    fun setMarginLeft(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(
                viewWrapper,
                "marginLeft",
                viewWrapper.marginLeft,
                targetValue
            )
            builder?.with(animator)
        }
    }

    fun setMarginTop(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(
                viewWrapper,
                "marginTop",
                viewWrapper.marginTop,
                targetValue
            )
            builder?.with(animator)
        }
    }

    /**
     * 设置marginRight
     * @param builder
     * @param targetValue
     * @param views
     */
    fun setMarginRight(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(
                viewWrapper,
                "marginRight",
                viewWrapper.marginRight,
                targetValue
            )
            builder?.with(animator)
        }
    }

    /***
     * 设置margin
     * @param builder
     * @param targetValue
     * @param views
     */
    fun setMarginBottom(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(
                viewWrapper,
                "marginBottom",
                viewWrapper.marginBottom,
                targetValue
            )
            builder?.with(animator)
        }
    }

    /***
     * 设置margin
     * @param builder
     * @param targetValue
     * @param views
     */
    fun setPaddingLeft(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(
                viewWrapper,
                "PaddingLeft",
                viewWrapper.paddingLeft,
                targetValue
            )
            builder?.with(animator)
        }
    }

    /***
     * 设置margin
     * @param builder
     * @param targetValue
     * @param views
     */
    fun setPaddingRight(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(
                viewWrapper,
                "PaddingRight",
                viewWrapper.paddingRight,
                targetValue
            )
            builder?.with(animator)
        }
    }

    /***
     * 设置margin
     * @param builder
     * @param targetValue
     * @param views
     */
    fun setPaddingTop(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(
                viewWrapper,
                "PaddingTop",
                viewWrapper.paddingTop,
                targetValue
            )
            builder?.with(animator)
        }
    }

    /***
     * 设置margin
     * @param builder
     * @param targetValue
     * @param views
     */
    fun setPaddingBottom(builder: AnimatorSet.Builder?, targetValue: Int, vararg views: View?) {
        for (view in views) {
            val viewWrapper = WrapperView(view)
            val animator = ObjectAnimator.ofInt(
                viewWrapper,
                "PaddingBottom",
                viewWrapper.paddingBottom,
                targetValue
            )
            builder?.with(animator)
        }
    }

    fun getAnimation(): AnimatorSet{
        return getAnimation(mDuration)
    }

    fun getAnimation(mDuration: Long): AnimatorSet {
        val animatorSet = AnimatorSet()
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.duration = mDuration
        return animatorSet
    }

    fun getBuilder(animatorSet: AnimatorSet?): AnimatorSet.Builder? {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        return animatorSet?.play(valueAnimator)
    }

    fun cancel(animatorSet: AnimatorSet?){
        animatorSet?.removeAllListeners()
        if (animatorSet?.isRunning == true) {
            animatorSet.cancel()
        }
    }
}