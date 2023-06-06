package com.xy.base.dialog.select

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.xy.base.dialog.base.BaseBottomDialog
import com.xy.base.utils.exp.setOnClick

class MinSelectDialog(context: Context,private val listener:DialogSelectListener) :BaseBottomDialog(context){

    private val adapter by lazy { MinDialogSelectAdapter(listener) }
    private val data by lazy { this.listener.onCreateData() }
    override fun layoutRes(): Int  = listener.dialogLayoutRes()?:0

    override fun initView() {
        super.initView()
        val recyclerView = listener.onCreateRecyclerView(this)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = adapter
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