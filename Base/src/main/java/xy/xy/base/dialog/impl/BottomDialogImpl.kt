package xy.xy.base.dialog.impl

import android.content.Context
import xy.xy.base.dialog.base.BaseBottomDialog
import xy.xy.base.dialog.listener.DialogImplListener

open class BottomDialogImpl(context: Context,private val listener: DialogImplListener) : BaseBottomDialog(context){

    override fun initView() {
        listener.dialogInitView(this)
    }

    override fun layoutRes(): Int  = listener.dialogLayoutRes()?: 0

    override fun proportion(): Double = listener.dialogProportion()

    fun show(any: Any?){
        super.show()
        listener.dialogShow(this,any)
    }

    override fun show() {
        super.show()
        listener.dialogShow(this)
    }
}