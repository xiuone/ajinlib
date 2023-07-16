package com.xy.amap

import android.content.Context
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

    interface MapTouchExListener{
        fun dispatchTouchEvent(ev: MotionEvent?)
    }
}