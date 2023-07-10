package xy.xy.base.widget.recycler.adapter

import xy.xy.base.widget.recycler.holder.BaseViewHolder
import xy.xy.base.widget.recycler.listener.RecyclerExpListener


open class RecyclerSingleExpAdapter<T>(layoutId:Int, private val listener: RecyclerExpListener<T>?) : RecyclerSingleAdapter<T>(layoutId) {

    override fun onBindViewHolder(holder: BaseViewHolder, data: T, position: Int) {
        listener?.onBindViewHolder(holder, data, position)
    }
}