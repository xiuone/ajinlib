package com.xy.baselib.widget.shadow

import android.graphics.*
import android.graphics.Color.parseColor
import com.xy.baselib.widget.shadow.impl.ShadowBuilderImpl
import com.xy.baselib.exp.Logger

class ShadowPaint(private val builderImpl: ShadowBuilderImpl) : Paint(ANTI_ALIAS_FLAG) {
    private val builder: ShadowBuilder by lazy {  builderImpl.builder }
    override fun reset() {
        isAddAlpha()
        setGradient()
        color = builder.backgroundColor
        setShadowLayer(builder.mShadowLimit, 0F, 0F, builder.mShadowColor)
        isAntiAlias = true
    }

    /**
     * 是否需要添加透明度
     */
    private fun isAddAlpha(){
        if (Color.alpha(builder.mShadowColor) == 255) {
            var red = Integer.toHexString(Color.red(builder.mShadowColor))
            var green = Integer.toHexString(Color.green(builder.mShadowColor))
            var blue = Integer.toHexString(Color.blue(builder.mShadowColor))
            if (red.length == 1) {
                red = "0$red"
            }
            if (green.length == 1) {
                green = "0$green"
            }
            if (blue.length == 1) {
                blue = "0$blue"
            }
            var endColor = "#99$red$green$blue"
            var argbColor: Int = builder.mShadowColor
            try {
                if (!endColor.startsWith("#")) {
                    endColor = "#$endColor"
                }
                argbColor = parseColor(endColor)
            } catch (e: Exception) {
            }
            builder.mShadowColor = argbColor
        }
    }

    /**
     * 画笔渐变色
     */
    private fun setGradient() {
        shader = null
        if (builder.centerColor == builder.defaultBackgroundColor && builder.startColor == builder.defaultBackgroundColor && builder.endColor == builder.defaultBackgroundColor) return
        val colors: IntArray = if (builder.centerColor == builder.defaultBackgroundColor) {
            intArrayOf(builder.startColor, builder.endColor)
        } else {
            intArrayOf(builder.startColor, builder.centerColor, builder.endColor)
        }
        if (builder.angle < 0) {
            builder.angle = builder.angle % 360 + 360
        }
        //当设置的角度大于0的时候
        //这个要算出每隔45度
        val angleFlag: Int = builder.angle % 360 / 45
        val rectF: RectF = builderImpl.getRectF()
        val x_ = rectF.width() / 2 + rectF.left
        var linearGradient: LinearGradient? = null
        when (angleFlag) {
            0 -> linearGradient = LinearGradient(rectF.left, rectF.top, rectF.right, rectF.top, colors, null, Shader.TileMode.CLAMP)
            1 -> linearGradient = LinearGradient(rectF.left, rectF.bottom, rectF.right, rectF.top, colors, null, Shader.TileMode.CLAMP)
            2 -> linearGradient = LinearGradient(x_, rectF.bottom, x_, rectF.top, colors, null, Shader.TileMode.CLAMP)
            3 -> linearGradient = LinearGradient(rectF.right, rectF.bottom, rectF.left, rectF.top, colors, null, Shader.TileMode.CLAMP)
            4 -> linearGradient = LinearGradient(rectF.right, rectF.top, rectF.left, rectF.top, colors, null, Shader.TileMode.CLAMP)
            5 -> linearGradient = LinearGradient(rectF.right, rectF.top, rectF.left, rectF.bottom, colors, null, Shader.TileMode.CLAMP)
            6 -> linearGradient = LinearGradient(x_, rectF.top, x_, rectF.bottom, colors, null, Shader.TileMode.CLAMP)
            7 -> linearGradient = LinearGradient(rectF.left, rectF.top, rectF.right, rectF.bottom, colors, null, Shader.TileMode.CLAMP)
        }
        if (linearGradient != null) shader = linearGradient
    }

    init {
        isAntiAlias = true
        style = Style.FILL
    }
}