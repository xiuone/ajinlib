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

class PermissionDialogDenied(context: Context,private val uiListener:DeniedUiListener): BaseBottomDialog(context) {
    private val messageTv by lazy { uiListener.onCreateDeniedMessageTextView(this) }
    private val cancelButton by lazy { uiListener.onCreateDialogCancelView(this) }
    private val sureButton by lazy { uiListener.onCreateDialogSureView(this) }
    private val permissionList by lazy { ArrayList<String>() }
    private var callback: OnPermissionCallback?= null

    override fun layoutRes(): Int = uiListener.onCreateDeniedLayoutRes()

    override fun initView() {
        super.initView()
        cancelButton?.setOnClick{
            activity?.run {
                if (this is FragmentActivity){
                    uiListener.onCreateDeniedAction(this,permissionList,callback)
                }
            }
            dismiss()
        }
        sureButton?.setOnClick{
            dismiss()
        }
    }

    fun showDialog(activity:FragmentActivity,string:SpannableString?,permission:ArrayList<String>,callback: OnPermissionCallback?){
        super.showDialogBindActivity(activity)
        this.callback  = callback
        permissionList.clear()
        permissionList.addAll(permission)
        messageTv?.movementMethod = LinkMovementMethod.getInstance()
        messageTv?.text = string
    }


    interface DeniedUiListener:DialogCancelSureView{
        fun onCreateDeniedAction(activity: FragmentActivity,permission:ArrayList<String>,callback: OnPermissionCallback?)
        fun onCreateDeniedLayoutRes(): Int
        fun onCreateDeniedMessageTextView(dialog: BaseDialog): TextView?
    }

}