package com.xy.base.widget.tag

abstract class TagMultiAdapter :TagBaseAdapter<TagMultiListener>() {


    override fun getItemViewType(position: Int): Int {
        if (position < data.size){
            val item  = data[position]
            return item.onCreateRecyclerType()
        }
        return super.getItemViewType(position)
    }
}