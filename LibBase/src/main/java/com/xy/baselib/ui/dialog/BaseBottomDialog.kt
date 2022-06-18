package com.xy.baselib.ui.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.xy.baselib.R

abstract class BaseBottomDialog(context: Context) : BaseDialog(context) {

    override fun proportion(): Double = 1.0

    override fun gravity(): Int {
        return Gravity.BOTTOM
    }



    /**
     * 显示动画
     */
    override fun showAnimation(view: View?){
        window?.setWindowAnimations(R.style.BottomDialog_Animation)
    }
}