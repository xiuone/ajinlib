package com.xy.baselib.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object TransformUtils {

    /**
     * 将时间戳转换为时间
     */
    fun stampToDate(time: Long,model:String): String? {
        val res: String
        val simpleDateFormat = SimpleDateFormat(model)
        val date = Date(time)
        res = simpleDateFormat.format(date)
        return res
    }

    /**
     * 给字符串某些关键字上色
     */
    fun changeContentColor(color: Int, text: String, keyword: String): SpannableString {
        val string = text.toLowerCase()
        val key = keyword.toLowerCase()
        val pattern = Pattern.compile(key)
        val matcher = pattern.matcher(string)
        val ss = SpannableString(text)
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
    fun setContentClicked(text: String, keyword: String,l: () -> Unit):SpannableString{
        val string = text.toLowerCase()
        val key = keyword.toLowerCase()
        val pattern = Pattern.compile(key)
        val matcher = pattern.matcher(string)
        val ss = SpannableString(text)
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


    /**
     * @description 方法的作用: 正则的方式隐藏中间4位手机号
     */
    fun replacePhone(phone: String): String? {
        return phone.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")
    }

    /**
     * @description 方法的作用: 正则的方式隐藏中间10位身份证号
     */
    fun replaceIdCard(idCard: String): String? {
        return idCard.replace("(\\d{4})\\d{10}(\\w{4})".toRegex(), "$1****$2")
    }

    /**
     * @description 方法的作用: 对某个数字保留两位小数
     */
    fun keepTwoDecimals(num: Float): String? {
        return String.format("%.2f", num)
    }
}