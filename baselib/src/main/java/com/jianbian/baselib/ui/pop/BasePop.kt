package com.jianbian.baselib.ui.pop

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import com.jianbian.baselib.R
import com.jianbian.baselib.utils.AppUtil

abstract class BasePop(context: Context) : PopupWindow(context) {
    init {
        isFocusable = true
        isOutsideTouchable = true
        contentView = LayoutInflater.from(context).inflate(LayoutRes(), null)
        setBackgroundDrawable(ColorDrawable(AppUtil.getColor(context,R.color.transparent)))
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        initView()
    }

    @LayoutRes
    abstract fun LayoutRes(): Int
    abstract fun initView()
}