package com.xy.baselib.widget.common.linear

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.xy.baselib.widget.common.CommonDrawListener
import com.xy.baselib.widget.common.draw.CommonDrawImpl

open class CommonLinearLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), CommonDrawListener,View.OnFocusChangeListener{
    private val editBackDrawImpl by lazy { CommonDrawImpl(this,context) }
    protected var mFocusListener:OnFocusChangeListener?=null
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


    override fun onFocusChange(p0: View?, p1: Boolean) {
        mFocusListener?.onFocusChange(p0, p1)
    }
}