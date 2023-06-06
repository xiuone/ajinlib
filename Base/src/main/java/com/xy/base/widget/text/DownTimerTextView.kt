package com.xy.base.widget.text

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.xy.base.R

class DownTimerTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr){
    private var runString: String ="%s"
    private var finishString: String =""

    init {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownTimerTextView)
            finishString = typedArray.getString(R.styleable.DownTimerTextView_down_timer_end_string)?:""
            runString = typedArray.getString(R.styleable.DownTimerTextView_down_timer_run_string)?:"%s"
        }
    }

    fun onFinishTime(millisUntilFinished:Long){
        if (millisUntilFinished >0){
            text = String.format(runString,"${(millisUntilFinished / 1000)}s")
            isClickable = false //设置不可点击
            isSelected = false
        }else{
            text = finishString
            isClickable = true //重新获得点击
            isSelected = false
        }
    }
}
