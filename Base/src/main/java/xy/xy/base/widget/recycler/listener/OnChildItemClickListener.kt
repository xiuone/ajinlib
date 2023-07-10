package xy.xy.base.widget.recycler.listener

import android.view.View
import xy.xy.base.widget.recycler.holder.BaseViewHolder

interface OnChildItemClickListener<T>{
    fun onItemChildClick(view:View,data:T,holder: BaseViewHolder?)
}