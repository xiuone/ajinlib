package com.xy.baselib.widget.multiline.label.item

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.xy.baselib.R

class ItemTopView @JvmOverloads constructor(context: Context, private val attrs: AttributeSet?=null, defStyleAttr:Int = 0)
    : ItemBaseView(context, attrs, defStyleAttr) {
    init {
        orientation = VERTICAL
    }

    override fun layoutRes(): Int = R.layout.item_left_top

    override fun drawMargin(params: LayoutParams, drawMargin: Int): LayoutParams {
        params.bottomMargin = drawMargin
        return params
    }

    override fun getDrawMargin(params: ViewGroup.LayoutParams): Int = if (params is LayoutParams) params.bottomMargin else 0
}
