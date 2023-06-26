package com.xy.base.widget.image.circle

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.xy.base.widget.image.RoundBuild

class BackgroundRoundImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr){
    protected val builder by lazy { RoundBuild(this,attrs) }

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }


    override fun onDraw(canvas: Canvas?) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = builder.backgroundColor
        canvas?.drawPath(builder.getAllPath(0f),paint)
        super.onDraw(canvas)
    }
}