package com.xy.base.utils.permission

import android.content.Context
import android.widget.TextView
import com.xy.base.dialog.base.BaseCenterDialog
import com.xy.base.dialog.base.BaseDialog
import com.xy.base.dialog.listener.DialogCancelSureView
import com.xy.base.utils.exp.setOnClick

/**
 * Default Reason dialog to show if developers did not implement their own custom Reason dialog.
 *
 * @author guolin
 * @since 2020/8/27
 */
class PermissionDialogReason(context: Context,private val uiListener: ReasonUiListener) : BaseCenterDialog(context) {
    private val messageTv by lazy { uiListener.onCreateReasonMessageTextView(this) }
    private val cancelButton by lazy { uiListener.onCreateDialogCancelView(this) }
    private val sureButton by lazy { uiListener.onCreateDialogSureView(this) }
    private var actionListener: PermissionActionListener?=null

    override fun layoutRes(): Int = uiListener.onCreateReasonLayoutRes()

    override fun initView() {
        super.initView()
        cancelButton?.setOnClick{
            this.actionListener?.onPermissionCancelAction()
            dismiss()
        }
        sureButton?.setOnClick{
            this.actionListener?.onPermissionSureNextAction()
            dismiss()
        }
    }

    fun bindActionListener(actionListener: PermissionActionListener){
        this.actionListener = actionListener
    }

    override fun showDialog(any: Any?) {
        super.showDialog(any)
        if (any is String){
            messageTv?.text = any
        }
    }

    interface ReasonUiListener:DialogCancelSureView{
        fun onCreateReasonLayoutRes():Int
        fun onCreateReasonMessageTextView(dialog: BaseDialog):TextView?
    }
}