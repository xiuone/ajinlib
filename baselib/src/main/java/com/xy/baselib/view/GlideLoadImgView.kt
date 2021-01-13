package com.xy.baselib.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.xy.baselib.utils.gilde.GlideProgressController


class GlideLoadImgView(context: Context,attributeSet: AttributeSet?)
    : AppCompatImageView(context,attributeSet){
    val glideProgressController:GlideProgressController = GlideProgressController(this)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        glideProgressController?.onDraw(canvas)
    }
}