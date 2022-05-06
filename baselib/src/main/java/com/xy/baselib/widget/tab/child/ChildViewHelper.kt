package com.xy.baselib.widget.tab.child

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.xy.baselib.R
import com.xy.baselib.widget.tab.listener.OnTabDrawItemListener
import com.xy.baselib.widget.tab.listener.OnTabTextItemListener
import com.xy.baselib.widget.tab.type.DirectionType
import com.xy.utils.getResColor
import com.xy.utils.getResDimension

class ChildViewHelper<T>(private val context: Context, private val attrs: AttributeSet?) {
    private var directionType: DirectionType = DirectionType.TOP

    private var selectTextColor = context.getResColor(R.color.white)
    private var selectTextSize = 15F
    private var unSelectTextColor = context.getResColor(R.color.gray_9999)
    private var unSelectTextSize = 15F

    fun init() {
        attrs?.run {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayout)
            selectTextColor = typedArray.getColor(R.styleable.TabLayout_tab_select_text_color,selectTextColor)
            unSelectTextColor = typedArray.getColor(R.styleable.TabLayout_tab_select_un_text_color,unSelectTextColor)
            selectTextSize = typedArray.getDimension(R.styleable.TabLayout_tab_select_text_size,context.getResDimension(R.dimen.sp_15).toFloat())
            unSelectTextSize = typedArray.getDimension(R.styleable.TabLayout_tab_select_un_text_size,selectTextSize)
            val type = typedArray.getInteger(R.styleable.TabLayout_tab_select_direction,0);
            directionType = when(type){
                1-> DirectionType.LEFT
                2-> DirectionType.RIGHT
                3-> DirectionType.TOP
                4-> DirectionType.BOTTOM
                else -> DirectionType.NONO
            }
        }
    }

    fun getView(data: T):View{
        var  layoutRes = when (directionType){
            DirectionType.LEFT-> R.layout.layout_tab_left
            DirectionType.RIGHT-> R.layout.layout_tab_right
            DirectionType.TOP-> R.layout.layout_tab_top
            DirectionType.BOTTOM-> R.layout.layout_tab_bottom
            else-> R.layout.layout_tab_nono;
        }
        val view = LayoutInflater.from(context).inflate(layoutRes,null)
        if (data is OnTabTextItemListener) {
            findTextView(view)?.text = data.onText()
        }
        if (data is OnTabDrawItemListener) {
            findIcon(view)?.setImageResource(data.onUnSelectDrawRes())
        }
        return view
    }

    fun findTextView(view: View): TextView?{
        val textView = view.findViewById<View>(R.id.tv_tab_title)
        if (textView is TextView)
            return textView
        return null
    }

    fun findIcon(view: View): ImageView?{
        val imageView = view.findViewById<View>(R.id.iv_tab_icon)
        if (imageView is ImageView)
            return imageView
        return null
    }

    fun setTabSelect(view: View ,data: T, isSelect: Boolean){
        if (data is OnTabDrawItemListener) {
            findIcon(view)?.setImageResource(if (isSelect) data.onSelectDrawRes() else data.onUnSelectDrawRes())
        }
        findTextView(view)?.setTextColor(if (isSelect) selectTextColor else unSelectTextColor)
        findTextView(view)?.setTextSize(TypedValue.COMPLEX_UNIT_PX,if (isSelect) selectTextSize else unSelectTextSize)
    }

}