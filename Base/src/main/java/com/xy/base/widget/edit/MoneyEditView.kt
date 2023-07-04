package com.xy.base.widget.edit

import android.content.Context
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.xy.base.R
import java.util.regex.Matcher
import java.util.regex.Pattern


class MoneyEditView (context: Context, attrs: AttributeSet): AppCompatEditText(context,attrs) , InputFilter {
    private var POINTER_LENGTH:Int = 2
    private var MAX_VALUE = Double.MAX_VALUE
    private val POINTER by lazy { "." }
    private val ZERO by lazy { "0" }
    private val pattern: Pattern by lazy { Pattern.compile("([0-9]|\\.)*") }

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.MoneyEditView)
        POINTER_LENGTH = array.getInt(R.styleable.MoneyEditView_decimal_length,2)
        array.recycle()
        filters = arrayOf(this)
    }


    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
        val sourceText = source?.toString()?:""
        val destText = dest?.toString()?:""

        if (TextUtils.isEmpty(sourceText)) {
            return "";
        }
        val matcher: Matcher = pattern.matcher(source)
        if(destText.contains(POINTER)) {//已经输入小数点的情况下，只能输入数字
            if (!matcher.matches() || POINTER == source.toString()) {
                return "";
            }
            //验证小数点精度，保证小数点后只能输入两位
            val index = destText.indexOf(POINTER);
            val length = dend - index;
            if (length > POINTER_LENGTH) {
                return dest?.subSequence(dstart, dend)?:""
            }
        }else {
            val isStartPointer = (POINTER == source.toString()) && TextUtils.isEmpty(destText)
            val isStartPointer2 = POINTER != source.toString() && ZERO == destText
            if (!matcher.matches() || isStartPointer || isStartPointer2) {
                return "";
            }
        }
        val sumText = "$destText$sourceText".toDouble()
        if (sumText > MAX_VALUE) {
            return dest?.subSequence(dstart, dend)?:""
        }

        return "${dest?.subSequence(dstart, dend)}$sourceText";
    }


    fun setMaxNumber(maxNumber:Double){
        this.MAX_VALUE = maxNumber
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