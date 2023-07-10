package xy.xy.base.widget.bar.progress

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View

class ProgressAnimHelper(private val view:View) :ValueAnimator.AnimatorUpdateListener{
    //高度进度
    var animValue = 0F
    private var openAnim: ValueAnimator?=null
    private var closeAnim: ValueAnimator?=null

    /**
     * 开启动画
     */
    fun startAnim(event: MotionEvent){
        closeAnim?.cancel()
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL){
            openAnim?.cancel()
            closeAnim = ObjectAnimator.ofFloat(animValue, 0f)
            closeAnim?.addUpdateListener (this)
            closeAnim?.duration = 200
            closeAnim?.start()
        }else  if(openAnim?.isRunning != true && animValue != 1F){
            openAnim = ObjectAnimator.ofFloat(animValue, 1f)
            openAnim?.addUpdateListener (this)
            openAnim?.duration = 200
            openAnim?.start()
        }
    }


    fun onDestroyed(){
        openAnim?.cancel()
        closeAnim?.cancel()
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        animValue = animation.animatedValue as Float
        view.invalidate()
    }
}