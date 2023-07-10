package xy.xy.base.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import xy.xy.base.dialog.impl.CenterDialogImpl
import xy.xy.base.dialog.listener.DialogImplListener

class LoadProgressDialog (context: Context, listener: DialogImplListener): CenterDialogImpl(context,listener) {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        super.onCreate(savedInstanceState)
    }

    override fun showDialog(idRes: Int?, content: String?) {
        super.showDialog(idRes, content)
        setText(idRes, content)
    }

    override fun showDialog(idRes: Int?, content: String?, any: Any?) {
        super.showDialog(idRes, content, any)
        setText(idRes, content)
    }

    override fun showDialogBindActivity(activity: Activity?, idRes: Int?, content: String?, any: Any?) {
        super.showDialogBindActivity(activity, idRes, content, any)
        if (activity == null)return
        setText(idRes, content)
    }

    private fun setText(idRes: Int?, content: String?){
        if (idRes != null) {
            val textView: TextView? = findViewById(idRes)
            textView?.text = content
        }
    }
}