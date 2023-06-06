package com.xy.base.widget.image.circle

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.xy.base.widget.image.RoundBuild

class BackgroundCircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr){
    protected val builder by lazy { RoundBuild(this,attrs) }

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = builder.backgroundColor
        val radius = builder.getViewMinSize()/2F
        canvas?.drawCircle(width.toFloat()/2,height.toFloat()/2,radius,paint)
        super.onDraw(canvas)
    }
}