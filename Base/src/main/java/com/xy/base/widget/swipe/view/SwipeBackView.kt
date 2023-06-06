package com.xy.base.widget.swipe.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.xy.base.widget.swipe.SwipeDrawHelper
import com.xy.base.widget.swipe.SwipeTouchHelper
import com.xy.base.widget.swipe.listener.SwipeHelperListener

class SwipeBackView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr), SwipeHelperListener {
    private val mSwipeBackHelper by lazy { SwipeTouchHelper(this) }
    private val mSwipeDrawHelper by lazy { SwipeDrawHelper(this) }
    override fun getSwipeBackHelper(): SwipeTouchHelper {
        return mSwipeBackHelper
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mSwipeBackHelper.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mSwipeDrawHelper.onDraw(canvas,mSwipeBackHelper.percent,mSwipeBackHelper.currentEdgeType,mSwipeBackHelper.downPoint)
    }
}
