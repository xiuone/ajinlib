package com.xy.baselib.widget.recycler.diver

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.xy.utils.dp2px
import com.xy.utils.getResColor

/**
 * @author jin
 * @Description: recyclerView分割线
 * @date: 2015/11/12 9:47
 * @version: V1.0
 */
open class DiverItemAverageHorizontalDecoration : ItemDecoration {
    private val horizontalPaint: Paint = Paint()
    private var horizontalWidth:Int = 0
    private var showLeft = false
    private var showRight = false

    constructor(context:Context,@ColorRes horizontalColor: Int, horizontalWidth: Float,showLeft:Boolean,showRight:Boolean)  {
        horizontalPaint.color = context.getResColor(horizontalColor)
        this.horizontalWidth = context.dp2px(horizontalWidth)
        this.showLeft = showLeft
        this.showRight = showRight
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        for (position in 0 until parent.childCount) {
            val child = parent.getChildAt(position)
            var top = child.top
            var bottom = child.bottom
            var left = child.left
            var right = child.right

            if (position == 0 && showLeft){
                canvas.drawRect(left.toFloat() - horizontalWidth, top.toFloat()
                    ,left.toFloat(), bottom.toFloat(), horizontalPaint)
            }
            if ((position == (parent.childCount - 1) && showRight) || (position != (parent.childCount - 1))){
                canvas.drawRect(right.toFloat(), top.toFloat()
                    ,right.toFloat() + horizontalWidth, bottom.toFloat(), horizontalPaint)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        parent.adapter?.run {
            val position = parent.getChildLayoutPosition(view)
            val itemCount = this.itemCount
            if (position == 0 && showLeft){
                outRect.left = horizontalWidth
            }
            if ((position == (itemCount - 1) && showRight) || (position != (itemCount - 1))){
                outRect.right = horizontalWidth
            }
        }
    }
}