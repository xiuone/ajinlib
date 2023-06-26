package com.xy.base.dialog.select

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.xy.base.dialog.base.BaseBottomDialog
import com.xy.base.utils.exp.setOnClick
import com.xy.base.widget.recycler.holder.BaseViewHolder
import com.xy.base.widget.recycler.listener.OnItemClickListener

class MinSelectDialog(context: Context,private val listener:DialogSelectListener) :BaseBottomDialog(context){

    private val adapter by lazy { MinDialogSelectAdapter(listener) }
    private val data by lazy { this.listener.onCreateData() }
    override fun layoutRes(): Int  = listener.dialogLayoutRes()?:0

    override fun initView() {
        super.initView()
        val recyclerView = listener.onCreateRecyclerView(this)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
        adapter.itemClickListener = object : OnItemClickListener<DialogSelectMode>{
            override fun onItemClick(view: View, data: DialogSelectMode, holder: BaseViewHolder?) {
                adapter.updateType(data.type)
                listener.onDataCallBack(data)
                dismiss()
            }
        }
        adapter.setNewData(data)
        this.listener.onCreateDialogCancelView(this)?.setOnClick{
            dismiss()
        }
        this.listener.onCreateDialogSureView(this)?.setOnClick{
            dismiss()
            adapter.getSelectData {
                listener.onDataCallBack(it)
            }
        }
    }

    fun showWithType(type:Any?) {
        super.show()
        adapter.updateType(type)
    }
}