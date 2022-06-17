package com.xy.baselib.widget.common

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.xy.baselib.widget.common.CommonDrawListener
import com.xy.baselib.widget.common.draw.CommonDrawImpl

open class CommonRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    RecyclerView(context, attrs, defStyleAttr), CommonDrawListener {
    private val editBackDrawImpl by lazy { CommonDrawImpl(this,context) }
    init {
        editBackDrawImpl.init(attrs)
    }

    override fun onDraw(canvas: Canvas?) {
        editBackDrawImpl.onDraw(canvas)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        editBackDrawImpl.onTouch(event)
        return super.onTouchEvent(event)
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        editBackDrawImpl.selected = selected
    }

    override fun onCommonDrawImpl(): CommonDrawImpl = editBackDrawImpl
}