package xy.xy.base.utils.config.font

import android.content.Context
import xy.xy.base.R
import xy.xy.base.utils.exp.getResString
import xy.xy.base.utils.exp.getSpFloat
import xy.xy.base.utils.exp.setSpFloat

class FontManger {
    private val FONT_SIZE_KEY = "FONT_SIZE_KEY"
    private val verySmallFontSize = 36f
    private val smallFontSize = 42f
    val middleFontSize = 48f
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
        var currentFontSize: Float = context.getSpFloat(FONT_SIZE_KEY, middleFontSize)
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
        if (currentFontSize == getFontScaleSize(context))return
        context.setSpFloat(FONT_SIZE_KEY, currentFontSize)
        if (context is ConfigChangeListener)
            context.onChangeConfig()
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

    companion object{
        val instant = FontManger()
    }
}