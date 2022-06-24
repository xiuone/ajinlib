package com.xy.baselib.widget.shadow

import android.graphics.Path
import android.graphics.RectF
import android.view.View
import com.xy.baselib.widget.shadow.impl.ShadowBuilderImpl
import kotlin.math.min

class ShadowPath(private val builderImpl: ShadowBuilderImpl, private val view: View) : Path() {
    private val builder: ShadowBuilder by lazy { builderImpl.builder }
    override fun reset() {
        super.reset()
        addRoundRect(getShadowRect())
    }

    private fun shadowRectLeft(): Float =  if(builder.isShowLeftShadow) builder.mShadowLimit else 0F
    private fun shadowRectTop(): Float =  if(builder.isShowTopShadow) builder.mShadowLimit else 0F
    private fun shadowRectRight(): Float =  if(builder.isShowRightShadow) (view.width - builder.mShadowLimit) else view.width.toFloat()
    private fun shadowRectBottom(): Float =  if(builder.isShowBottomShadow) (view.height - builder.mShadowLimit) else view.height.toFloat()


    fun getShadowRect() = RectF(shadowRectLeft(), shadowRectTop(), shadowRectRight(), shadowRectBottom())


    fun addRoundRect(rect: RectF) {
        super.addRoundRect(rect, getCornerValue(), Direction.CW)
    }

    /**
     * 获取圆角
     * @return
     */
    fun getCornerValue(): FloatArray{
        val maxRadius = min(view.height, view.height) / 2F
        var leftTop = builderImpl.leftTopRadius()
        var rightTop = builderImpl.rightTopRadius()
        var leftBottom = builderImpl.leftBottomRadius()
        var rightBottom = builderImpl.rightBottomRadius()
        leftTop = min(leftTop, maxRadius)
        rightTop = min(rightTop, maxRadius)
        rightBottom = min(rightBottom, maxRadius)
        leftBottom = min(leftBottom, maxRadius)
        return floatArrayOf(leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom) //左上，右上，右下，左下
    }
}