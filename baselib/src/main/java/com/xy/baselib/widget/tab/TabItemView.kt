package com.xy.baselib.widget.tab

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.*
import com.xy.baselib.R
import com.xy.utils.getResColor
import com.xy.utils.getResDimension
import com.xy.baselib.widget.tab.listener.CustomTabEntity
import com.xy.baselib.widget.tab.type.ItemType

class TabItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :RelativeLayout(context, attrs, defStyleAttr) {
    var tabEntity:CustomTabEntity?=null
        set(value) {
            field = value
            removeAllViews()
            LayoutInflater.from(context).inflate(getLayoutRes(value),this,true)
            setData(tabEntity)
            paddingLeft = value?.paddingLeft()?:0
            paddingTop = value?.paddingTop()?:0
            paddingRight = value?.paddingRight()?:0
            paddingBottom = value?.paddingBottom()?:0
        }

    @LayoutRes
    open fun getLayoutRes(tabEntity: CustomTabEntity?):Int{
        return when(tabEntity?.layoutType()){
            ItemType.IconLeft-> R.layout.layout_tab_left
            ItemType.IconTop-> R.layout.layout_tab_top
            ItemType.IconRight-> R.layout.layout_tab_right
            ItemType.IconBottom-> R.layout.layout_tab_bottom
            else -> R.layout.layout_tab_nono
        }
    }

    /**
     * 获取textView
     */
    protected fun getTextView(@IdRes idRes: Int):TextView?{
        val view = findViewById<View>(idRes)
        if (view is TextView)return view;
        return null
    }

    /**
     * 获取imageView
     */
    protected fun getImageView(@IdRes idRes: Int):ImageView?{
        val view = findViewById<View>(idRes)
        if (view is ImageView)return view;
        return null;
    }

    /**
     * 设置初始化数据
     */
    open fun setData(tabEntity: CustomTabEntity?){
        tabEntity?.run {
            setTabText(getTabTitle())
            setTabIcon(getTabIcon())
            setTabTextSize(getTabUnSelectTitleSize())
            setTabTextColor(getTabTitleColor())
            setTabTextStyle(getTabTitleStyle())
            setSelect(false)
        }
    }

    /**
     * 设置选中状态
     */
    open fun setSelect(isSelect:Boolean){
        getTextView(R.id.tv_tab_title)?.isSelected = isSelect
        getImageView(R.id.iv_tab_icon)?.isSelected = isSelect
        tabEntity?.run {
            setTabTextSize(if (isSelect) getTabSelectTitleSize() else getTabUnSelectTitleSize())
        }
    }

    /**
     * 设置文本大小
     */
    open fun setTabTextSize(@DimenRes textSize:Int){
        getTextView(R.id.tv_tab_title)?.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResDimension(textSize).toFloat())
    }

    /**
     * 设置字体类型
     */
    fun setTabTextStyle(typeFace:Int){
        getTextView(R.id.tv_tab_title)?.typeface = Typeface.defaultFromStyle(typeFace)
    }

    /**
     * 设置字体颜色
     */
    fun setTabTextColor(@ColorRes colors:Int){
        getTextView(R.id.tv_tab_title)?.setTextColor(ColorStateList.valueOf(context.getResColor(colors)))
    }

    /**
     * 设置文字
     */
    fun setTabText(string: String?){
        getTextView(R.id.tv_tab_title)?.text = string
    }

    /**
     * 设置图标
     */
    fun setTabIcon(@DrawableRes iconRes: Int){
        getImageView(R.id.iv_tab_icon)?.setImageResource(iconRes)
    }

    fun setPaddingLeft(@DimenRes paddingLeft:Int){
        setPadding(paddingLeft,paddingRight,paddingTop,paddingBottom)
    }

    fun setPaddingTop(@DimenRes paddingTop:Int){
        setPadding(paddingLeft,paddingRight,paddingTop,paddingBottom)
    }

    fun setPaddingRight(@DimenRes paddingRight:Int){
        setPadding(paddingLeft,paddingRight,paddingTop,paddingBottom)
    }

    fun setPaddingBottom(@DimenRes paddingBottom:Int){
        setPadding(paddingLeft,paddingRight,paddingTop,paddingBottom)
    }
}