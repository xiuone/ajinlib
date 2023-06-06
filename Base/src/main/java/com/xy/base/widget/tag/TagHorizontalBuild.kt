package com.xy.base.widget.tag

import android.util.AttributeSet
import android.view.View
import com.xy.base.R

class TagHorizontalBuild(private val view: View,private val attrs: AttributeSet?=null) {
    private val context by lazy { view.context }
    private var itemPaddingH = 0
    private var itemPaddingV = 0
    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagView)
            itemPaddingH = typedArray.getDimensionPixelSize(R.styleable.TagView_tag_padding_item_h,itemPaddingH)
            itemPaddingV = typedArray.getDimensionPixelSize(R.styleable.TagView_tag_padding_item_v,itemPaddingV)
        }
    }
}