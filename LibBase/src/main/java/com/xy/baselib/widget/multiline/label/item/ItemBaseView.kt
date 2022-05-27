package com.xy.baselib.widget.multiline.label.item

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.xy.baselib.R
import com.xy.baselib.widget.common.linear.CommonLinearLayout
import com.xy.baselib.exp.getResColor
import com.xy.baselib.exp.getResDimension

abstract class ItemBaseView @JvmOverloads constructor(context: Context, private val attrs: AttributeSet?=null, defStyleAttr:Int = 0)
    : CommonLinearLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(layoutRes(),this,true)
        gravity = Gravity.CENTER
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemView)
            val textSize = typedArray.getDimension(R.styleable.ItemView_item_text_size, context.getResDimension(R.dimen.sp_15).toFloat())
            val textColor = typedArray.getColor(R.styleable.ItemView_item_text_color, context.getResColor(R.color.gray_3333))
            val drawSize = typedArray.getDimensionPixelSize(R.styleable.ItemView_item_draw_size, ViewGroup.LayoutParams.WRAP_CONTENT)
            val drawRes = typedArray.getResourceId(R.styleable.ItemView_item_draw_res, R.drawable.bg_transparent)
            val textStr = typedArray.getString(R.styleable.ItemView_item_text)
            val drawMargin = typedArray.getDimensionPixelOffset(R.styleable.ItemView_item_draw_margin,context.getResDimension(R.dimen.dp_0))

            setTextColor(textColor)
            setTextSize(textSize)
            setText(textStr)
            setDrawSize(drawSize,drawMargin)
            setImageResource(drawRes)
        }
    }



    fun setDrawSize(drawSize:Int,drawMargin:Int){
        val imageView :ImageView?= findViewById(R.id.item_iv)
        imageView?.layoutParams = drawMargin(LayoutParams(drawSize,drawSize),drawMargin)
    }

    fun setDrawSize(drawSize: Int){
        val imageView :ImageView?= findViewById(R.id.item_iv)
        val params = imageView?.layoutParams?: LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        setDrawSize(drawSize,getDrawMargin(params))
    }

    fun setDrawMargin(drawMargin: Int){
        val imageView :ImageView?= findViewById(R.id.item_iv)
        val params = imageView?.layoutParams
        val drawSize = if (params is LayoutParams) params.width else ViewGroup.LayoutParams.WRAP_CONTENT
        setDrawSize(drawSize,drawMargin)
    }

    fun setTextColor(textColor:Int){
        val textView :TextView? = findViewById(R.id.item_tv)
        textView?.setTextColor(textColor)
    }

    fun setTextColor(textColor:ColorStateList){
        val textView :TextView? = findViewById(R.id.item_tv)
        textView?.setTextColor(textColor)
    }

    fun setTextSize(textSize:Float){
        val textView :TextView?= findViewById(R.id.item_tv)
        textView?.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize)
    }

    fun setText(textStr:String?){
        val textView:TextView? = findViewById(R.id.item_tv)
        textView?.text = textStr
    }

    fun setImageResource(@DrawableRes drawRes:Int){
        val imageView:ImageView? = findViewById(R.id.item_iv)
        imageView?.setImageResource(drawRes)
    }

    @LayoutRes
    abstract fun layoutRes():Int

    abstract fun drawMargin(params:LayoutParams,drawMargin:Int):LayoutParams

    abstract fun getDrawMargin(params:ViewGroup.LayoutParams):Int

}