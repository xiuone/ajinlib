package com.xy.baselib.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object CopyUtils {
    val TAG = "IMG"
    fun copy(context: Context, text: String) {
        val mClipData = ClipData.newPlainText(TAG, text)
        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
            mClipData
        )
        ToastUtils.show(context, "复制成功")
    }
}