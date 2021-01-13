package com.jianbian.baselib.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.jianbian.baselib.R
import com.jianbian.baselib.utils.AppUtil
import com.jianbian.baselib.utils.gilde.GlideProgressController
import com.jianbian.baselib.utils.gilde.OnProgressListener
import kotlin.math.min


class GlideLoadImgView(context: Context,attributeSet: AttributeSet?)
    : AppCompatImageView(context,attributeSet){
    val glideProgressController:GlideProgressController = GlideProgressController(this)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        glideProgressController?.onDraw(canvas)
    }
}