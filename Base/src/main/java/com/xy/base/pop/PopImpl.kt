package com.xy.base.pop

import android.content.Context
import com.xy.base.R

class PopImpl(context: Context, private val popListener: PopListener) : BasePop(context) {

    init {
        popListener.popInitView(this)
        setOnDismissListener(this)
    }

    override fun onDismiss() {
        popListener.popDismiss(this)
    }

    override fun layoutRes(): Int {
        return popListener.popLayoutRes()?:R.layout.layout_xiu_layer_view
    }
}