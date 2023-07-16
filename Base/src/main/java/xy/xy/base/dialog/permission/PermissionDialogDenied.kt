package xy.xy.base.dialog.permission

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.OnPermissionCallback
import xy.xy.base.dialog.base.BaseBottomDialog
import xy.xy.base.dialog.base.BaseDialog
import xy.xy.base.dialog.listener.DialogCancelSureView
import xy.xy.base.utils.exp.setOnClick

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
            dismiss()
        }
        sureButton?.setOnClick{
            dismiss()
            activity?.run {
                uiListener.onCreateDeniedAction(this,permissionList,callback)
            }
        }
    }

    fun showDialog(activity:Activity,string:SpannableString?,permission:MutableList<String>,callback: OnPermissionCallback?){
        super.showDialogBindActivity(activity)
        this.callback  = callback
        permissionList.clear()
        permissionList.addAll(permission)
        messageTv?.movementMethod = LinkMovementMethod.getInstance()
        messageTv?.text = string
    }


    interface DeniedUiListener:DialogCancelSureView{
        fun onCreateDeniedAction(activity: Activity,permission:MutableList<String>,callback: OnPermissionCallback?)
        fun onCreateDeniedLayoutRes(): Int
        fun onCreateDeniedMessageTextView(dialog: BaseDialog): TextView?
    }

}