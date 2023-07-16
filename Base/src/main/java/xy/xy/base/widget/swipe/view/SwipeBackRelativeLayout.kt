package xy.xy.base.widget.swipe.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import xy.xy.base.R
import xy.xy.base.widget.swipe.SwipeDrawHelper
import xy.xy.base.widget.swipe.SwipeTouchHelper
import xy.xy.base.widget.swipe.listener.SwipeHelperListener

open class SwipeBackRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr),
    SwipeHelperListener {
    private val mSwipeBackHelper by lazy { SwipeTouchHelper(this) }

    private val swipeDrawHelper by lazy { SwipeDrawHelper(this) }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mSwipeBackHelper.onTouchEvent(event)
                super.dispatchTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (mSwipeBackHelper.onTouchEvent(event)) {
                    event.action = MotionEvent.ACTION_CANCEL
                }
                super.dispatchTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mSwipeBackHelper.isRun) {
                    mSwipeBackHelper.onTouchEvent(event)
                    event.action = MotionEvent.ACTION_CANCEL
                }
                return super.dispatchTouchEvent(event)
            }
        }
        super.dispatchTouchEvent(event)
        return true
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        swipeDrawHelper.onDraw(canvas,mSwipeBackHelper.percent,mSwipeBackHelper.currentEdgeType,mSwipeBackHelper.downPoint)
    }

    override fun getSwipeBackHelper(): SwipeTouchHelper {
        return mSwipeBackHelper
    }

    init {
        setBackgroundColor(resources.getColor(R.color.transparent))
    }

}