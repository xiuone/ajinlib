package com.xy.amap

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import com.amap.api.maps.MapView

class MapTouchExpView@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    MapView(context,attrs,defStyleAttr) {
    private var mapTouchExListener:MapTouchExListener?=null

    fun bindTouchListener(listener:MapTouchExListener){
        this.mapTouchExListener = listener
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        mapTouchExListener?.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        val rect = RectF(0F,0F,width.toFloat(),height.toFloat())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        canvas?.drawRect(rect,paint)
        super.dispatchDraw(canvas)
    }

    interface MapTouchExListener{
        fun dispatchTouchEvent(ev: MotionEvent?)
    }
}