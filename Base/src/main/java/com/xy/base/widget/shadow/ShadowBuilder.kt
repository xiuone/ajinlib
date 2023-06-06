package com.xy.base.widget.shadow

import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.xy.base.R

class ShadowBuilder(var view: View, attrs: AttributeSet?) {
    //背景
    val defaultBackgroundColor = 0X00FFFFFF
    var defaultCornerRadius = 0f


    var stokeColor = 0X00FFFFFF
    var stokeSize = 0f

    //是否隐藏阴影
    var isShowShadow = false
    var isShowLeftShadow = false
    var isShowRightShadow = false
    var isShowBottomShadow = false
    var isShowTopShadow = false

    //圆角
    var mCornerRadius = 0f
    var mCornerRadiusLeftTop = 0f
    var mCornerRadiusLeftBottom = 0f
    var mCornerRadiusRightTop = 0f
    var mCornerRadiusRightBottom = 0f

    //扩散的宽度
    var mShadowLimit = 0f
    var mShadowColor = 0
    var backgroundColor = 0

    //关于控件填充渐变色
    var startColor = 0
    var centerColor = 0
    var endColor = 0
    var angle = 0

    var arrowHeight = 0F
    var arrowWidth = 0F
    var arrowOff = 0F
    var arrowIsCenter = true
    var arrowWay = ShadowArrow.NONO

    /*-------------------------------------------------设置偏移量----------------------------------------------------------------*/
    /**
     * 动态设置阴影扩散区域  最大偏移量
     * @param mShadowLimit
     */
    fun setShadowLimit(mShadowLimit: Int) {
        this.mShadowLimit = mShadowLimit.toFloat()
        isShowShadow = true
        isShowTopShadow = true
        isShowBottomShadow = true
        isShowLeftShadow = true
        isShowRightShadow = true
        if (mShadowLimit == 0) {
            isShowShadow = false
            isShowLeftShadow = false
            isShowRightShadow = false
            isShowTopShadow = false
            isShowBottomShadow = false
        }
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    fun setCurrentShowShadow(showShadow: Boolean) {
        isShowShadow = showShadow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    fun setCurrentShowLeftShadow(showLeftShadow: Boolean) {
        isShowLeftShadow = showLeftShadow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    fun setCurrentShowRightShadow(showRightShadow: Boolean) {
        isShowRightShadow = showRightShadow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    fun setCurrentShowBottomShadow(showBottomShadow: Boolean) {
        isShowBottomShadow = showBottomShadow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    fun setCurrentShowTopShadow(showTopShadow: Boolean) {
        isShowTopShadow = showTopShadow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }
    /*-------------------------------------------------设置偏移量END-------------------------------------------------------------*/ /*-------------------------------------------------设置隐藏----------------------------------------------------------------*/
    /**
     * 是否隐藏阴影
     * @param isShowShadow
     */
    fun setShadowHidden(isShowShadow: Boolean) {
        this.isShowShadow = !isShowShadow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    /**
     * 是否隐藏上面部分
     * @param topShow
     */
    fun setShadowHiddenTop(topShow: Boolean) {
        isShowTopShadow = !topShow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    /**
     * 是否隐藏下面部分
     * @param bottomShow
     */
    fun setShadowHiddenBottom(bottomShow: Boolean) {
        isShowBottomShadow = !bottomShow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    /**
     * 是否隐藏右边部分
     * @param rightShow
     */
    fun setShadowHiddenRight(rightShow: Boolean) {
        isShowRightShadow = !rightShow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    /**
     * 是否隐藏左边部分
     * @param leftShow
     */
    fun setShadowHiddenLeft(leftShow: Boolean) {
        isShowLeftShadow = !leftShow
        view.setPadding(0, 0, 0, 0)
        view.invalidate()
    }

    /**
     * 动态设置阴影颜色值
     * @param mShadowColor
     */
    fun setShadowColor(mShadowColor: Int) {
        this.mShadowColor = mShadowColor
        view.invalidate()
    }

    fun setCurrentBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        view.invalidate()
    }

    /*-------------------------------------------------设置偏移量END-------------------------------------------------------------*/ /*-------------------------------------------------设置圆角-------------------------------------------------------------*/
    /**
     * 设置角度
     * @param mCornerRadius
     */
    fun setCornerRadius(mCornerRadius: Float) {
        this.mCornerRadius = mCornerRadius
        mCornerRadiusLeftTop = mCornerRadius
        mCornerRadiusRightTop = mCornerRadius
        mCornerRadiusLeftBottom = mCornerRadius
        mCornerRadiusRightBottom = mCornerRadius
        view.invalidate()
    }

    /**
     * 设置特殊的圆角
     * @param leftTop
     * @param rightTop
     * @param leftBottom
     * @param rightBottom
     */
    fun setCornerRadius(leftTop: Int, rightTop: Int, leftBottom: Int, rightBottom: Int) {
        mCornerRadiusLeftTop = leftTop.toFloat()
        mCornerRadiusRightTop = rightTop.toFloat()
        mCornerRadiusLeftBottom = leftBottom.toFloat()
        mCornerRadiusRightBottom = rightBottom.toFloat()
        view.invalidate()
    }

    /*-------------------------------------------------设置圆角END-------------------------------------------------------------*/


//    var arrowHeight = 0F
//    var arrowWidth = 0F
//    var arrowOff = 0F
//    var arrowIsCenter = true
//    var arrowWay = ShadowArrow.NONO

    fun setArrow(arrowHeight:Float,arrowWidth:Float) = setArrow(arrowHeight,arrowWidth,arrowOff)

    fun setArrow(arrowHeight:Float,arrowWidth:Float,arrowOff:Float) = setArrow(arrowHeight,arrowWidth,arrowOff,arrowIsCenter)

    fun setArrow(arrowHeight:Float,arrowWidth:Float,arrowOff:Float,arrowIsCenter:Boolean) = setArrow(arrowHeight,arrowWidth,arrowOff,arrowIsCenter,arrowWay)

    fun setArrow(arrowHeight:Float,arrowWidth:Float,arrowOff:Float,arrowIsCenter:Boolean,arrowWay:ShadowArrow){
        this.arrowHeight = arrowHeight
        this.arrowWidth = arrowWidth
        this.arrowOff = arrowOff
        this.arrowIsCenter = arrowIsCenter
        this.arrowWay = arrowWay
        view.invalidate()
    }

    init {
        val attr = view.context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout)
        isShowShadow = attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHidden, false)
        isShowLeftShadow = attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHiddenLeft, !isShowShadow)
        isShowRightShadow = attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHiddenRight, !isShowShadow)
        isShowBottomShadow = attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHiddenBottom, !isShowShadow)
        isShowTopShadow = attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHiddenTop, !isShowShadow)

        defaultCornerRadius = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius, 0F)
        mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius, defaultCornerRadius)
        mCornerRadiusLeftTop = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius_leftTop, defaultCornerRadius)
        mCornerRadiusLeftBottom = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius_leftBottom, defaultCornerRadius)
        mCornerRadiusRightTop = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius_rightTop, defaultCornerRadius)
        mCornerRadiusRightBottom = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius_rightBottom, defaultCornerRadius)

        mShadowLimit = attr.getDimension(R.styleable.ShadowLayout_hl_shadowLimit, 0f)
        mShadowColor = attr.getColor(R.styleable.ShadowLayout_hl_shadowColor, 0X2A000000)

        backgroundColor = attr.getColor(R.styleable.ShadowLayout_hl_backgroundColor, Color.TRANSPARENT)

        stokeColor = attr.getColor(R.styleable.ShadowLayout_hl_stokeColor, Color.TRANSPARENT)
        stokeSize = attr.getDimension(R.styleable.ShadowLayout_hl_stokeSize, 0F)

        arrowHeight = attr.getDimension(R.styleable.ShadowLayout_hl_arrow_height, arrowHeight)
        arrowWidth = attr.getDimension(R.styleable.ShadowLayout_hl_arrow_width, arrowWidth)
        arrowOff = attr.getDimension(R.styleable.ShadowLayout_hl_arrow_off, arrowOff)
        arrowIsCenter = attr.getBoolean(R.styleable.ShadowLayout_hl_arrow_is_center, arrowIsCenter)
        val arrowWay = attr.getInt(R.styleable.ShadowLayout_hl_arrow_direction, 0)
        this.arrowWay = when(arrowWay){
            ShadowArrow.LeftTop.type-> ShadowArrow.LeftTop
            ShadowArrow.LeftBottom.type-> ShadowArrow.LeftBottom
            ShadowArrow.RightTop.type-> ShadowArrow.RightTop
            ShadowArrow.RightBottom.type-> ShadowArrow.RightBottom
            ShadowArrow.TopLeft.type-> ShadowArrow.TopLeft
            ShadowArrow.TopRight.type-> ShadowArrow.TopRight
            ShadowArrow.BottomLeft.type-> ShadowArrow.BottomLeft
            ShadowArrow.BottomRight.type-> ShadowArrow.BottomRight
            else-> ShadowArrow.NONO
        }

        //x轴偏移量
        if (mShadowLimit == 0f) {
            isShowShadow = false
            isShowLeftShadow = false
            isShowRightShadow = false
            isShowTopShadow = false
            isShowBottomShadow = false
        }
        startColor = attr.getColor(R.styleable.ShadowLayout_hl_startColor, defaultBackgroundColor)
        centerColor = attr.getColor(R.styleable.ShadowLayout_hl_centerColor, startColor)
        endColor = attr.getColor(R.styleable.ShadowLayout_hl_endColor, startColor)
        angle = attr.getInt(R.styleable.ShadowLayout_hl_angle, 0)
        angle = if (angle % 45 != 0) 0 else angle
        attr.recycle()
    }
}