package com.xy.baselib.widget.multiline.label.item

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.xy.baselib.R
import com.xy.baselib.exp.getResDimension

open class ItemTopView @JvmOverloads constructor(context: Context, private val attrs: AttributeSet?=null, defStyleAttr:Int = 0)
    : ItemBaseView(context, attrs, defStyleAttr) {
    init {
        orientation = VERTICAL
    }

    override fun layoutRes(): Int = R.layout.item_left_top

    override fun drawMargin(params: LayoutParams,drawSize:Int, drawMargin: Int): LayoutParams {
        params.bottomMargin = drawMargin
        params.width = drawSize+context.getResDimension(R.dimen.dp_10)*2
        return params
    }
}
