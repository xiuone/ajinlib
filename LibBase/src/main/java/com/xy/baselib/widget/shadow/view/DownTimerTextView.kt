package com.xy.baselib.widget.shadow.view

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import com.xy.baselib.R

class DownTimerTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    ShadowTextView(context, attrs, defStyleAttr){
    private val downTimer by lazy { DownTimer() }
    private var runString: String ="%s"
    private var finishString: String =""

    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownTimerTextView)
            finishString = typedArray.getString(R.styleable.DownTimerTextView_down_timer_end_string)?:""
            runString = typedArray.getString(R.styleable.DownTimerTextView_down_timer_run_string)?:"%s"
        }
    }

    fun run(){
        downTimer.start()
    }


    inner class DownTimer :CountDownTimer(60000, 1000){
        override fun onTick(millisUntilFinished: Long) {
            text = String.format(runString,"${(millisUntilFinished / 1000)}s")
            isClickable = false //设置不可点击
            isSelected = false
        }

        override fun onFinish() {
            text = finishString
            isClickable = true //重新获得点击
            isSelected = false
        }
    }

    fun onDestroy(){
        downTimer.cancel()
    }
}
