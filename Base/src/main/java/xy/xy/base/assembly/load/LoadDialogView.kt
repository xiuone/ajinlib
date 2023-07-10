package xy.xy.base.assembly.load

import xy.xy.base.dialog.LoadProgressDialog

interface LoadDialogView  {
    fun onCreateLoadDialog(): LoadProgressDialog?
    fun loadProgressTvIdRes():Int
    fun loadProgressString():String?
}