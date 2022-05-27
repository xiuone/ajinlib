package com.xy.baselib.widget.common.text

import android.content.Context
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.xy.baselib.R

class DownTimerTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    CommonTextView(context, attrs, defStyleAttr){
    private val downTimer by lazy { DownTimer() }
    private var runString: String ="%s"
    private var finishString: String =""

    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownTimerTextView)
            finishString = typedArray.getString(R.styleable.DownTimerTextView_down_timer_end_string)?:""
            runString = typedArray.getString(R.styleable.DownTimerTextView_down_timer_run_string)?:""
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
}
