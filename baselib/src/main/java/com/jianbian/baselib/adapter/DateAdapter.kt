package com.jianbian.baselib.adapter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.jianbian.baselib.R
import com.jianbian.baselib.mvp.mode.DateMode
import com.jianbian.baselib.utils.AppUtil

class DateAdapter(private val selectBackground:Int,private val selectBackgroundNot:Int,private val toDayBackGround:Int
                  ,private val selectTextColor:Int,private val selectTextColorNot:Int,private val toDayTextColor:Int
                  ,private val commonTextColor:Int)
    :BaseRecyclerAdapter<DateMode>(R.layout.item_date) {
    override fun convert(context: Context, viewHolder: ViewHolder, item: DateMode, position: Int) {
        val itemWidth = AppUtil.dp2px(context,40F)
        viewHolder.getView<View>(R.id.date_title)?.layoutParams = LinearLayout.LayoutParams(itemWidth,itemWidth)
        val text = viewHolder.getTextView(R.id.date_title)
        text?.text = "${item.day}"
        if (item.isSelect){
            text?.setBackgroundResource(selectBackground)
            text?.setTextColor(selectTextColor)
        }else if (item.isToDay){
            text?.setBackgroundResource(toDayBackGround)
            text?.setTextColor(toDayTextColor)
        }else if (item.isLeftMore || item.isRightMore){
            text?.setTextColor(selectTextColorNot)
            text?.setBackgroundResource(selectBackgroundNot)
        }else{
            text?.setTextColor(commonTextColor)
            text?.setBackgroundResource(selectBackgroundNot)
        }
    }
    
    fun notifychangeItem(index:Int,date: DateMode){
        getItem(index)?.year = date.year
        getItem(index)?.month = date.month
        getItem(index)?.day = date.day
        getItem(index)?.isToDay = date.isToDay
        getItem(index)?.isLeftMore = date.isLeftMore
        getItem(index)?.isRightMore = date.isRightMore
        getItem(index)?.isSelect = date.isSelect
        notifyItemChanged(index)
    }
}