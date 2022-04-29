package com.xy.utils

import android.graphics.Color

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
            argbColor = Color.parseColor(endColor.toUpperCase())
        } catch (e: Exception) { }
    }
    return argbColor
}