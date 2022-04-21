package com.xy.baselib.widget.recycler.diver

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.xy.baselib.widget.recycler.adapter.RecyclerAdapterWrapper
import com.xy.utils.getResColor
import com.xy.utils.getResDimension

/**
 * @author jin
 * @Description: recyclerView分割线
 * @date: 2015/11/12 9:47
 * @version: V1.0
 */
open class DiverItemDecoration : ItemDecoration {
    private val horizontalPaint: Paint = Paint()
    private val verticalPaint: Paint = Paint()
    private var spanCount = 0
    private var horizontalWidth:Int = 0
    private var verticalHeight:Int = 0


    constructor(context:Context, @ColorRes horizontalColor: Int, @DimenRes horizontalWidth: Int, @ColorRes verticalColor: Int
                , @DimenRes verticalHeight: Int, spanCount:Int)  {
        horizontalPaint.color = context.getResColor(horizontalColor)
        verticalPaint.color = context.getResColor(verticalColor)
        this.horizontalWidth = context.getResDimension(horizontalWidth)
        this.verticalHeight = context.getResDimension(verticalHeight)
        this.spanCount = spanCount
    }

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
        var top = child.top
        var bottom = child.bottom
        var left = child.left
        var right = child.right
        val column = index % spanCount
        val rightSize = horizontalWidth - (column + 1) * horizontalWidth / spanCount
        val leftSize = column * horizontalWidth / spanCount

        canvas.drawRect(left.toFloat()-leftSize, top.toFloat(),left
            .toFloat(), bottom.toFloat(), horizontalPaint)

        canvas.drawRect(right.toFloat(), top.toFloat(),(right+rightSize)
            .toFloat(), bottom.toFloat(), horizontalPaint)
        if (index >= spanCount)
            canvas.drawRect(left.toFloat(), top.toFloat() - verticalHeight,right.toFloat()
                , top.toFloat(), verticalPaint)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        var position = parent.getChildLayoutPosition(view)
        val adapter = parent.adapter?:return
        if(adapter.getItemViewType(position)<0)  return
        val column = (position-getHeadSize(adapter)) % spanCount
        outRect.left = column * horizontalWidth / spanCount;
        outRect.right = horizontalWidth - (column + 1) * horizontalWidth / spanCount
        if ((position-getHeadSize(adapter)) >= spanCount)
            outRect.top = verticalHeight
    }
    private fun getHeadSize(adapter:RecyclerView.Adapter<*>):Int{
        return if (adapter is RecyclerAdapterWrapper<*>) adapter.heardMap.size else 0
    }

}