package com.xy.baselib.ui.dialog

import android.content.Context
import android.view.Window
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.xy.baselib.exp.getResString

class LoadProgressBaseDialog (context: Context,impl:DialogImpl): BaseImplDialog(context,impl) {

    override fun initView() {
        super.initView()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    fun show(@IdRes idRes: Int, @StringRes strRes:Int) {
        super.show()
        val textView:TextView? = findViewById(idRes)
        textView?.text = context.getResString(strRes)
    }
}