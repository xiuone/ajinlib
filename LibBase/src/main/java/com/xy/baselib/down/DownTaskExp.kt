package com.xy.baselib.down

import android.content.Context
import android.text.TextUtils
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.common.HttpOption
import com.arialyy.aria.core.task.DownloadTask
import com.xy.baselib.R
import com.xy.baselib.exp.showToast

fun DownloadTask.getDownloadId():Long{
    var did: Long = -1
    if (downloadEntity != null) {
        did = downloadEntity.id
    }
    return did
}

fun DownloadTask.getDownProportion():Float{
    return percent * 1.0f / 100
}

fun Context.delDownRecord(path:String){
    Aria.get(this).delRecord(1, path, true)
}

fun Context.delDownRecord(type:Int,path:String){
    Aria.get(this).delRecord(type, path, true)
}


fun Context.delDownRecord(type:Int,path:String,deleteFile: Boolean){
    Aria.get(this).delRecord(type, path, deleteFile)
}

fun Context.cancelDownRecord(taskId: Long){
    Aria.download(this).load(taskId).cancel()
}

fun Context.downloadFile(type: Int,url: String?, savePath: String?, heads: HashMap<String?, String?>?):Long{
    val downFileErrorCode = -1L;
    if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savePath)) {
        return downFileErrorCode
    }
    val httpOption = HttpOption()
    if (heads != null) {
        httpOption.addHeaders(heads)
    }
    val downloadId = Aria.download(this)
        .load(url) //读取下载地址
        .option(httpOption)
        .setFilePath(savePath, true) //设置文件保存的完整路径
        .create() //启动下载
    if (downFileErrorCode == downloadId){
        showToast( getString(R.string.download_add_fail))
    }
    return downloadId;
}