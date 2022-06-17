package com.xy.baselib.widget.multiline

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.xy.baselib.exp.Logger

open class MultiBaseFrameLayout  @JvmOverloads constructor(context: Context, private val attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    protected var spaceHorizontal = 0
    protected var spaceVertical = 0


    protected fun resetEqualView(horSize:Int) {
        if (width == 0)return
        for (index in 0 until childCount) {
            val childView = getChildAt(index)
            val itemWidth = (width - spaceHorizontal * (horSize - 1)) / horSize
            val leftMargin = (index % horSize) * (itemWidth + spaceHorizontal)
            val topMargin = (index / horSize) * (itemWidth + spaceVertical)
            resetView(childView,itemWidth,itemWidth,leftMargin,topMargin)
        }
    }


    protected fun resetEqualView(horSize:Int,itemHeight: Int) {
        if (width == 0)return
        for (index in 0 until childCount) {
            val childView = getChildAt(index)
            val itemWidth = (width -paddingLeft-paddingRight- spaceHorizontal * (horSize - 1)) / horSize
            val leftMargin = (index % horSize) * (itemWidth + spaceHorizontal)
            val topMargin = (index / horSize) * (itemHeight + spaceVertical)
            resetView(childView,itemWidth,itemHeight,leftMargin,topMargin)
        }
    }

    protected fun resetMoreView(itemHeight: Int) {
        if (width == 0)return
        var lineMap = HashMap<Int,Int>()
        var lineViewMap = HashMap<Int,ArrayList<View>>()
        val canUseViewWidth = width-paddingLeft-paddingRight
        for (index in 0 until childCount) {
            val childView = getChildAt(index)
            val itemWidth = childView.width
            var isAdd = false
            var line = lineViewMap.size
            for (key in lineMap.keys) {
                val value = lineMap[key] ?: 0
                val useWidth = itemWidth + value + spaceHorizontal
                if (useWidth < canUseViewWidth) {//判断使用的长度是否能在这一行放下
                    lineMap[key] = useWidth
                    var topMargin = key * (itemHeight + spaceVertical)
                    resetView(childView,
                        itemWidth,
                        itemHeight,
                        value + spaceHorizontal,
                        topMargin)
                    isAdd = true
                    line = key
                    break
                }
            }
            if (!isAdd) {
                var topMargin = lineMap.size * (itemHeight + spaceVertical)
                resetView(childView, itemWidth, itemHeight, 0, topMargin)
                lineMap[lineMap.size] = itemWidth
            }
            val list = lineViewMap[line] ?: ArrayList<View>()
            list.add(childView)
            lineViewMap[line] = list
        }
    }

    protected fun resetView(childView : View, itemWidth:Int, itemHeight:Int, leftMargin:Int, topMargin:Int) {
        val params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.width = itemWidth
        params.height = itemHeight
        params.leftMargin = leftMargin
        params.topMargin = topMargin
        childView.layoutParams = params
    }

}