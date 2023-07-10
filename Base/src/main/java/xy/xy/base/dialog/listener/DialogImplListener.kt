package xy.xy.base.dialog.listener

import xy.xy.base.dialog.base.BaseDialog

interface DialogImplListener : DialogImpResListener {
    fun dialogProportion(): Double = 0.85
    fun dialogInitView(dialog: BaseDialog){}
    fun dialogShow(dialog: BaseDialog){}
    fun dialogShow(dialog: BaseDialog, any: Any?){}
    fun dialogDismiss(dialog: BaseDialog){}
}