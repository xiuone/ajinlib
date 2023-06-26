package com.xy.base.utils.permission

import android.content.Context
import android.widget.TextView
import com.xy.base.dialog.base.BaseBottomDialog
import com.xy.base.dialog.base.BaseCenterDialog
import com.xy.base.dialog.base.BaseDialog
import com.xy.base.dialog.listener.DialogCancelSureView
import com.xy.base.utils.exp.setOnClick

class PermissionDialogDenied(context: Context,private val uiListener:DeniedUiListener):
    BaseBottomDialog(context) {
    private val messageTv by lazy { uiListener.onCreateDeniedMessageTextView(this) }
    private val cancelButton by lazy { uiListener.onCreateDialogCancelView(this) }
    private val sureButton by lazy { uiListener.onCreateDialogSureView(this) }
    private var actionListener:PermissionActionListener?= null

    override fun layoutRes(): Int = uiListener.onCreateDeniedLayoutRes()

    override fun initView() {
        super.initView()
        cancelButton?.setOnClick{
            actionListener?.onPermissionCancelAction()
            dismiss()
        }
        sureButton?.setOnClick{
            actionListener?.onPermissionSureNextAction()
            dismiss()
        }
    }

    override fun showDialog(any: Any?) {
        super.showDialog(any)
        if (any is String){
            messageTv?.text = any
        }
    }

    fun bindActionListener(actionListener: PermissionActionListener){
        this.actionListener = actionListener
    }

    interface DeniedUiListener:DialogCancelSureView{
        fun onCreateDeniedLayoutRes(): Int
        fun onCreateDeniedMessageTextView(dialog: BaseDialog): TextView?
    }
}