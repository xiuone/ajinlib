package com.xy.base.dialog.listener

import android.app.Activity
import androidx.annotation.IdRes

interface DialogActionListener {
    fun showDialog()
    fun showDialog(any: Any?)
    fun showDialog(any: Any?,content: String?)
    fun showDialog(@IdRes idRes: Int?, content:String?){}
    fun showDialog(@IdRes idRes: Int?, content:String?,any: Any?)

    fun showDialogBindActivity(activity: Activity?)
    fun showDialogBindActivity(activity: Activity?,any: Any?)
    fun showDialogBindActivity(activity: Activity?,idRes: Int?, content:String?)
    fun showDialogBindActivity(activity: Activity?,idRes: Int?, content:String?,any: Any?)

    fun dialogIsShow():Boolean

}