package com.xy.base.toast

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.LayoutRes

abstract class BaseToast (context: Context):Toast(context) {
    init {
        try {
            val view = LayoutInflater.from(context).inflate(layoutRes(), null)
            if (view != null) setView(view)
            setGravity(gravity(),0,0)
        }catch (e:Exception){
        }
    }

    @LayoutRes
    abstract fun layoutRes(): Int
    open fun gravity(): Int{
        return android.view.Gravity.BOTTOM
    }
}