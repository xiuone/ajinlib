package com.xy.baselib.widget.progress

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View

class ProgressAnimHelper(private val view:View) {
    //高度进度
    var animValue = 0F
    private var openAnim: ValueAnimator?=null
    private var closeAnim: ValueAnimator?=null

    /**
     * 开启动画
     */
    public fun startAnim(event: MotionEvent){
        closeAnim?.cancel()
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL){
            openAnim?.cancel()
            closeAnim = ObjectAnimator.ofFloat(animValue, 0f)
            closeAnim?.addUpdateListener { animation: ValueAnimator ->
                animValue = animation.animatedValue as Float
                view.invalidate()
            }
            closeAnim?.duration = 200
            closeAnim?.start()
        }else  if(openAnim?.isRunning != true && animValue != 1F){
            openAnim = ObjectAnimator.ofFloat(animValue, 1f)
            openAnim?.addUpdateListener { animation: ValueAnimator ->
                animValue = animation.animatedValue as Float
                view.invalidate()
            }
            openAnim?.duration = 200;
            openAnim?.start()
        }
    }


    public fun onDestroyed(){
        openAnim?.cancel()
        closeAnim?.cancel()
    }
}