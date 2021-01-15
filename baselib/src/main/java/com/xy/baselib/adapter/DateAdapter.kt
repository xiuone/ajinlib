package com.xy.baselib.adapter

import android.view.View
import android.widget.LinearLayout
import com.xy.baselib.R
import com.xy.baselib.mvp.mode.DateMode
import com.xy.baselib.utils.AppUtil
import com.xiuone.adapter.adapter.RecyclerSingleAdapter
import com.xiuone.adapter.adapter.RecyclerViewHolder

class DateAdapter(private val selectBackground:Int,private val selectBackgroundNot:Int,private val toDayBackGround:Int
                  ,private val selectTextColor:Int,private val selectTextColorNot:Int,private val toDayTextColor:Int
                  ,private val commonTextColor:Int)
    :RecyclerSingleAdapter<DateMode>(R.layout.item_date) {

    var selectPosition = -1
    fun changeSelectPosition(position: Int){
        val oldSelectPosition = selectPosition
        this.selectPosition = position
        notifyItemChanged(position)
        notifyItemChanged(oldSelectPosition)
    }

    fun notifyChangeItem(index:Int,date: DateMode){
        dataController.getItem(index)?.year = date.year
        dataController.getItem(index)?.month = date.month
        dataController.getItem(index)?.day = date.day
        dataController.getItem(index)?.isToDay = date.isToDay
        dataController.getItem(index)?.nowMoth = date.nowMoth
        notifyItemChanged(index+dataController.getHeadSize())
    }

    override fun bindView(holder: RecyclerViewHolder, item: DateMode, position: Int) {
        val itemWidth = AppUtil.dp2px(holder.itemView.context,40F)
        holder.getView<View>(R.id.date_title)?.layoutParams = LinearLayout.LayoutParams(itemWidth,itemWidth)
        val text = holder.getTextView(R.id.date_title)
        text?.text = "${item.day}"
        if (selectPosition == position){
            text?.setBackgroundResource(selectBackground)
            text?.setTextColor(selectTextColor)
        }else if (item.isToDay){
            text?.setBackgroundResource(toDayBackGround)
            text?.setTextColor(toDayTextColor)
        }else if (!item.nowMoth){
            text?.setTextColor(selectTextColorNot)
            text?.setBackgroundResource(selectBackgroundNot)
        }else{
            text?.setTextColor(commonTextColor)
            text?.setBackgroundResource(selectBackgroundNot)
        }
    }
}