package com.xy.base.pop

import android.view.View
import androidx.annotation.LayoutRes

interface PopListener {
    @LayoutRes
    fun popLayoutRes():Int?
    fun popInitView(pop: BasePop){}
    fun popShow(itemView:View?,pop: BasePop){}
    fun popShow(itemView:View?,pop: BasePop,any: Any?){}
    fun popDismiss(pop: BasePop){}
}