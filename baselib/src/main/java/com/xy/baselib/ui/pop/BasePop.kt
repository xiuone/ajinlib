package com.xy.baselib.ui.pop

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import com.xy.baselib.R
import com.xy.baselib.utils.expandRes

abstract class BasePop(context: Context) : PopupWindow(context) {
    init {
        isFocusable = true
        isOutsideTouchable = true
        contentView = LayoutInflater.from(context).inflate(LayoutRes(), null)
        setBackgroundDrawable(ColorDrawable(expandRes.getColor(context,R.color.transparent)))
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        initView(context)
    }

    @LayoutRes
    abstract fun LayoutRes(): Int
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