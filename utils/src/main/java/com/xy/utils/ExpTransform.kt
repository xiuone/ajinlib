package com.xy.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
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
fun String.repaceContentColor(color: Int, keyword: String): SpannableString {
    val string = toLowerCase()
    val key = keyword.toLowerCase()
    val pattern = Pattern.compile(key)
    val matcher = pattern.matcher(string)
    val ss = SpannableString(this)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        ss.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return ss
}

/**
 * 让文字可以点击
 */
fun String.setContentClicked( keyword: String,l: () -> Unit):SpannableString{
    val string = toLowerCase()
    val key = keyword.toLowerCase()
    val pattern = Pattern.compile(key)
    val matcher = pattern.matcher(string)
    val ss = SpannableString(this)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        ss.setSpan(object : ClickableSpan() {
            override fun onClick(p0: View) {
                l.invoke()
            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return ss
}

