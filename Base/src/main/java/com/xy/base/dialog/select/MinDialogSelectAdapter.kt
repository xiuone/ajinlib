package com.xy.base.dialog.select

import com.xy.base.utils.ContextHolder
import com.xy.base.utils.exp.getResString
import com.xy.base.widget.recycler.adapter.RecyclerSingleSelectAdapter
import com.xy.base.widget.recycler.holder.BaseViewHolder

class MinDialogSelectAdapter(private val listener:DialogSelectListener) :
    RecyclerSingleSelectAdapter<DialogSelectMode>(listener.onCreateItemLayoutRes()) {
    private var selectType :Any?= null

    fun updateType(type:Any?){
        if (type != selectType){
            selectType = type
            notifyDataSetChanged()
        }
    }


    override fun onBindViewHolder(holder: BaseViewHolder, data: DialogSelectMode, position: Int) {
        listener.onCreateSelectStatusView(holder)?.isSelected = selectType == data.type
        listener.onCreateContentTextView(holder)?.text = ContextHolder.getContext()?.getResString(data.contentRes)
        listener.onCreateTitleImageView(holder)?.setImageResource(data.icon)
    }


    fun getSelectData(method:(DialogSelectMode?)->Unit){
        val data = ArrayList(data)
        for (item in data){
            if (item.type == selectType){
                method(item)
                return
            }
        }
        method(null)
        return
    }
}
