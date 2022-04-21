package com.xy.baselib.widget.tab

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class TabContainerMoreLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :LinearLayout(context, attrs, defStyleAttr) {
    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        var childCountWidth = 0;
        var left = 0
        var top = 0
        for (index in 0..childCount){
            val childView = getChildAt(index);
            childCountWidth += childView.width
            if (width < childCountWidth){
                top += childView.height
                left = 0
                childCountWidth = 0
            }
            childView.layout(left,top,childView.width+left,childView.height+top)
            left += childView.width
        }
    }
}