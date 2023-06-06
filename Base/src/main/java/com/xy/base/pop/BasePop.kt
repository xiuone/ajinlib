package com.xy.base.pop

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import com.xy.base.R
import com.xy.base.utils.exp.getResColor
import com.xy.base.utils.exp.getResDimension

abstract class BasePop(context: Context) : PopupWindow(context),PopupWindow.OnDismissListener {
    val rootView : View by lazy { LayoutInflater.from(context).inflate(layoutRes(), null) }
    init {
        setBackgroundDrawable(ColorDrawable(context.getResColor(R.color.transparent)))
        isFocusable = true
        isOutsideTouchable = true
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

    override fun onDismiss() {}

    @LayoutRes
    abstract fun layoutRes(): Int
    open fun initView(context: Context){}

    fun changeHeight(anchor:View?){
        if (Build.VERSION.SDK_INT >= 24 && anchor != null) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h: Int = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
    }
}