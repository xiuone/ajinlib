package com.xy.base.widget.image

import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.xy.base.R
import kotlin.math.max
import kotlin.math.min

class RoundBuild(private val view:View,private val attrs: AttributeSet? = null) {
    private val context by lazy { view.context }
    //左上角圆角
    var leftTopRadius = 0
    //左下角圆角
    var leftBottomRadius = 0
    //右上角圆角
    var rightTopRadius = 0
    //右下角圆角
    var rightBottomRadius = 0
    //边框线颜色
    var frameColor = Color.TRANSPARENT
    //边框线大小
    var frameSize = 0
    //显示左上角圆角
    var showLeftTopRound = true
    //显示左下角圆角
    var showLeftBottomRound = true
    //显示右上角圆角
    var showRightTopRound = true
    //显示右下角圆角
    var showRightBottomRound = true
    //背景颜色
    var backgroundColor = Color.TRANSPARENT

    var showShaow = false

    fun init(){
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundBaseImageView)
        showShaow = typedArray.getBoolean(R.styleable.RoundBaseImageView_round_image_show_shadow,false)

        frameColor = typedArray.getColor(R.styleable.RoundBaseImageView_round_image_frame_color, Color.TRANSPARENT)
        frameSize = typedArray.getDimensionPixelOffset(R.styleable.RoundBaseImageView_round_image_frame_size, frameSize)

        showLeftTopRound = typedArray.getBoolean(R.styleable.RoundBaseImageView_round_image_show_left_top,showLeftTopRound)
        showLeftBottomRound = typedArray.getBoolean(R.styleable.RoundBaseImageView_round_image_show_left_bottom,showLeftBottomRound)
        showRightTopRound = typedArray.getBoolean(R.styleable.RoundBaseImageView_round_image_show_right_top,showRightTopRound)
        showRightBottomRound = typedArray.getBoolean(R.styleable.RoundBaseImageView_round_image_show_right_bottom,showRightBottomRound)

        var radius = typedArray.getDimensionPixelOffset(R.styleable.RoundBaseImageView_round_image_radius, 0)
        if (radius <= 0 )
            radius = 0
        leftTopRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundBaseImageView_round_image_radius_left_top, radius)
        leftBottomRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundBaseImageView_round_image_radius_left_bottom, radius)
        rightTopRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundBaseImageView_round_image_radius_right_top, radius)
        rightBottomRadius = typedArray.getDimensionPixelOffset(R.styleable.RoundBaseImageView_round_image_radius_right_bottom, radius)
        backgroundColor = typedArray.getColor(R.styleable.RoundBaseImageView_round_image_background_color, backgroundColor)
        typedArray.recycle()
    }


    fun leftTopRadius() = if (showLeftTopRound) leftTopRadius.toFloat() else 0F

    fun leftBottomRadius() = if (showLeftBottomRound) leftBottomRadius.toFloat() else 0F

    fun rightTopRadius() = if (showRightTopRound) rightTopRadius.toFloat() else 0F

    fun rightBottomRadius() = if (showRightBottomRound) rightBottomRadius.toFloat() else 0F


    fun getViewMinSize() = min(view.width - view.paddingLeft - view.paddingRight, view.height - view.paddingTop - view.paddingBottom)
    fun getViewMinAllSize() = min(view.width , view.height)

    /**
     * 获取圆角
     */
    fun getCornerValue(): FloatArray{
        var leftTop = leftTopRadius()
        var leftBottom = leftBottomRadius()
        var rightTop = rightTopRadius()
        var rightBottom = rightBottomRadius()
        return floatArrayOf(leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom) //左上，右上，右下，左下
    }

    /**
     * 回去绘制路劲
     */
    fun getPath(stokeWidth:Float): Path {
        val path = Path()
        val left = view.paddingLeft+stokeWidth/2
        val top = view.paddingTop+stokeWidth/2

        val right = view.width - (stokeWidth/2) - view.paddingRight
        val bottom = view.height - (stokeWidth/2) - view.paddingBottom
        val rectF = RectF(left,top, right, bottom)
        path.addRoundRect(rectF,getCornerValue(), Path.Direction.CW)
        return path
    }

    fun getAllPath(stokeWidth:Float): Path {
        val path = Path()
        val left = stokeWidth/2
        val top = stokeWidth/2

        val right = view.width - (stokeWidth/2)
        val bottom = view.height - (stokeWidth/2)
        val rectF = RectF(left,top, right, bottom)
        path.addRoundRect(rectF,getCornerValue(), Path.Direction.CW)
        return path
    }

    fun getFrameSize() = max(0F, min(getViewMinSize()/2F,frameSize.toFloat()))


    fun setAllRadius(radius:Int):RoundBuild{
        if (radius != leftTopRadius || radius != leftBottomRadius || radius != rightTopRadius || radius != rightBottomRadius) {
            this.leftTopRadius = radius
            this.leftBottomRadius = radius
            this.rightTopRadius = radius
            this.rightBottomRadius = radius
            view.postInvalidateDelayed(10)
        }
        return this
    }

    fun showLeftBottomRound(status:Boolean = true):RoundBuild{
        if (status != showLeftTopRound) {
            showLeftBottomRound = status
            view.postInvalidateDelayed(10)
        }
        return this
    }

    fun showLeftTopRound(status:Boolean = true):RoundBuild{
        if (status != showLeftTopRound) {
            showLeftTopRound = status
            view.postInvalidateDelayed(10)
        }
        return this
    }

    fun showRightBottomRound(status:Boolean = true):RoundBuild{
        if (status != showRightBottomRound) {
            showRightBottomRound = status
            view.postInvalidateDelayed(10)
        }
        return this
    }
    fun showRightTopRound(status:Boolean = true):RoundBuild{
        if (status != showRightTopRound) {
            showRightTopRound = status
            view.postInvalidateDelayed(10)
        }
        return this
    }

    fun setFrame(frameSize:Int,color:Int){
        this.frameSize = frameSize
        this.frameColor = color
        view.invalidate()
    }

    fun setShadowStatus(show:Boolean){
        if (show == showShaow)return
        this.showShaow = show
        view.postInvalidateDelayed(10)
    }
}