package com.jianbian.baselib.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast

object ToastUtils {
    fun show(context: Context?, title: String?) {
        if (context == null || TextUtils.isEmpty(title))
            return
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
        }
    }
}
