package com.xy.base.assembly.load

import com.xy.base.dialog.LoadProgressDialog

interface LoadDialogView  {
    fun onCreateLoadDialog(): LoadProgressDialog?
    fun loadProgressTvIdRes():Int
    fun loadProgressString():String?
}