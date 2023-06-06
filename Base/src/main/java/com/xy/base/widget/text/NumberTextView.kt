package com.xy.base.widget.text

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.xy.base.utils.exp.getNumber

class NumberTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    fun setNumber(number:Long,def:String? = null){
        text = if (number <= 0 ){ def }else{ number.getNumber() }
    }
}