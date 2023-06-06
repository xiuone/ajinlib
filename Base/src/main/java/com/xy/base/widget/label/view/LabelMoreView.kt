package com.xy.base.widget.label.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import com.xy.base.utils.exp.setOnClick
import com.xy.base.widget.label.LabelBuilder
import com.xy.base.widget.label.listener.LabelListener
import com.xy.base.widget.label.listener.LabelView
import com.xy.base.widget.label.listener.LabelViewClickedListener

class LabelMoreView<T>@JvmOverloads  constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, )
    : LinearLayout(context, attrs, defStyleAttr), LabelView<T> {

    private var clickedListener : LabelViewClickedListener<T>? = null
    private var viewListener : LabelListener<T>? = null

    private val builder by lazy { LabelBuilder(this,attrs) }

    private val dataList by lazy { ArrayList<T>() }

    init {
        builder.init()
    }


    override fun setData(data: MutableList<T>) {
        synchronized(this){
            dataList.clear()
            dataList.addAll(data)
            removeAllViews()
            for ((index,item) in data.withIndex()){
                if (index in 0 until builder.maxNumber){
                    val cowLayout = getCowLayout()
                    val childView = viewListener?.onCreateLabelView(item)
                    val layoutParams = LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1F)
                    if (cowLayout.childCount>0) layoutParams.leftMargin = builder.spaceH
                    childView?.layoutParams = layoutParams
                    childView?.setPadding(builder.paddingH,builder.paddingV,builder.paddingH,builder.paddingV)
                    if (clickedListener != null){
                        childView?.setOnClick{
                            clickedListener?.onLabelClicked(childView,item)
                        }
                    }
                    if (childView != null){
                        cowLayout.addView(childView)
                        childView.tag = item
                    }
                }else{
                    break
                }
            }

            if (childCount > 0 ){
                val lastLayout = getChildAt(childCount-1)
                if (lastLayout is LinearLayout && lastLayout.childCount < builder.cowNumber){
                    for (index in lastLayout.childCount .. builder.cowNumber){
                        val space = Space(context)
                        val layoutParams = LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1F)
                        if (lastLayout.childCount>0) layoutParams.leftMargin = builder.spaceH
                        space.layoutParams = layoutParams
                        addView(space)
                    }
                }
            }
        }
    }


    private fun getCowLayout():LinearLayout{
        if (this.childCount > 0 ){
            val childView = getChildAt(childCount-1)
            if (childView is LinearLayout && childView.childCount < builder.cowNumber){
                return childView
            }
        }
        val childView = LinearLayout(context)
        val params =  LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        if (this.childCount <= 0)
            params.topMargin = builder.spaceV
        childView.layoutParams = params
        this.addView(childView)
        return childView
    }


    override fun getData(): MutableList<T>  = dataList

    override fun setOnClickedListener(listener: LabelViewClickedListener<T>) {
        this.clickedListener = listener
    }

    override fun setOnViewListener(listener: LabelListener<T>) {
        viewListener = listener
    }

}