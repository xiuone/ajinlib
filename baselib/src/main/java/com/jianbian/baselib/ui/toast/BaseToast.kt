package com.jianbian.baselib.ui.toast

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.LayoutRes

abstract class BaseToast (context: Context):Toast(context) {
    init {
        try {
            val view = LayoutInflater.from(context).inflate(LayoutRes(), null)
            if (view != null)
                setView(view)
            setGravity(Gravity(),0,0)
        }catch (e:Exception){
            Log.e("==``","BaseToast:${e.message}")
        }
    }

    @LayoutRes
    abstract fun LayoutRes(): Int
    open fun Gravity(): Int{
        return android.view.Gravity.BOTTOM
    }
}