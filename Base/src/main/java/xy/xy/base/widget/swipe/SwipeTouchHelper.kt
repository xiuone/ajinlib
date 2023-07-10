package xy.xy.base.widget.swipe

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import xy.xy.base.utils.exp.getResDimension
import xy.xy.base.R
import xy.xy.base.widget.swipe.listener.OnSwipeBackListener
import xy.xy.base.widget.swipe.mode.EdgeType
import kotlin.math.abs

class SwipeTouchHelper(val view: View) : ValueAnimator.AnimatorUpdateListener {
    private var swipeBackListener: OnSwipeBackListener? = null
    private val backRegion by lazy { view.context.getResDimension(R.dimen.dp_20).toFloat() }
    private val backMaxMoveRegion by lazy { view.context.getResDimension(R.dimen.dp_40).toFloat() }
    var downPoint:PointF = PointF()
    private var currentPoint :PointF = PointF()
    var isLeftBack = false
    var isRightBack = false
    var isRun = false
    var percent = 0f
    private val backPercent = 0.6F

    private var animator : ValueAnimator = ObjectAnimator.ofFloat(percent, 0f)
    var currentEdgeType = EdgeType.EDGE_NONO


    fun setSwipeBackListener(swipeBackListener: OnSwipeBackListener?) {
        this.swipeBackListener = swipeBackListener
    }

    fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        if (!isLeftBack && !isRightBack) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return actionDown(event)
            MotionEvent.ACTION_MOVE -> return actionMove(event)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> return actionUp(event)
        }
        return false
    }


    /**
     * 点下的时候  判断是否在可以返回的区域
     * @param event
     * @return
     */
    private fun actionDown(event: MotionEvent): Boolean {
        downPoint = PointF(event.x,event.y)
        currentEdgeType = EdgeType.EDGE_NONO
        isRun = false
        if (isLeftBack && !animator.isRunning && downPoint.x <= backRegion) {
            currentEdgeType = EdgeType.EDGE_LEFT
        } else if (isRightBack && !animator.isRunning && (view.width - downPoint.x) <= backRegion) {
            currentEdgeType = EdgeType.EDGE_RIGHT
        }
        return false
    }

    /**
     * 移动的时候 这时候需要显示view 去标榜  我正在返回
     * @param event
     */
    private fun actionMove(event: MotionEvent): Boolean {
        currentPoint = PointF(event.x,event.y)
        when(currentEdgeType){
            EdgeType.EDGE_NONO->return false
            else ->{
                isRun = if(abs(currentPoint.x - downPoint.x) > abs(currentPoint.y - downPoint.y)) true else isRun
            }
        }
        if (isRun){
            when(currentEdgeType){
                EdgeType.EDGE_LEFT-> percent = (currentPoint.x - downPoint.x)/backMaxMoveRegion
                EdgeType.EDGE_RIGHT-> percent = (downPoint.x - currentPoint.x)/backMaxMoveRegion
            }
            percent = if (percent> 1)  1F else if (percent <0) 0F else percent
            view.invalidate()
        }
        return isRun
    }

    private fun actionUp(event: MotionEvent): Boolean {
        if (isRun) {
            animator.cancel()
            animator = ObjectAnimator.ofFloat(percent, 0f)
            animator.addUpdateListener(this)
            animator.start()
            if (percent >= backPercent)
                swipeBackListener?.onBackPressed()
        }
        return isRun
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        var percent = animation.animatedValue
        if (percent is Float)
            this.percent = percent
        else
            this.percent = 0F
        view.invalidate()
    }
}