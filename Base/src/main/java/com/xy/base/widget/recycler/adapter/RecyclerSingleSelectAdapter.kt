package com.xy.base.widget.recycler.adapter



abstract class RecyclerSingleSelectAdapter<T>(layoutId:Int) : RecyclerSingleAdapter<T>(layoutId) {
    protected var selectPosition = -1

    fun setCurrentSelectPosition(position:Int){
        if (position == selectPosition)return
        val oldSelectPosition = this.selectPosition
        this.selectPosition = position
        setNotifyItemChanged(oldSelectPosition)
    }

    open fun setNotifyItemChanged(oldSelectPosition:Int){
        if (oldSelectPosition >= getHeadSize()){
            notifyItemChanged(oldSelectPosition)
        }
        notifyItemChanged(selectPosition-getHeadSize())
    }

    fun getCurrentSelectPosition():T?{
        if (selectPosition in 0 until data.size){
            return data[selectPosition]
        }
        return null
    }
}