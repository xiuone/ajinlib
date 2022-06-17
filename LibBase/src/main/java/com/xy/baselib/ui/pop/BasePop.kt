package com.xy.baselib.ui.pop

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import com.xy.baselib.R
import com.xy.baselib.exp.getResColor
import com.xy.baselib.exp.getResDimension

abstract class BasePop(context: Context) : PopupWindow(context) {
    protected var rootView : View?=null
    init {
        isFocusable = true
        isOutsideTouchable = true
        rootView = LayoutInflater.from(context).inflate(layoutRes(), null)
        contentView = rootView
        setBackgroundDrawable(ColorDrawable(context.getResColor(R.color.transparent)))
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        initView(context)
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        runTranslateAnimation(anchor)
    }

    protected fun runTranslateAnimation(view: View?){
        view?.run {
            val itemHeight: Int = context.getResDimension(R.dimen.dp_88)
            val translateAnimation = TranslateAnimation(0F, 0F, (-itemHeight).toFloat(), 0F)
            translateAnimation.duration = 300
            contentView.startAnimation(translateAnimation)
        }
    }


    @LayoutRes
    abstract fun layoutRes(): Int
    abstract fun initView(context: Context)

    fun changeHeight(anchor:View?){
        if (Build.VERSION.SDK_INT >= 24 && anchor != null) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h: Int = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
    }
}