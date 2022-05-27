package com.xy.baselib.widget.multiline.label

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.xy.baselib.R
import com.xy.baselib.widget.AppItemClickListener
import com.xy.baselib.widget.common.draw.CommonDrawImpl
import com.xy.baselib.widget.common.CommonDrawListener
import com.xy.baselib.widget.common.text.CommonTextView
import com.xy.baselib.widget.multiline.MultiBaseFrameLayout
import com.xy.baselib.exp.getResColor
import com.xy.baselib.exp.getResDimension
import com.xy.baselib.exp.setOnClick

open class LabelView<T :LabelEntry>  @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr:Int = 0) :
    MultiBaseFrameLayout(context, attrs, defStyleAttr) {
    private val editBackDrawImpl by lazy { CommonDrawImpl(this,context) }
    var itemClickListener: AppItemClickListener<T>?=null

    protected var cow = 0
    protected var textSize = context.getResDimension(R.dimen.sp_14).toFloat()
    protected var textColor = context.getResColor(R.color.gray_3333)
    protected var textColorList = ColorStateList.valueOf(textColor)
    protected var labelPaddingLeft = 0
    protected var labelPaddingRight = 0
    protected var labelPaddingTop = 0
    protected var labelPaddingBottom = 0
    var labelItemHeight = ViewGroup.LayoutParams.WRAP_CONTENT
        set(value) {
            field = value
            requestLayout()
        }

    init {
        editBackDrawImpl.init(attrs)
        attrs?.run {
            val tabArray = context.obtainStyledAttributes(attrs, R.styleable.LabelView)
            cow = tabArray.getInteger(R.styleable.LabelView_label_cow,cow)
            textSize = tabArray.getDimension(R.styleable.LabelView_label_textSize,textSize)
            textColor = tabArray.getColor(R.styleable.LabelView_label_textColor,textColor)
            textColorList = tabArray.getColorStateList(R.styleable.LabelView_label_textColor)?:textColorList
            labelPaddingLeft = tabArray.getDimensionPixelSize(R.styleable.LabelView_label_paddingLeft,labelPaddingLeft)
            labelPaddingRight = tabArray.getDimensionPixelSize(R.styleable.LabelView_label_paddingRight,labelPaddingRight)
            labelPaddingTop = tabArray.getDimensionPixelSize(R.styleable.LabelView_label_paddingTop,labelPaddingTop)
            labelPaddingBottom = tabArray.getDimensionPixelSize(R.styleable.LabelView_label_paddingBottom,labelPaddingBottom)
            spaceHorizontal = tabArray.getDimensionPixelSize(R.styleable.LabelView_label_space_horizontal,spaceHorizontal)
            spaceVertical = tabArray.getDimensionPixelSize(R.styleable.LabelView_label_space_vertical,spaceVertical)
            labelItemHeight = tabArray.getDimensionPixelSize(R.styleable.LabelView_label_height,labelItemHeight)
            tabArray.recycle()
        }
    }

    fun setNewView(data:MutableList<T>){
        removeAllViews()
        addView(data)
    }

    fun addView(data:MutableList<T>){
        for (item in data){
            addView(item)
        }
    }

    open fun addView(data: T?) {
        data?.run {
            val commonTextView = CommonTextView(context)
            setItemDraw(commonTextView)
            commonTextView.text = data.onLabel()
            commonTextView.isSingleLine = true
            commonTextView.setTextColor(textColorList)
            commonTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize)
            addView(commonTextView,data)
        }
    }

    protected fun addView(view: View,data: T){
        val wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT
        val params = ViewGroup.LayoutParams(wrapContent,if (labelItemHeight<=0) wrapContent else labelItemHeight)
        view.layoutParams = params
        super.addView(view)
        view.tag = data
        view.setPadding(labelPaddingLeft,labelPaddingTop,labelPaddingRight,labelPaddingBottom)
        view.setOnClick {
            view.isSelected = !view.isSelected
            itemClickListener?.onClicked(data)
        }
    }


    protected fun setItemDraw(commonDrawListener: CommonDrawListener){
        commonDrawListener.onCommonDrawImpl().stokeColor = editBackDrawImpl.stokeColor
        commonDrawListener.onCommonDrawImpl().focusStokeColor = editBackDrawImpl.focusStokeColor
        commonDrawListener.onCommonDrawImpl().pressStokeColor = editBackDrawImpl.pressStokeColor
        commonDrawListener.onCommonDrawImpl().stokeSize = editBackDrawImpl.stokeSize
        commonDrawListener.onCommonDrawImpl().commonBackgroundColor = editBackDrawImpl.commonBackgroundColor
        commonDrawListener.onCommonDrawImpl().focusBackgroundColor = editBackDrawImpl.focusBackgroundColor
        commonDrawListener.onCommonDrawImpl().pressBackgroundColor = editBackDrawImpl.pressBackgroundColor
        commonDrawListener.onCommonDrawImpl().radius = editBackDrawImpl.radius
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (labelItemHeight<=0)return
        if (cow>0){
            resetEqualView(cow,labelItemHeight)
        }else{
            resetMoreView(labelItemHeight)
        }
    }
}