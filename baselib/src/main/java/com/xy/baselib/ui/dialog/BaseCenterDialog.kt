package com.xy.baselib.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation

abstract class BaseCenterDialog(context: Context) : BaseDialog(context) {

    override fun proportion(): Double = 0.8

    override fun gravity(): Int {
        return Gravity.CENTER
    }

    /**
     * 显示动画
     */
    override fun showAnimation(view: View?){
        view?.run {
            val animator = ScaleAnimation(0F,1F,0F,1F,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            animator.duration = 300
            startAnimation(animator)
        }
    }
}