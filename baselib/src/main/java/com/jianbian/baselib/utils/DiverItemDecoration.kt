package com.jianbian.baselib.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * @author jin
 * @Description: recyclerView分割线
 * @date: 2015/11/12 9:47
 * @version: V1.0
 */
open class DiverItemDecoration : ItemDecoration {
    private val horizontalPaint: Paint = Paint()
    private val verticalPaint: Paint = Paint()
    private var horizontalSize = 0
    private var horizontalWidth:Int = 0
    private var verticalHeight:Int = 0


    constructor(context:Context,@ColorRes horizontalColor: Int, horizontalWidth: Float, @ColorRes verticalColor: Int
                , verticalHeight: Float, horizontalSize:Int)  {
        horizontalPaint.color = AppUtil.getColor(context,horizontalColor)
        verticalPaint.color = AppUtil.getColor(context,verticalColor)
        this.horizontalWidth = AppUtil.dp2px(context,horizontalWidth)
        this.verticalHeight = AppUtil.dp2px(context,verticalHeight)
        this.horizontalSize = horizontalSize
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        for (index in 0 until parent.childCount) {
            val child = parent.getChildAt(index)
            var top = child.top
            var bottom = child.bottom
            var left = child.left
            var right = child.right
            var haveRight = ((index+1)% horizontalSize) != 0
            if (haveRight){
                canvas.drawRect(right.toFloat(), top.toFloat(),(right+horizontalWidth).toFloat(), bottom.toFloat(), horizontalPaint)
            }
            canvas.drawRect(left.toFloat(), bottom.toFloat(),right.toFloat(), (bottom+verticalHeight).toFloat(), verticalPaint)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        var haveRight = ((position+1)% horizontalSize) != 0
        var bottomMargin = 0
        var rightMargin = 0
        if (haveRight)
            rightMargin = horizontalWidth
        bottomMargin = verticalHeight
        outRect.set(0,0,rightMargin,bottomMargin)
    }
}