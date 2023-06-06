package com.xy.base.widget.label.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.xy.base.utils.exp.setOnClick
import com.xy.base.widget.label.LabelBuilder
import com.xy.base.widget.label.listener.LabelListener
import com.xy.base.widget.label.listener.LabelView
import com.xy.base.widget.label.listener.LabelViewClickedListener

class LabelSingleView<T>@JvmOverloads  constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, )
    : HorizontalScrollView(context, attrs, defStyleAttr), LabelView<T> {

    private var clickedListener : LabelViewClickedListener<T>? = null
    private var viewListener : LabelListener<T>? = null

    private val builder by lazy { LabelBuilder(this,attrs) }

    private val contentView by lazy { LinearLayout(context) }

    private val dataList by lazy { ArrayList<T>() }


    init {
        builder.init()
        contentView.orientation = LinearLayout.HORIZONTAL
        isVerticalScrollBarEnabled = false
        this.addView(contentView)
    }


    override fun setData(data: MutableList<T>) {
        synchronized(this){
            dataList.clear()
            dataList.addAll(data)
            contentView.removeAllViews()
            for (item in data){
                if (contentView.childCount in 0 until builder.maxNumber){
                    val childView = viewListener?.onCreateLabelView(item)
                    val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    if (contentView.childCount>0)
                        layoutParams.leftMargin = builder.spaceH
                    childView?.layoutParams = layoutParams
                    if (clickedListener != null){
                        childView?.setOnClick{
                            clickedListener?.onLabelClicked(childView,item)
                        }
                    }
                    if (childView != null){
                        contentView.addView(childView)
                        childView.tag = item
                    }
                }else{
                    return
                }
            }
        }
    }

    override fun getData(): MutableList<T>  = dataList

    override fun setOnClickedListener(listener: LabelViewClickedListener<T>) {
        this.clickedListener = listener
    }

    override fun setOnViewListener(listener: LabelListener<T>) {
        viewListener = listener
    }

}