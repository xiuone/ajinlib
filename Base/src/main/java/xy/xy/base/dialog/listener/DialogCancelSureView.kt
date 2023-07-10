package xy.xy.base.dialog.listener

import android.view.View
import xy.xy.base.dialog.base.BaseDialog

interface DialogCancelSureView {
    fun onCreateDialogCancelView(dialog: BaseDialog): View?
    fun onCreateDialogSureView(dialog: BaseDialog): View?
}