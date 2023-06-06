package com.xy.base.dialog.base

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation

abstract class BaseBottomDialog(context: Context) : BaseDialog(context) {

    override fun proportion(): Double = 1.0

    override fun gravity(): Int {
        return Gravity.BOTTOM
    }


    /**
     * 显示动画
     */
    override fun showAnimation(view: View?){
        view?.run {
            val animator = TranslateAnimation(Animation.RELATIVE_TO_SELF,0F,Animation.RELATIVE_TO_SELF,0F
                ,Animation.RELATIVE_TO_SELF,1F,Animation.RELATIVE_TO_SELF,0F)
            animator.duration = 300
            startAnimation(animator)
        }
    }
}