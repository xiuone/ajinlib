package com.xy.baselib.widget.shadow.impl

import android.graphics.RectF
import com.infinitybrowser.mobile.widget.shadow.ArrowDirection.BottomLeft
import com.infinitybrowser.mobile.widget.shadow.ArrowDirection.BottomRight
import com.infinitybrowser.mobile.widget.shadow.ArrowDirection.LeftBottom
import com.infinitybrowser.mobile.widget.shadow.ArrowDirection.LeftTop
import com.infinitybrowser.mobile.widget.shadow.ArrowDirection.RightBottom
import com.infinitybrowser.mobile.widget.shadow.ArrowDirection.RightTop
import com.infinitybrowser.mobile.widget.shadow.ArrowDirection.TopLeft
import com.infinitybrowser.mobile.widget.shadow.ArrowDirection.TopRight
import com.xy.baselib.widget.shadow.ShadowBuilder

class ShadowBuilderImpl(val builder: ShadowBuilder) {

    /*-----------------------获取圆角---------------------------------------------------------------------------*/
    fun isNoNoRadius(): Boolean = (builder.mCornerRadius == 0F) &&
            (builder.mCornerRadiusLeftTop == 0F) &&
            (builder.mCornerRadiusLeftBottom == 0F) &&
            (builder.mCornerRadiusRightTop == 0F) &&
            (builder.mCornerRadiusRightBottom == 0F)
    fun isCommonLeftTopRadius(): Boolean =  builder.mCornerRadiusLeftTop == builder.defaultCornerRadius
    fun isCommonLeftBottomRadius(): Boolean = builder.mCornerRadiusLeftBottom == builder.defaultCornerRadius
    fun isCommonRightTopRadius(): Boolean = builder.mCornerRadiusRightTop == builder.defaultCornerRadius
    fun isCommonRightBottomRadius(): Boolean = builder.mCornerRadiusRightBottom == builder.defaultCornerRadius
    fun leftTopRadius(): Float = if (isCommonLeftTopRadius()) builder.mCornerRadius else builder.mCornerRadiusLeftTop
    fun rightTopRadius(): Float = if (isCommonRightTopRadius()) builder.mCornerRadius else builder.mCornerRadiusRightTop
    fun leftBottomRadius(): Float = if (isCommonLeftBottomRadius()) builder.mCornerRadius else builder.mCornerRadiusLeftBottom
    fun rightBottomRadius(): Float = if (isCommonRightBottomRadius()) builder.mCornerRadius else builder.mCornerRadiusRightBottom
    /**
     *
     * / **
     * 获取正文的显示区域
     * @return
     */
    fun getRectF(): RectF{
        val rectF = RectF()
        val leftPadding: Int = builder.view.paddingLeft
        val topPadding: Int = builder.view.paddingTop
        val rightPadding: Int = builder.view.paddingRight
        val bottomPadding: Int = builder.view.paddingBottom
        rectF.left = leftPadding.toFloat()
        rectF.top = topPadding.toFloat()
        rectF.right = (builder.view.width - rightPadding).toFloat()
        rectF.bottom = (builder.view.height - bottomPadding).toFloat()
        return rectF
    }

}