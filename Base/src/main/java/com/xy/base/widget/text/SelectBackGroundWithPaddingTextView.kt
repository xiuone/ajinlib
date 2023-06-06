package com.xy.base.widget.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.xy.base.R
import kotlin.math.min

class SelectBackGroundWithPaddingTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    private var selectBackgroundUn = Color.TRANSPARENT
    private var selectBackgroundUnIsStoke = true
    private var selectBackgroundUnStokeSize = 0

    private var selectBackground = Color.TRANSPARENT
    private var selectBackgroundIsStoke = true
    private var selectBackgroundStokeSize = 0

    private var selectRadius = 0

    init {
        setBackgroundColor(Color.TRANSPARENT)
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectBackGroundWithPaddingTextView)
            selectBackgroundUn = typedArray.getColor(R.styleable.SelectBackGroundWithPaddingTextView_select_back_ground_un,Color.TRANSPARENT)
            selectBackgroundUnIsStoke = typedArray.getBoolean(R.styleable.SelectBackGroundWithPaddingTextView_select_back_ground_un_is_stoke,true)
            selectBackgroundUnStokeSize = typedArray.getDimensionPixelSize(R.styleable.SelectBackGroundWithPaddingTextView_select_back_ground_un_stoke_size,0)
            selectBackground = typedArray.getColor(R.styleable.SelectBackGroundWithPaddingTextView_select_back_ground,Color.TRANSPARENT)
            selectBackgroundIsStoke = typedArray.getBoolean(R.styleable.SelectBackGroundWithPaddingTextView_select_back_ground_is_stoke,true)
            selectBackgroundStokeSize = typedArray.getDimensionPixelSize(R.styleable.SelectBackGroundWithPaddingTextView_select_back_ground_stoke_size,0)
            selectRadius = typedArray.getDimensionPixelSize(R.styleable.SelectBackGroundWithPaddingTextView_select_back_ground_radius,0)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        val rectF = getDrawRectF(if (isSelected) selectBackgroundStokeSize else selectBackgroundUnStokeSize)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        if (!isSelected){
            paint.color = selectBackgroundUn
            if (selectBackgroundUnIsStoke){
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = selectBackgroundUnStokeSize.toFloat()
            }else{
                paint.style = Paint.Style.FILL
            }
        }else{
            paint.color = selectBackground
            if (selectBackgroundIsStoke){
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = selectBackgroundStokeSize.toFloat()
            }else{
                paint.style = Paint.Style.FILL
            }
        }
        val radius = getCurrentRadius(rectF)
        canvas?.drawRoundRect(rectF,radius,radius,paint)
        super.onDraw(canvas)

    }


    private fun getDrawRectF(stoke:Int):RectF{
        val left = paddingLeft.toFloat() + stoke/2
        val right = width - paddingRight.toFloat() - stoke/2
        val top = paddingTop.toFloat()+stoke/2
        val bottom = height - paddingBottom.toFloat()-stoke/2
        return RectF(left,top,right,bottom)
    }


    private fun getCurrentRadius(rectF: RectF) :Float{
        return min(selectRadius*2F, min(rectF.width(),rectF.height()))/2
    }

}