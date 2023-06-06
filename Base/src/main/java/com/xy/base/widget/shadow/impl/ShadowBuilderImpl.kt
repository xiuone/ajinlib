package com.xy.base.widget.shadow.impl

import android.graphics.RectF
import com.xy.base.widget.shadow.ShadowArrow
import com.xy.base.widget.shadow.ShadowBuilder

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

    fun haveArrowSize() = builder.arrowWidth > 0 && builder.arrowHeight > 0
    fun arrowIsLeft() = builder.arrowWay == ShadowArrow.LeftBottom || builder.arrowWay == ShadowArrow.LeftTop
    fun arrowIsTop() = builder.arrowWay == ShadowArrow.TopLeft || builder.arrowWay == ShadowArrow.TopRight
    fun arrowIsRight() = builder.arrowWay == ShadowArrow.RightTop || builder.arrowWay == ShadowArrow.RightBottom
    fun arrowIsBottom() = builder.arrowWay == ShadowArrow.BottomRight || builder.arrowWay == ShadowArrow.BottomLeft

    fun arrowLeft() = if (haveArrowSize() && arrowIsLeft()) builder.arrowHeight else 0F
    fun arrowTop() = if (haveArrowSize() && arrowIsTop()) builder.arrowHeight else 0F
    fun arrowRight() = if (haveArrowSize() && arrowIsRight()) builder.arrowHeight else 0F
    fun arrowBottom() = if (haveArrowSize() && arrowIsBottom()) builder.arrowHeight else 0F


    fun shadowLeft() = if(builder.isShowLeftShadow) builder.mShadowLimit else 0F
    fun shadowTop() = if(builder.isShowTopShadow) builder.mShadowLimit else 0F
    fun shadowRight() = if(builder.isShowRightShadow) builder.mShadowLimit else 0F
    fun shadowBottom() = if(builder.isShowBottomShadow) builder.mShadowLimit else 0F

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