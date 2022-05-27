package com.xy.baselib.widget.shadow.impl

import android.view.View
import com.xy.baselib.widget.shadow.ShadowBuilder

open class OnSizeChangeImpl(protected val view: View, protected var builderImpl: ShadowBuilderImpl) :
    OnSizeChangeListener {
    protected val builder: ShadowBuilder by lazy {builderImpl.builder  }
    override fun initView() {
        view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)
        view.setBackgroundColor(0X00000000)
        view.postInvalidate()
    }



    override fun getPaddingLeft(): Int = if (builder.isShowLeftShadow) { builder.mShadowLimit.toInt() } else 0
    override fun getPaddingRight(): Int = if (builder.isShowRightShadow) { builder.mShadowLimit.toInt() } else 0
    override fun getPaddingTop(): Int = if (builder.isShowTopShadow) { builder.mShadowLimit.toInt() } else 0;
    override fun getPaddingBottom(): Int = if (builder.isShowTopShadow) { builder.mShadowLimit.toInt() } else 0
    }
