package com.xy.baselib.widget.multiline.label

import android.content.Context
import android.util.AttributeSet
import com.xy.baselib.R
import com.xy.baselib.widget.common.draw.CommonDrawImpl
import com.xy.baselib.widget.multiline.label.item.*
import com.xy.baselib.widget.tab.type.DirectionType
import com.xy.baselib.exp.getResDimension

class MultiIconTabLayout<T :LabelIconEntry> @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0)
    : LabelView<T>(context, attrs, defStyleAttr){
    var imageSize = context.getResDimension(R.dimen.dp_30)
    var marginIcon = context.getResDimension(R.dimen.dp_6)
    private var directionType = DirectionType.TOP

    private val editBackDrawImpl by lazy { CommonDrawImpl(this,context) }

    init {
        attrs?.run {
            val tabArray = context.obtainStyledAttributes(attrs, R.styleable.MultiTabLayout)
            imageSize = tabArray.getDimensionPixelSize(R.styleable.MultiTabLayout_multi_img_size,imageSize)
            marginIcon = tabArray.getDimensionPixelSize(R.styleable.MultiTabLayout_multi_margin,marginIcon)
            when(tabArray.getInteger(R.styleable.MultiTabLayout_multi_direction,directionType.type)){
                DirectionType.BOTTOM.type->this@MultiIconTabLayout.directionType = DirectionType.BOTTOM
                DirectionType.RIGHT.type->this@MultiIconTabLayout.directionType = DirectionType.RIGHT
                DirectionType.LEFT.type->this@MultiIconTabLayout.directionType = DirectionType.LEFT
                else->this@MultiIconTabLayout.directionType = DirectionType.TOP
            }
            tabArray.recycle()
        }
        editBackDrawImpl.init(attrs)
    }

    override fun addView(data: T?) {
        data?.run {
            val itemView = when(directionType){
                DirectionType.LEFT->ItemLeftView(context)
                DirectionType.RIGHT->ItemRightView(context)
                DirectionType.BOTTOM->ItemBottomView(context)
                else->ItemTopView(context)
            }
            itemView.setText(onLabel())
            itemView.setImageResource(onIcon())
            itemView.setTextColor(textColorList)
            itemView.setTextSize(textSize)
            itemView.setDrawSize(imageSize,marginIcon)
            setItemDraw(itemView)
            addView(itemView,data)
        }
    }
}