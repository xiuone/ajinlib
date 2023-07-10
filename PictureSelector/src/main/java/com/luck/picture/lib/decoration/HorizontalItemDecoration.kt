package com.luck.picture.lib.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author：luck
 * @data：2022/1/16 下午23:50
 * @describe:HorizontalItemDecoration
 */
class HorizontalItemDecoration(private val spanCount: Int, private val spacing: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        if (position == 0) {
            outRect.left = spacing - column * spacing / spanCount
        } else {
            outRect.left = column * spacing / spanCount
        }
        outRect.right = spacing - (column + 1) * spacing / spanCount
        if (position < spanCount) {
            outRect.top = spacing
        }
        outRect.bottom = spacing
    }
}