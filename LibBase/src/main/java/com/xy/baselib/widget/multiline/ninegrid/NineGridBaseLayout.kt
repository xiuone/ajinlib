package com.xy.baselib.widget.multiline.ninegrid

import android.content.Context
import android.util.AttributeSet
import com.xy.baselib.R
import com.xy.baselib.widget.multiline.MultiBaseFrameLayout
import com.xy.baselib.exp.getResDimension

abstract class NineGridBaseLayout<T> @JvmOverloads constructor(context: Context, private val attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    MultiBaseFrameLayout(context, attrs, defStyleAttr) {
    private val data  by lazy { ArrayList<T>() }

    var itemSpace = 0
        set(value) {
            field = value
            spaceHorizontal = value
            spaceVertical = value
            requestLayout()
        }

    init {
        itemSpace = context.getResDimension(R.dimen.dp_2)
        spaceHorizontal = itemSpace
        spaceVertical =itemSpace
    }

    @Synchronized
    open fun setData(data:MutableList<T>){
        this.removeAllViews()
        this.data.addAll(data)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val horSize = if (childCount == 1) 1 else if (childCount == 2 || childCount == 4) 2 else 3
        if (horSize > 1){
            resetEqualView(horSize)
        }else{
            resetOneSize()
        }
    }


    private fun resetOneSize(){
        if (childCount>0){
            val childView = getChildAt(0)
            val childWidth = childView.width
            val childHeight = childView.height
            val item23 = (width-paddingLeft-paddingRight)*2/3
            if (childHeight > childWidth){
                val itemHeight = if (childHeight >= item23) item23 else childHeight
                var itemWidth = itemHeight*childWidth/childHeight
                itemWidth = if((itemWidth*2) < itemHeight) itemHeight/2 else itemWidth
                resetView(childView,itemWidth,itemHeight,0,0)
            }else{
                val itemWidth = if (childWidth >= item23) item23 else childWidth
                var itemHeight = itemWidth*childHeight/childWidth
                itemHeight = if((itemHeight*2) < itemWidth) itemWidth/2 else itemHeight
                resetView(childView,itemWidth,itemHeight,0,0)
            }
        }
    }
}