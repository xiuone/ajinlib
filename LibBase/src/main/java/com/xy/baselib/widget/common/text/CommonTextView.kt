package com.xy.baselib.widget.common.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.xy.baselib.R
import com.xy.baselib.exp.Logger
import com.xy.baselib.exp.getResColor
import com.xy.baselib.widget.common.CommonDrawListener
import com.xy.baselib.widget.common.draw.CommonDrawImpl

open class CommonTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr), CommonDrawListener {
    private val editBackDrawImpl by lazy { CommonDrawImpl(this,context) }
    init {
        editBackDrawImpl.init(attrs)
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialTextView)
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(scrollX.toFloat(),scrollY.toFloat())
        editBackDrawImpl.onDraw(canvas)
        canvas?.restore()
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