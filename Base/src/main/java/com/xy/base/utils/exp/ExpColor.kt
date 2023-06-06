package com.xy.base.utils.exp

import android.graphics.Color
import java.util.*


/** 根据百分比改变颜色透明度 */
fun  Int.addAlpha(fraction:Float):Int {
    val newFraction = if (fraction > 1) 1F else fraction
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    val alpha = (Color.alpha(this) * newFraction).toInt()
    return Color.argb(alpha, red, green, blue)
}


/**
 * 是否需要添加透明度
 */
fun Int.addAlpha(defaultAlpha:String) :Int{
    var argbColor: Int = this
    if (Color.alpha(this) == 255) {
        var red = Integer.toHexString(Color.red(this))
        var green = Integer.toHexString(Color.green(this))
        var blue = Integer.toHexString(Color.blue(this))
        if (red.length == 1) {
            red = "0$red"
        }
        if (green.length == 1) {
            green = "0$green"
        }
        if (blue.length == 1) {
            blue = "0$blue"
        }
        try {
            var endColor = "#$defaultAlpha$red$green$blue"
            if (!endColor.startsWith("#")) {
                endColor = "#$endColor"
            }
            argbColor = Color.parseColor(endColor.uppercase(Locale.getDefault()))
        } catch (e: Exception) { }
    }
    return argbColor
}


fun Int.getColorList():IntArray{
    val colors = IntArray(this + 1)
    val colorAngleStep = 360 / this
    val hsv = floatArrayOf(0f, 1f, 1f)
    for (i in colors.indices) {
        hsv[0] = (i * colorAngleStep % 360).toFloat()
        if (hsv[0] == 360F)
            hsv[0] = 359F
        colors[i] = Color.HSVToColor(hsv)
    }
    return colors
}


/**
 * 根据主题色获取 imgage的颜色  只能用于纯色
 * @param color
 * @return
 */
fun Int.getRelativeThemeColor(): Int {
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    val bright = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (bright >= 200) Color.BLACK else Color.WHITE
}

fun String.parseColor(default:Int) :Int{
    try {
        return Color.parseColor(this)
    }catch (e:java.lang.Exception){
        com.xy.base.utils.Logger.e("======parseColor:Error$this")
    }
    return default
}