package com.jianbian.baselib.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import com.jianbian.baselib.R

/**
 * 旋转的标签
 */
class LabelView(context: Context, attributeSet: AttributeSet?):AppCompatTextView(context,attributeSet){
    private var viewPosition = 1

    companion object{
        const val leftTop = 1
        const val leftBottom = 2
        const val rightTop = 3
        const val rightBottom = 4
    }


    init {
        if (attributeSet != null) {
            val array = context.obtainStyledAttributes(attributeSet, R.styleable.LabelView)
            viewPosition = array.getInt(R.styleable.LabelView_aj_label_direction,leftTop)
        }
    }

    fun setPostion(postion:Int){
        this.viewPosition = postion
        invalidate()
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        if (viewPosition == leftTop){
            val translateWidth = 10F
            canvas?.translate(translateWidth, 0F)
            canvas?.rotate(-45F, 0F, 0F)
        }else if (viewPosition == leftBottom){
            canvas?.translate(-width / 2.toFloat(), 0f)
            canvas?.rotate(-45F, 0F, height.toFloat())
        }else if (viewPosition == rightTop){
            canvas?.translate(width / 2.toFloat(), 0f)
            canvas?.rotate(-45F, width / 2.toFloat(), 0f)
        }else if (viewPosition == rightBottom){
            canvas?.translate(width / 2.toFloat(), 0f)
            canvas?.rotate(-45F, 0F,  height.toFloat())
        }
        canvas?.restore()
    }
}