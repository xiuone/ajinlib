package com.xy.baselib.ui.dialog

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.Window
import android.widget.TextView
import com.xy.baselib.R

class LoadingDialog (context: Context): Dialog(context) {
    private var tvLoad: TextView? = null
    init {
        val dialogWindow: Window? = this.window
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_common_load)
        tvLoad = findViewById<TextView>(R.id.tvLoad)
        dialogWindow?.setBackgroundDrawableResource(R.color.transparent)
        dialogWindow?.setGravity(Gravity.CENTER)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    fun setText(str:String?){
        if (!TextUtils.isEmpty(str))
            tvLoad?.text = str
    }
}