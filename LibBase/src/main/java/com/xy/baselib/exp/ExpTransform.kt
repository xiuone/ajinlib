package com.xy.baselib.exp

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * @description 方法的作用: 正则的方式隐藏中间4位手机号
 */
fun String.replacePhone(): String {
    return replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")
}

/**
 * @description 方法的作用: 正则的方式隐藏中间10位身份证号
 */
fun String.replaceIdCard(): String? {
    return replace("(\\d{4})\\d{10}(\\w{4})".toRegex(), "$1****$2")
}

/**
 * @description 方法的作用: 对某个数字保留两位小数
 */
fun Float.keepTwoDecimals(): String? {
    return String.format("%.2f", this)
}

/**
 * 将时间戳转换为时间
 */
fun Long.stampToDate(model:String): String? {
    val res: String
    val simpleDateFormat = SimpleDateFormat(model)
    val date = Date(this)
    res = simpleDateFormat.format(date)
    return res
}

/**
 * 给字符串某些关键字上色
 */
fun SpannableString.replaceContentColor(color: Int, keyword: String): SpannableString {
    findSpanPosition(toString(),keyword) { start, end ->
        this.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return this
}

/**
 * 给字符串某些关键字大小
 */
fun SpannableString.setContentSize(size: Int, keyword: String): SpannableString {
    findSpanPosition(toString(),keyword) { start, end ->
        this.setSpan(AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        this.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return this
}

/**
 * 给字符串某些关键字大小
 */
fun SpannableString.setStyleBold(keyword: String): SpannableString {
    return setStyle(Typeface.BOLD,keyword)
}

/**
 * 给字符串某些关键字大小
 */
fun SpannableString.setStyle(style:Int,keyword: String): SpannableString {
    findSpanPosition(toString(),keyword) { start, end ->
        this.setSpan(StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return this
}

/**
 * 让文字可以点击
 */
fun SpannableString.setContentClicked( keyword: String,l: () -> Unit):SpannableString{
    findSpanPosition(toString(),keyword) { start, end ->
        this.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
                l.invoke()
            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return this
}


private fun findSpanPosition(string: String,key:String,method:(start:Int,end:Int)->Unit){
    val string = string.toLowerCase()
    val key = key.toLowerCase()
    val pattern = Pattern.compile(key)
    val matcher = pattern.matcher(string)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        method(start,end)
    }
}

