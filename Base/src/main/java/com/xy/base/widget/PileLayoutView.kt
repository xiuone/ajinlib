package com.xy.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.xy.base.R
import com.xy.base.utils.exp.getResDimension

class PileLayoutView<T> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, ) :
    FrameLayout(context, attrs, defStyleAttr) {
    /**
     * 重叠宽度
     */
    private val pileWidth: Int
    private val childWidth: Int
    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.PileLayout)
        pileWidth = ta.getDimensionPixelSize(R.styleable.PileLayout_PileLayout_pileWidth, context.getResDimension(R.dimen.dp_10))
        childWidth = ta.getDimensionPixelSize(R.styleable.PileLayout_PileLayout_childWidth, context.getResDimension(R.dimen.dp_20))
        ta.recycle()
    }

    fun setData(data:MutableList<T>?,listener:PileListener<T>){
        this.removeAllViews()
        val newData = data?:ArrayList()
        for ((index,item) in newData.withIndex()){
            if (listener.canAdd(context,index, item)){
                val layoutParams = LayoutParams(childWidth,childWidth)
                layoutParams.leftMargin = (childWidth - pileWidth) * index
                val itemView = listener.onCreateItemView(context,index,item)
                this.addView(itemView)
                itemView.layoutParams = layoutParams
            }else{
                val layoutParams = LayoutParams(childWidth,childWidth)
                layoutParams.leftMargin = (childWidth - pileWidth) * index
                val itemView = listener.onCreateEndItemView(context,index,item,newData.size)
                this.addView(itemView)
                itemView.layoutParams = layoutParams
                return
            }
        }
    }

    interface PileListener<T>{
        fun canAdd(context: Context,index: Int,item:T):Boolean
        fun onCreateItemView(context: Context,index: Int,item:T):View
        fun onCreateEndItemView(context: Context,index: Int,item:T,all:Int):View
    }

}

