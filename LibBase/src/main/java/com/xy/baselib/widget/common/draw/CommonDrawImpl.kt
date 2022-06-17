package com.xy.baselib.widget.common.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.xy.baselib.R
import com.xy.baselib.exp.Logger
import com.xy.baselib.exp.getResColor
import com.xy.baselib.exp.getResDimension

open class CommonDrawImpl(protected val view: View, private val context: Context) {
    var stokeColor = context.getResColor(R.color.transparent)
        set(value) {
            if (pressStokeColor == stokeColor)
                pressStokeColor = value
            field = value
            view.invalidate()
        }
    var focusStokeColor = context.getResColor(R.color.transparent)
        set(value) {
            field = value
            view.invalidate()
        }
    var pressStokeColor = context.getResColor(R.color.transparent)

    var stokeSize = context.getResDimension(R.dimen.dp_0_8).toFloat()
        set(value) {
            field = value
            view.invalidate()
        }

    var commonBackgroundColor = context.getResColor(R.color.transparent)
        set(value) {
            if (commonBackgroundColor == pressBackgroundColor)
                pressBackgroundColor = value
            field = value
            view.invalidate()
        }
    var focusBackgroundColor = context.getResColor(R.color.transparent)
        set(value) {
            field = value
            view.invalidate()
        }
    var pressBackgroundColor = context.getResColor(R.color.transparent)

    var radius = context.getResDimension(R.dimen.dp_0).toFloat()
        set(value) {
            field = value
            view.invalidate()
        }
    var selected :Boolean = false
        set(value) {
            field = value
            view.invalidate()
        }
    protected var touch = false
    fun init(attrs: AttributeSet?=null){
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonDrawView)
            stokeColor = typedArray.getColor(R.styleable.CommonDrawView_common_draw_stoke_color,stokeColor)
            focusStokeColor = typedArray.getColor(R.styleable.CommonDrawView_common_draw_focus_stoke_color,stokeColor)
            pressStokeColor = typedArray.getColor(R.styleable.CommonDrawView_common_draw_press_stoke_color,stokeColor)
            stokeSize = typedArray.getDimension(R.styleable.CommonDrawView_common_draw_stoke_size,stokeSize)
            commonBackgroundColor = typedArray.getColor(R.styleable.CommonDrawView_common_draw_background,commonBackgroundColor)
            focusBackgroundColor = typedArray.getColor(R.styleable.CommonDrawView_common_draw_focus_background,commonBackgroundColor)
            pressBackgroundColor = typedArray.getColor(R.styleable.CommonDrawView_common_draw_press_background,commonBackgroundColor)
            radius = typedArray.getDimension(R.styleable.CommonDrawView_common_draw_radius,radius)
            typedArray.recycle()
        }
        view.setBackgroundColor(context.getResColor(R.color.transparent))
    }


    open fun onDraw(canvas: Canvas?) {
        val backgroundRectF = RectF(0F,0F,view.width.toFloat(),view.height.toFloat())
        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val backgroundColor = if (touch && pressBackgroundColor != commonBackgroundColor) pressBackgroundColor
        else if (selected) focusBackgroundColor else commonBackgroundColor
        backgroundPaint.color = backgroundColor
        canvas?.drawRoundRect(backgroundRectF,radius,radius,backgroundPaint)

        val halfStokeSize = stokeSize/2F
        val stokeRectF = RectF(halfStokeSize,halfStokeSize,view.width.toFloat()-halfStokeSize,view.height.toFloat()-halfStokeSize)
        val stokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        stokePaint.color = if (touch && pressStokeColor != stokeColor) pressStokeColor else if (selected) focusStokeColor else stokeColor
        stokePaint.style = Paint.Style.STROKE
        stokePaint.strokeWidth = stokeSize
        canvas?.drawRoundRect(stokeRectF,radius,radius,stokePaint)
    }

    fun onTouch(event: MotionEvent?){
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                if (view.isClickable){
                    touch = true
                    view.invalidate()
                }
            }
            MotionEvent.ACTION_CANCEL,MotionEvent.ACTION_UP->{
                touch = false
                view.invalidate()
            }
        }
    }
}