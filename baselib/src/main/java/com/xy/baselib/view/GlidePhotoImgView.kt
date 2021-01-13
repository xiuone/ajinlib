package com.xy.baselib.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.xy.baselib.utils.gilde.GlideProgressController
import com.luck.picture.lib.photoview.PhotoView

class GlidePhotoImgView(context: Context,attributeSet: AttributeSet?=null):PhotoView(context,attributeSet) {
    val glideProgressController: GlideProgressController = GlideProgressController(this)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        glideProgressController?.onDraw(canvas)
    }
}