package com.xy.baselib.widget.common.text

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.xy.baselib.widget.common.CommonDrawListener
import com.xy.baselib.widget.common.draw.CommonDrawImpl

open class CommonEditTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null) :
    AppCompatEditText(context, attrs), View.OnFocusChangeListener ,
    CommonDrawListener {
    private val editBackDrawImpl by lazy { CommonDrawImpl(this,context) }
    private var mFocusListener:OnFocusChangeListener ?=null
    init {
        editBackDrawImpl.init(attrs)
        gravity = Gravity.CENTER_VERTICAL
        super.setOnFocusChangeListener(this)
    }

    override fun onDraw(canvas: Canvas?) {
        editBackDrawImpl.onDraw(canvas)
        super.onDraw(canvas)
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        super.setOnFocusChangeListener(this)
        this.mFocusListener = l
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        mFocusListener?.onFocusChange(p0, p1)
        editBackDrawImpl.selected = p1
    }
//
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        editBackDrawImpl.onTouch(event)
//        return super.onTouchEvent(event)
//    }

    override fun onCommonDrawImpl(): CommonDrawImpl = editBackDrawImpl

}