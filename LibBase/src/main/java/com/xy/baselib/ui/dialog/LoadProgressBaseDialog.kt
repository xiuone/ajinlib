package com.xy.baselib.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.xy.baselib.exp.getResString

class LoadProgressBaseDialog (context: Context,impl:DialogImpl): BaseImplDialog(context,impl) {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        super.onCreate(savedInstanceState)
    }

    fun show(@IdRes idRes: Int, @StringRes strRes:Int) {
        super.show()
        val textView:TextView? = findViewById(idRes)
        textView?.text = context.getResString(strRes)
    }

    fun show(@IdRes idRes: Int,  content:String) {
        super.show()
        val textView:TextView? = findViewById(idRes)
        textView?.text = content
    }
}