package com.xy.baselib.widget.tab.anim

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.OvershootInterpolator
import com.xy.baselib.R
import com.xy.baselib.exp.getResDimension
import com.xy.baselib.exp.getViewPosRect

class TabAnimHelper (private val listener:TabAnimUpdateListener)
    :ValueAnimator.AnimatorUpdateListener{
    private var valueAnim : ValueAnimator?= null
    private var valueAnimStartX = 0F
    private var valueAnimStartY = 0F
    private var valueAnimStartWidth = 0F
    private var valueAnimStartHeight = 0F
    private var valueAnimTagetX = 0F
    private var valueAnimTagetY = 0F
    private var valueAnimTagetWidth = 0F
    private var valueAnimTagetHeight = 0F

    private var valueAnimCenterX = 0F
    private var valueAnimCenterY = 0F
    private var valueAnimWidth = 0F
    private var valueAnimHeight = 0F


    fun setSelect(view:View?,fatherView: View){
        view?:return
        valueAnim?.cancel()

        val rect = view.getViewPosRect()
        val fatherRect = fatherView.getViewPosRect()

        valueAnimTagetX = rect.left + rect.width()/2F - fatherRect.left
        valueAnimTagetY = rect.top + rect.height()/2F - fatherRect.top
        valueAnimTagetWidth = rect.width().toFloat()
        valueAnimTagetHeight = rect.height().toFloat()
        if (valueAnimCenterX < valueAnimTagetX) {
            valueAnimStartX = valueAnimTagetX - view.context.getResDimension(R.dimen.dp_10)
        }else if (valueAnimCenterX > valueAnimTagetX){
            valueAnimStartX = valueAnimTagetX + view.context.getResDimension(R.dimen.dp_10)
        }
        valueAnimStartY = valueAnimTagetY
        valueAnimStartWidth = valueAnimWidth
        valueAnimStartHeight = valueAnimHeight


        valueAnim = ObjectAnimator.ofFloat(0F,1F)
        valueAnim?.duration = 300L
        valueAnim?.addUpdateListener (this)
        valueAnim?.interpolator = OvershootInterpolator(3F)
        valueAnim?.start()
    }

    fun cancel(){
        valueAnim?.cancel()
    }


    fun setTargetView(view: View?,fatherView: View){
        view?:return
        val rect = view.getViewPosRect()
        val fatherRect = fatherView.getViewPosRect()
        valueAnimTagetX = rect.left + rect.width()/2F - fatherRect.left
        valueAnimTagetY = rect.top + rect.height()/2F - fatherRect.top
        valueAnimTagetWidth = rect.width().toFloat()
        valueAnimTagetHeight = rect.height().toFloat()
        listener.onTabAnimationUpdate(1F,valueAnimTagetX,valueAnimTagetY,valueAnimWidth,valueAnimHeight)
    }


    override fun onAnimationUpdate(animation: ValueAnimator?) {
        animation?.run {
            val valueAnimProgress =  animatedValue as Float
            valueAnimCenterX = valueAnimStartX + (valueAnimTagetX - valueAnimStartX) * valueAnimProgress
            valueAnimCenterY = valueAnimStartY + (valueAnimTagetY - valueAnimStartY) * valueAnimProgress
            valueAnimWidth = valueAnimStartWidth + (valueAnimTagetWidth - valueAnimStartWidth) * valueAnimProgress
            valueAnimHeight = valueAnimStartHeight + (valueAnimTagetHeight - valueAnimStartHeight) * valueAnimProgress
            listener.onTabAnimationUpdate(valueAnimProgress,valueAnimCenterX,valueAnimCenterY,valueAnimWidth,valueAnimHeight)
        }
    }
}