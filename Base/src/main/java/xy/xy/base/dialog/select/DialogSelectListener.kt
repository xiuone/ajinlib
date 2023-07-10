package xy.xy.base.dialog.select

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xy.xy.base.dialog.base.BaseDialog
import xy.xy.base.dialog.listener.DialogCancelSureView
import xy.xy.base.dialog.listener.DialogImpResListener
import xy.xy.base.widget.recycler.holder.BaseViewHolder

interface DialogSelectListener :DialogCancelSureView, DialogImpResListener {
    fun onCreateRecyclerView(dialog:BaseDialog):RecyclerView?

    fun onCreateItemLayoutRes():Int

    fun onCreateSelectStatusView(holder: BaseViewHolder):View?
    fun onCreateContentTextView(holder: BaseViewHolder):TextView?
    fun onCreateTitleImageView(holder: BaseViewHolder):ImageView?

    fun onCreateData():MutableList<DialogSelectMode>

    fun onDataCallBack(data:DialogSelectMode?)
}