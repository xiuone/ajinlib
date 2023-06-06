package com.xy.base.widget.localmedia

import android.animation.AnimatorSet
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.xy.base.R
import com.xy.base.listener.ContextListener
import com.xy.base.utils.anim.ViewAnimHelper
import com.xy.base.utils.exp.getResDimension
import com.xy.base.utils.exp.getViewPosRect
import com.xy.base.utils.runMain
import kotlin.math.abs
import kotlin.math.max

class ResetPositionController(private val view:ViewGroup, var contextListener: ContextListener?,
                              private val positionListener:ResetPositionListener) {
    private val context by lazy { view.context }
    private val windowView by lazy { contextListener?.getCurrentAct()?.window?.decorView }
    private val animParams by lazy { ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT) }
    private val animView by lazy { FrameLayout(context) }
    private val minCheckMove by lazy { context.getResDimension(R.dimen.dp_2) }
    private val minAnimMove by lazy { context.getResDimension(R.dimen.dp_4) }

    val contentTag by lazy { "contentTag" }
    val delTag by lazy { "delTag" }
    /**
     * 检查移动
     */
    private var touchContent = false;
    private var touchMove = false;
    private var downTime = 0L
    private var downX = 0F
    private var downY = 0F
    private var downView :View?= null
    private var movePositionView:ViewGroup?=null
    private var oldParams :ViewGroup.MarginLayoutParams?= null
    private var startParams:ViewGroup.MarginLayoutParams?=null
    private var moveAnimatorSet: AnimatorSet? = null
    private var returnAnimatorSet: AnimatorSet? = null

    private var movePauseIndex = -1
    private var movePauseStartTime = 0L


    fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action){
            MotionEvent.ACTION_DOWN->{
                downX = ev.x
                downY = ev.y
                touchContent = false
                touchMove = false
                downTime = System.currentTimeMillis()
                movePauseIndex = -1
                movePauseStartTime = System.currentTimeMillis()
                touchContent = moveAnimatorSet?.isRunning == true && returnAnimatorSet?.isRunning == true && checkTouchContent(view,ev)
                return false
            }
            MotionEvent.ACTION_MOVE->{
                return if (touchMove){
                    moveAnimView(ev)
                    checkMoveView(ev)
                    true
                }else{
                    touchContent = checkTouchContent(view,ev)
                    checkCanMove(ev)
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP->{
                actionUp()
                return touchMove
            }
        }
        return false
    }

    /**
     * 检查是否在view上
     */
    private fun checkTouchContent(viewGroup: ViewGroup,ev: MotionEvent):Boolean{
        for (index in 0 until viewGroup.childCount){
            val childView = viewGroup.getChildAt(index)
            if (childView is ViewGroup){
                val status = checkTouchContent(childView,ev)
                if (status)return true
            }else if (childView.tag == contentTag){
                val rectF = RectF(childView.getViewPosRect())
                if (rectF.contains(ev.x,ev.y)){
                    downView = childView
                    movePositionView = getMovePosition(childView)
                    return true
                }
            }
        }
        return false
    }

    /**
     * 获取最上层的view
     */
    private fun getMovePosition(view: View):ViewGroup?{
        if (view is ViewGroup){
            val parent = view.parent
            if (parent is ViewGroup){
                return if (parent != this.view){ getMovePosition(parent) }else{ parent }
            }

        }
        return null;
    }

    /**
     * 检查是否移动
     */
    private fun checkCanMove(ev: MotionEvent):Boolean{
        val downView = this.downView
        val movePositionView = this.movePositionView
        val windowView = this.windowView
        if (touchContent  && downView != null && movePositionView != null && (System.currentTimeMillis() - downTime) > 1500){
            touchMove = abs(ev.x - downX) > 20 || abs(ev.y - downY) > minCheckMove
            if (touchMove && windowView is ViewGroup){
                animView.layoutParams = animParams

                val oldParams = downView.layoutParams
                if (oldParams is ViewGroup.MarginLayoutParams){
                    this.oldParams = oldParams
                    val rectF = downView.getViewPosRect()
                    movePositionView.visibility = View.INVISIBLE
                    windowView.removeView(animView)
                    windowView.addView(animView)
                    val newLayoutParams = FrameLayout.LayoutParams(oldParams.width,oldParams.height)
                    newLayoutParams.leftMargin = rectF.left
                    newLayoutParams.topMargin = rectF.top
                    startParams = newLayoutParams
                    removeView(downView)
                    downView.layoutParams = newLayoutParams
                    animView.addView(downView)
                    movePauseIndex = -1
                    movePauseStartTime = System.currentTimeMillis()
                }
            }
        }
        return touchMove
    }

    /**
     * 移动view
     */
    private fun moveAnimView(ev: MotionEvent){
        val params = downView?.layoutParams
        val startParams = this.startParams
        if (startParams != null && params is ViewGroup.MarginLayoutParams){
            params.leftMargin = startParams.leftMargin + (ev.x - downX).toInt()
            params.topMargin = startParams.topMargin + ( ev.y - downY).toInt()
        }
    }

    /**
     * 移除view
     */
    private fun removeView(view: View?){
        val parent = view?.parent
        if (parent is ViewGroup){
            parent.removeView(view)
        }
    }

    /**
     * 检查移除制定view
     */
    private fun checkMoveView(ev: MotionEvent){
        if (moveAnimatorSet?.isRunning == true)return
        for (index in 0 until view.childCount){
            val childView = view.getChildAt(index)
            val contentView = childView.findViewWithTag<View>(contentTag)
            val rowSize = positionListener.onRowSize()
            val childRect = RectF(childView.getViewPosRect())


            val rectF = childRect.left - rowSize/2F
            val rectT = childRect.top
            val rectR = childRect.right + rowSize/2F
            val rectB = childRect.bottom
            childRect.set(rectF ,rectT,rectR,rectB)

            if (childRect.contains(ev.x,ev.y) && contentView != null){
                if (childView == movePositionView){
                    movePauseIndex = -1
                    movePauseStartTime = System.currentTimeMillis()
                    return
                }else{
                    if (movePauseIndex != index){
                        movePauseStartTime = System.currentTimeMillis()
                    }
                    if ((System.currentTimeMillis() - movePauseStartTime) >= 1500){
                        removeView(movePositionView)
                        startMoveAnim(index)
                        moveAnimatorSet?.start()
                        view.addView(movePositionView,index)
                        movePauseIndex = -1
                        movePauseStartTime = System.currentTimeMillis()
                    }else {
                        movePauseIndex = index
                    }
                    return
                }
            }else{
                movePauseIndex = -1
                movePauseStartTime = System.currentTimeMillis()
            }
            if (childView == movePositionView){

            }
        }
    }

    /**
     * 开始移动view
     */
    private fun startMoveAnim(takeIndex:Int){
        ViewAnimHelper.cancel(moveAnimatorSet)
        moveAnimatorSet = ViewAnimHelper.getAnimation()
        val build = ViewAnimHelper.getBuilder(moveAnimatorSet)
        for (index in 0 until view.childCount){
            val childView = view.getChildAt(index)
            val positionArray = positionListener.onPosition(index + (if (index >= takeIndex) 1 else 0))
            ViewAnimHelper.setMarginLeft(build,positionArray[0],childView)
            ViewAnimHelper.setMarginTop(build,positionArray[1],childView)
        }
    }

    /**
     * 处理事件抬起
     */
    private fun actionUp(){
        val downView = this.downView
        val downParams = downView?.layoutParams
        if (touchMove && downParams is ViewGroup.MarginLayoutParams){
            for (index in 0  until view.childCount){
                val childView = view.getChildAt(index)
                if (childView == movePositionView){
                    val positionRect = childView.getViewPosRect()
                    val leftMargin = positionRect.left + (oldParams?.leftMargin?:0)
                    val topMargin = positionRect.top + (oldParams?.topMargin?:0)
                    val downLeftMargin = downParams.leftMargin
                    val downTopMargin = downParams.topMargin
                    val leftD = abs(downLeftMargin - leftMargin)
                    val topD = abs(downTopMargin - topMargin)
                    val maxD = max(leftD,topD)
                    val isMin = leftD <= minAnimMove && topD <= minAnimMove
                    val time = if (isMin) 100L else (maxD/2L)
                    ViewAnimHelper.cancel(returnAnimatorSet)
                    returnAnimatorSet = ViewAnimHelper.getAnimation(time)
                    val build = ViewAnimHelper.getBuilder(returnAnimatorSet)
                    ViewAnimHelper.setMarginLeft(build,leftMargin,downView)
                    ViewAnimHelper.setMarginTop(build,topMargin,downView)
                    returnAnimatorSet?.start()
                    runMain({
                        removeView(downView)
                        movePositionView?.addView(downView)
                        val delView = movePositionView?.findViewWithTag<View>(delTag)
                        if (delView != null) {
                            removeView(delView)
                            movePositionView?.addView(delView)
                        }
                    },time)
                }
            }
        }
    }


    fun onDestroy(){
        ViewAnimHelper.cancel(moveAnimatorSet)
        ViewAnimHelper.cancel(returnAnimatorSet)
        removeView(animView)
    }
}