package com.xy.base.utils.exp

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import com.xy.base.listener.AppTextWatcher

fun EditText?.addAppTextChangedListener(textWatcher:AppTextWatcher?){
    this?.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            textWatcher?.beforeTextChanged(s,start, count,after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textWatcher?.onTextChanged(s,start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            textWatcher?.afterTextChanged(s)
        }
    })
}

fun EditText?.bindInputNumber(textView: TextView?, max:Int){
    val length = this?.text.toString().length
    textView?.text = "${length}/$max"
    this?.addAppTextChangedListener(object :AppTextWatcher{
        override fun afterTextChanged(editable: Editable?) {
            if (editable == null)return
            val length = editable.length
            if (length > max){
                editable.delete(max, editable.length)
                textView?.text = "$max/$max"
                return
            }
            textView?.text = "${length}/$max"
        }
    })
}


fun EditText?.bindInputNumberByte(textView: TextView, max:Int){
    textView.text = "${stringLength(this?.text?.toString()?:"")}/$max"
    this?.addAppTextChangedListener(object :AppTextWatcher{
        override fun afterTextChanged(editable: Editable?) {
            editable?:return
            delete(editable, max)
            textView.text = "${stringLength(editable.toString())}/$max"
        }
    })
}


private fun delete(editable: Editable,max: Int){
    if (stringLength(editable.toString())>max) {
        editable.delete(editable.length - 1, editable.length)
        delete(editable, max)
    }
}

private fun stringLength(value: String): Int {
    var length = 0
    val chinese = "[\u4e00-\u9fa5]"
    for (i in value.indices) {
        val temp = value.substring(i, i + 1)
        length += if (temp.matches(chinese.toRegex())) { 2 } else { 1 }
    }
    return length
}