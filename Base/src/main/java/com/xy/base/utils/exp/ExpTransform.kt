package com.xy.base.utils.exp

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.text.*
import android.text.style.*
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
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
fun Float.keepTwoDecimals(): String {
    return String.format("%.2f", this)
}
fun Double.keepTwoDecimals(): String {
    return String.format("%.2f", this)
}

/**
 * 将时间戳转换为时间
 */
fun Long.stampToDate(model:String?): String? {
    if (model == null)return ""
    val res: String
    val simpleDateFormat = SimpleDateFormat(model)
    val date = Date(this)
    res = simpleDateFormat.format(date)
    return res
}

/**
 * 给字符串某些关键字上色
 */
fun Editable?.replaceContentColorRule(color: Int, pattern: String?){
    val patternStr = this?.toString()?:return
    val r: Pattern = Pattern.compile(pattern)
    val m: Matcher = r.matcher(patternStr)
    while (m.find()) {
        replaceContentColor(m.start(), m.end(), color)
    }
}

/**
 * 给字符串某些关键字上色
 */
fun Editable.replaceContentColor(start: Int,end:Int, color: Int): Editable? {
    this?.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

/**
 * 给字符串某些关键修改成图片
 */
fun Editable.replaceContentImage(context:Context?,start: Int,end:Int,drawableRes:Int,width: Int = 0, height: Int = 0):Editable{
    if (context == null)return this
    var drawable = context.getResDrawable(drawableRes)?:return this
    if (width > 0 && height > 0 ){
        drawable = BitmapDrawable(drawable.resizeDrawable(width, height))
    }
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE) //将图片实例化为一个ImageSpan型
    this.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE) //将ImageSpan代替之前添加的[luckdrawlabel]字符串
    return this
}


/**
 * 给字符串某些关键字上色
 */
fun SpannableString.replaceContentColor(color: Int, keyword: String?): SpannableString {
    if (keyword == null)return this
    findSpanPosition(toString(),keyword) { start, end ->
        replaceContentColor(start, end, color)
    }
    return this
}
/**
 * 给字符串某些关键字上色
 */
fun SpannableString.replaceContentColor(start: Int,end:Int, color: Int): SpannableString {
    this.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

/**
 * 给字符串某些关键修改成图片
 */
fun SpannableString.replaceContentImage(context:Context?,start: Int,end:Int,drawableRes:Int,width: Int = 0, height: Int = 0):SpannableString{
    if (context == null)return this
    var drawable = context.getResDrawable(drawableRes)?:return this
    if (width > 0 && height > 0 ){
        drawable = BitmapDrawable(drawable.resizeDrawable(width, height))
    }
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE) //将图片实例化为一个ImageSpan型
    this.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE) //将ImageSpan代替之前添加的[luckdrawlabel]字符串
    return this
}

/**
 * 给字符串某些关键修改成图片
 */
fun SpannableString.replaceContentImage(context:Context?,keyword: String?,drawableRes:Int,width: Int = 0, height: Int = 0):SpannableString{
    if (context == null || keyword == null)return this
    findSpanPosition(keyword){start, end ->
        replaceContentImage(context,start, end, drawableRes, width, height)
    }
    return this
}



/**
 * 给字符串某些关键字大小
 */
fun SpannableString.setContentSize(size: Int, keyword: String): SpannableString {
    findSpanPosition(toString(),keyword) { start, end ->
        setContentSize(start, end, size)
    }
    return this
}
fun SpannableString.setContentSize(start: Int,end:Int, size: Int): SpannableString {
    this.setSpan(AbsoluteSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    this.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}


/**
 * 给字符串某些关键字大小
 */
fun SpannableString.setStyleBold(keyword: String?): SpannableString {
    if (keyword == null)return this
    return setStyle(Typeface.BOLD,keyword)
}

/**
 * 给字符串某些关键字大小
 */
fun SpannableString.setStyle(style:Int,keyword: String?): SpannableString {
    if (keyword == null)return this
    findSpanPosition(toString(),keyword) { start, end ->
        this.setSpan(StyleSpan(style), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return this
}

/**
 * 让文字可以点击
 */
fun SpannableString.setContentClicked( keyword: String?,l: () -> Unit):SpannableString{
    if (keyword == null)return this
    findSpanPosition(toString(),keyword) { start, end ->
        setContentClicked(start, end, l)
    }
    return this
}

/**
 * 让文字可以点击
 */
fun SpannableString.setContentClickedWithColor( keyword: String?,color:Int,l: () -> Unit):SpannableString{
    if (keyword == null)return this
    findSpanPosition(toString(),keyword) { start, end ->
        setContentClicked(start, end, l)
        replaceContentColor(start, end, color)
    }
    return this
}

/**
 * 让文字可以点击
 */
fun SpannableString.setContentClicked( start: Int,end:Int,l: () -> Unit):SpannableString{
    this.setSpan(object : ClickableSpan() {
        override fun onClick(p0: View) {
            l.invoke()
        }
        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false; // set to false to remove underline
        }
    }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

fun SpannableString.findSpanPosition(key:String?,method:(start:Int,end:Int)->Unit){
    findSpanPosition(toString(),key,method)
}


private fun findSpanPosition(string: String,key:String?,method:(start:Int,end:Int)->Unit){
    if (key != null) {
        val string = string.toLowerCase()
        val key = key.toLowerCase()
        val pattern = Pattern.compile(key)
        val matcher = pattern.matcher(string)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            method(start, end)
        }
    }
}

