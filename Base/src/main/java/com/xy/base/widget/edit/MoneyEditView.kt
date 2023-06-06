package com.xy.base.widget.edit

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.xy.base.R
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern

class MoneyEditView (context: Context, attrs: AttributeSet): AppCompatEditText(context,attrs) , InputFilter {
    private var decimalLength:Int = 2
    private var maxNumber = Double.MAX_VALUE
    private val pattern: Pattern by lazy { Pattern.compile("([0-9]|\\.)*") }
    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MoneyEditView)
        decimalLength = array.getInt(R.styleable.MoneyEditView_decimal_length,2)
        array.recycle()
    }


    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
        if (source == null)return ""
        val oldest = dest.toString()
        if ("" == source.toString()) {
            return ""
        }
        val m: Matcher = pattern.matcher(source)
        if (oldest.contains(".")) {
            if (!m.matches()) {//已经存在小数点的情况下，只能输入数字
                return ""
            }
        } else {
            if (!m.matches()) {//已经存在小数点的情况下，只能输入数字
                return ""
            } else {
                if ("0" == source && "0" == oldest) {
                    return ""
                }
            }
            if ("." == source && TextUtils.isEmpty(oldest)) {
                return ""
            }
        }

        //验证小数位精度是否正确
        if (oldest.contains(".")) {
            val index = oldest.indexOf(".")
            val len = dend - index
            //小数位只能2位
            if (len > decimalLength) {
                return dest!!.subSequence(dstart, dend)
            }
        }
        return "${dest?.subSequence(dstart, dend)}$source.toString()"
    }


    fun setMaxNumber(maxNumber:Double){
        this.maxNumber = maxNumber
        var currentNumber = text.toString()
        if (!TextUtils.isEmpty(currentNumber)){
            try {
                val number = currentNumber.toDouble()
                if (maxNumber < number){
                    setText("$maxNumber")
                }
                return
            }catch (e:Exception){

            }
            setText("$maxNumber")
        }

    }

    fun setMaxNumber(str:String?){
        if (TextUtils.isEmpty(str))return
        try {
            val number = str?.toDouble()?:0.0
            setMaxNumber(number)
        }catch (e:Exception){

        }
    }
}