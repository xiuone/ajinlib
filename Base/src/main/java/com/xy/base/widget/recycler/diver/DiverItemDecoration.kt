package com.xy.base.widget.recycler.diver

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.xy.base.widget.recycler.adapter.RecyclerAdapterWrapper

/**
 * @author jin
 * @Description: recyclerView分割线
 * @date: 2015/11/12 9:47
 * @version: V1.0
 */
open class DiverItemDecoration(verticalColor: Int = 0X00FFFFFF,val verticalHeight: Int,
                               horizontalColor: Int = 0X00FFFFFF,val horizontalWidth: Int =0,
                               val spanCount:Int = 1) : ItemDecoration() {
    private val horizontalDrawer: Drawer = Drawer(horizontalColor,horizontalWidth,0)
    private val verticalDrawer: Drawer = Drawer(verticalColor,0,verticalHeight)

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        for (index in 0 until parent.childCount) {
           onDraw(parent, index, canvas)
        }
    }

    private fun onDraw(parent: RecyclerView,index: Int,canvas: Canvas){
        val adapter = parent.adapter?:return
        if(adapter.getItemViewType(index)<0)  return
        val child = parent.getChildAt(index)
        val currentPosition = getCurrentPosition(index,adapter)
        val column = currentPosition % spanCount
        val rightSize = horizontalWidth - (column + 1) * horizontalWidth / spanCount
        val leftSize = column * horizontalWidth / spanCount

        if (currentPosition < spanCount) {
            horizontalDrawer.drawLeft(child, canvas, leftSize)
            horizontalDrawer.drawRight(child, canvas, rightSize)
        }else{
            horizontalDrawer.drawLeft(child, canvas, leftSize,verticalHeight)
            horizontalDrawer.drawRight(child, canvas, rightSize,verticalHeight)
            verticalDrawer.drawTop(child,canvas,0,verticalHeight)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        var position = parent.getChildLayoutPosition(view)
        val adapter = parent.adapter?:return
        if(adapter.getItemViewType(position)<0)  return
        val currentPosition = getCurrentPosition(position,adapter)
        val column = currentPosition % spanCount
        outRect.left = column * horizontalWidth / spanCount
        outRect.right = horizontalWidth - (column + 1) * horizontalWidth / spanCount
        if (currentPosition >= spanCount)
            outRect.top = verticalHeight
    }

    private fun getCurrentPosition(index:Int,adapter:RecyclerView.Adapter<*>):Int{
        val headSize = if (adapter is RecyclerAdapterWrapper<*>) adapter.heardMap.size else 0
        return index - headSize
    }
}