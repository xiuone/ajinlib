package com.xy.baselib.widget.recycler.diver

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.roundToInt

/**
 * Created by Zhenjie Yan on 1/30/19.
 */
class BorderItemDecoration @JvmOverloads constructor(@ColorInt color: Int, width: Int = 4, height: Int = 4, ) : ItemDecoration() {
    private val mWidth = (width / 2f).roundToInt()
    private val mHeight = (height / 2f).roundToInt()
    private val mDrawer: Drawer = Drawer(color, mWidth, mHeight)
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State, ) {
        outRect[mWidth, mHeight, mWidth] = mHeight
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val layoutManager = parent.layoutManager
        val childCount = layoutManager?.childCount?:0
        for (i in 0 until childCount) {
            val view = layoutManager?.getChildAt(i)
            mDrawer.drawLeft(view, canvas)
            mDrawer.drawTop(view, canvas)
            mDrawer.drawRight(view, canvas)
            mDrawer.drawBottom(view, canvas)
        }
        canvas.restore()
    }
}