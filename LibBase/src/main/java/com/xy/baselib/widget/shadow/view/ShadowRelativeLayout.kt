package com.xy.baselib.widget.shadow.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.xy.baselib.widget.shadow.impl.ShadowBuilderImpl
import com.xy.baselib.widget.shadow.ShadowBuilder
import com.xy.baselib.widget.shadow.impl.OnDrawImpl

class ShadowRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, ) :
    RelativeLayout(context, attrs, defStyleAttr) {
    private val shadowBuilderImpl: ShadowBuilderImpl by lazy { ShadowBuilderImpl(ShadowBuilder(this, attrs)) }
    private val onDrawImpl: OnDrawImpl by lazy { OnDrawImpl(this, shadowBuilderImpl) }

    init {
        onDrawImpl.initView()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val newLeft = onDrawImpl.getPaddingLeft()+left
        val newRight = onDrawImpl.getPaddingRight()+right
        val newTop = onDrawImpl.getPaddingTop()+top
        val newBottom = onDrawImpl.getPaddingBottom()+bottom
        super.setPadding(newLeft, newTop, newRight, newBottom)
    }

    override fun onDraw(canvas: Canvas) {
        onDrawImpl.onDraw(canvas)
        super.onDraw(canvas)
    }

}