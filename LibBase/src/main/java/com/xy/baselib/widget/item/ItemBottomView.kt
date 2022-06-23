package com.xy.baselib.widget.item

import android.content.Context
import android.util.AttributeSet
import com.xy.baselib.R
import com.xy.baselib.exp.getResDimension

open class ItemBottomView @JvmOverloads constructor(context: Context, private val attrs: AttributeSet?=null, defStyleAttr:Int = 0)
    : ItemBaseView(context, attrs, defStyleAttr) {
    init {
        orientation = VERTICAL
    }

    override fun layoutRes(): Int = R.layout.item_xiu_right_bottom

    override fun drawMargin(params: LayoutParams,drawSize:Int, drawMargin: Int): LayoutParams {
        params.topMargin = drawMargin
        params.width = drawSize+context.getResDimension(R.dimen.dp_10)*2
        return params
    }
}
