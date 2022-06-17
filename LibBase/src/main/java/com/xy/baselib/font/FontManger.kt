package com.xy.baselib.font

import android.content.Context
import com.xy.baselib.BaseApp
import com.xy.baselib.R
import com.xy.baselib.config.BaseObject
import com.xy.baselib.exp.getResString

class FontManger {
    private val FONT_SIZE_KEY = "FONT_SIZE_KEY"
    private val verySmallFontSize = 36f
    private val smallFontSize = 42f
    private val middleFontSize = 48f
    private val maxFontSize = 54f
    private val veryMaxFontSize = 72f

    /**
     * 获取当前 设置的字体大小
     */
    fun getFontScaleSize(context: Context): Float{
        val currentFontSize = getFontTextSize(context)
        return currentFontSize / middleFontSize
    }

    fun getWebTextZoom(context: Context): Int{
        return (getFontScaleSize(context)*100).toInt()
    }

    /**
     * 获取当前 设置的字体大小
     */
    fun getFontTextSize(context: Context): Float{
        var currentFontSize: Float = BaseObject.spHelperUtils.getFloat(context,FONT_SIZE_KEY, middleFontSize)
        if (currentFontSize != verySmallFontSize
            && currentFontSize != smallFontSize
            && currentFontSize != middleFontSize
            && currentFontSize != maxFontSize
            && currentFontSize != veryMaxFontSize) {
            currentFontSize = middleFontSize
        }
        return currentFontSize
    }

    /**
     * 设置当前选中的字体大小
     */
    fun setFontSize(context: Context,textSize: Float) {
        var currentFontSize = textSize
        if (currentFontSize != verySmallFontSize
            && currentFontSize != smallFontSize
            && currentFontSize != middleFontSize
            && currentFontSize != maxFontSize
            && currentFontSize != veryMaxFontSize) {
            currentFontSize = middleFontSize
        }
        BaseObject.spHelperUtils.setFloat(context,FONT_SIZE_KEY, currentFontSize)
        val applicationContext = context.applicationContext
        if (applicationContext is BaseApp)
            applicationContext.updateConfiguration()
    }

    fun getFonts(context: Context): MutableList<FontMode>{
        val fontModes = ArrayList<FontMode>()
        fontModes.add(FontMode(context.getResString(R.string.font_very_small), verySmallFontSize))
        fontModes.add(FontMode(context.getResString(R.string.font_small), smallFontSize))
        fontModes.add(FontMode(context.getResString(R.string.font_middle), middleFontSize))
        fontModes.add(FontMode(context.getResString(R.string.font_max), maxFontSize))
        fontModes.add(FontMode(context.getResString(R.string.font_very_max), veryMaxFontSize))
        return fontModes
    }
}