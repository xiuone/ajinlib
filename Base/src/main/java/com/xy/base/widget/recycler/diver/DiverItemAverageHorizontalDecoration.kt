package com.xy.base.widget.recycler.diver

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * @author jin
 * @Description: recyclerView分割线
 * @date: 2015/11/12 9:47
 * @version: V1.0
 */
open class DiverItemAverageHorizontalDecoration (@ColorInt horizontalColor: Int,private val horizontalWidth: Int,
                                                 private val showLeft:Boolean,private val showRight:Boolean): ItemDecoration() {
    private val drawer = Drawer(horizontalColor,horizontalWidth,0)


    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        for (position in 0 until parent.childCount) {
            val child = parent.getChildAt(position)
            val endPosition = parent.childCount - 1
            if (position == 0 && showLeft){
                drawer.drawLeft(child,canvas)
            }
            if (position == endPosition && showRight){
                drawer.drawRight(child,canvas)
            }
            if (position != endPosition){
                drawer.drawRight(child,canvas)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        parent.adapter?.run {
            val position = parent.getChildLayoutPosition(view)
            val endPosition = itemCount - 1
            if (position == 0 && showLeft){
                outRect.left = horizontalWidth
            }
            if ((position == endPosition && showRight)){
                outRect.right = horizontalWidth
            }
            if (position != endPosition){
                outRect.right = horizontalWidth
            }
        }
    }
}