package com.xy.base.widget.label

import android.util.AttributeSet
import android.view.View
import com.xy.base.R

class LabelBuilder(private val view: View, private val attrs: AttributeSet? = null)  {
    private val context by lazy { view.context }
    var maxNumber  = -1
    var cowNumber= 0
    var paddingH = 0
    var paddingV = 0
    var spaceH = 0
    var spaceV= 0

    fun init(){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelView)
        maxNumber = typedArray.getInteger(R.styleable.LabelView_label_max_number, Int.MAX_VALUE)
        cowNumber = typedArray.getInteger(R.styleable.LabelView_label_cow_number, 3)

        paddingH = typedArray.getDimensionPixelSize(R.styleable.LabelView_label_item_padding_h, paddingH)
        paddingV = typedArray.getDimensionPixelSize(R.styleable.LabelView_label_item_padding_v, paddingV)
        spaceH = typedArray.getDimensionPixelSize(R.styleable.LabelView_label_item_space_h, spaceH)
        spaceV = typedArray.getDimensionPixelSize(R.styleable.LabelView_label_item_space_v, spaceV)
        typedArray.recycle()
    }
}