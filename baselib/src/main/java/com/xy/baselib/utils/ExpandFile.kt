package com.xy.baselib.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.xy.baselib.R
import java.io.*



/***
 * 保存照片到本地--注意相关权限
 */
fun savePhoto(context: Context?, file: File): String? {
    val uri = Uri.fromFile(file)
    context?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    return file?.path
}

fun delFile(path:String?){
    if (path == null)return
    delFile(File((path)))
}
fun delFile(file:File?){
    if (file == null)return
    if (file.exists())
        file.delete()
}

fun delFileList(paths :List<String?>?){
    if (paths == null)return
    for (path in paths)
        delFile(path)
}

fun copy(context: Context, text: String) {
    val mClipData = ClipData.newPlainText(getString(context, R.string.app_name), text)
    (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
        mClipData
    )
    ToastUtils.show(context, "复制成功")
}