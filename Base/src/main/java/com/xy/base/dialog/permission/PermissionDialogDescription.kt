package com.xy.base.dialog.permission

import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.xy.base.dialog.base.BaseBottomDialog
import com.xy.base.dialog.base.BaseDialog
import com.xy.base.dialog.listener.DialogCancelSureView
import com.xy.base.permission.OnPermissionCallback
import com.xy.base.utils.exp.setOnClick

/**
 * Default Reason dialog to show if developers did not implement their own custom Reason dialog.
 *
 * @author guolin
 * @since 2020/8/27
 */
class PermissionDialogDescription(context: Context, private val uiListener: ReasonUiListener) : BaseBottomDialog(context) {
    private val messageTv by lazy { uiListener.onCreateReasonMessageTextView(this) }
    private val cancelButton by lazy { uiListener.onCreateDialogCancelView(this) }
    private val sureButton by lazy { uiListener.onCreateDialogSureView(this) }
    private val permissionList by lazy { ArrayList<String>() }
    private var callback: OnPermissionCallback?= null

    override fun layoutRes(): Int = uiListener.onCreateReasonLayoutRes()

    override fun initView() {
        super.initView()
        cancelButton?.setOnClick{
            activity?.run {
                if (this is FragmentActivity) {
                    uiListener.onCreateReasonDescription(this, permissionList, callback)
                }
            }
            dismiss()
        }
        sureButton?.setOnClick{
            dismiss()
        }
    }

    fun showDialog(activity: FragmentActivity,string:SpannableString?, permission: ArrayList<String>, callback: OnPermissionCallback?){
        super.showDialogBindActivity(activity)
        this.callback  = callback
        permissionList.clear()
        permissionList.addAll(permission)
        messageTv?.movementMethod = LinkMovementMethod.getInstance()
        messageTv?.text = string
    }

    interface ReasonUiListener:DialogCancelSureView{
        fun onCreateReasonDescription(activity: FragmentActivity, permission: ArrayList<String>, callback: OnPermissionCallback?)
        fun onCreateReasonLayoutRes():Int
        fun onCreateReasonMessageTextView(dialog: BaseDialog):TextView?
    }
}