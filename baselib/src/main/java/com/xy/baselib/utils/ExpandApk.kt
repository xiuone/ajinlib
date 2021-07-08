package com.xy.baselib.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import java.io.File
import java.math.BigDecimal
import java.util.*

/**
 * 设置点击事件监听
 */
var clickedTime = 0L
fun View.setOnClick(listener:View.OnClickListener) {
    this.setOnClickListener {
        if (System.currentTimeMillis() > (clickedTime+500)){
            clickedTime = System.currentTimeMillis()
            listener.onClick(this)
        }
    }
}


/**
 * 获取制定包名应用的版本的versionCode
 *
 * @param context
 * @param
 * @return
 */
fun getVersionCode(context: Context, packageName: String): Int {
    return try {
        val manager = context.packageManager
        val info = manager.getPackageInfo(packageName, 0)
        info.versionCode
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}


/**
 * 获取制定包名应用的版本的versionCode
 *
 * @param
 * @return
 */
fun getVersionName(context: Context): String{
    return try {
        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, 0)
        info.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun getDeviceId(context: Context?): String? {
    var deviceId: String? = null
    if (TextUtils.isEmpty(deviceId) && context != null) {
        deviceId = getUniqueID(context)
    }
    if (TextUtils.isEmpty(deviceId))
        deviceId = "0000000000"
    return deviceId
}

private fun getUniqueID(context: Context): String? {
    var id = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    if (TextUtils.isEmpty(id)) {
        id = getUUID()
    }
    return if (TextUtils.isEmpty(id)) UUID.randomUUID().toString() else id
}

@SuppressLint("MissingPermission")
private fun getUUID(): String? {
    var serial: String? = null
    val idShort =
        "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10 //13 位
    try {
        serial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Build.getSerial() // TODO crash in Q
        } else {
            Build.SERIAL
        }
        return UUID(idShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    } catch (exception: java.lang.Exception) {
        serial = "serial" // 随便一个初始化
    }

    //使用硬件信息拼凑出来的15位号码
    return UUID(idShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
}

/**
 * 获取缓存值
 */
fun getTotalCacheSize(context: Context): String? {
    var cacheSize: Double = getFolderSize(context.cacheDir)
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        cacheSize += getFolderSize(context.externalCacheDir)
    }
    return getFormatSize(cacheSize)
}

/**
 * 清除所有缓存
 */
fun clearAllCache(context: Context) {
    deleteDir(context.cacheDir)
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        deleteDir(context.externalCacheDir)
    }
}

/**
 * 格式化单位
 */
private fun getFormatSize(size: Double): String? {
    val kiloByte = size / 1024
    val megaByte = kiloByte / 1024
    val gigaByte = megaByte / 1024
    if (gigaByte < 1) {
        val result2 = BigDecimal(megaByte.toString())
        return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "MB"
    }
    val teraBytes = gigaByte / 1024
    if (teraBytes < 1) {
        val result3 = BigDecimal(gigaByte.toString())
        return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "GB"
    }
    val result4: BigDecimal = BigDecimal.valueOf(teraBytes)
    return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "TB"
}

/**
 * 删除某个文件
 */
private fun deleteDir(dir: File?): Boolean {
    if (dir != null && dir.isDirectory) {
        val children: Array<String> = dir.list()
        for (i in children.indices) {
            val success = deleteDir(File(dir, children[i]))
            if (!success) {
                return false
            }
        }
        return dir.delete()
    }
    return dir?.delete() ?: false
}


/**
 * 获取文件
 */
private fun getFolderSize(file: File?): Double {
    var size = 0.0
    if (file != null) {
        val fileList = file.listFiles()
        if (fileList != null && fileList.isNotEmpty()) {
            for (i in fileList.indices) {
                // 如果下面还有文件
                size = if (fileList[i].isDirectory) {
                    size + getFolderSize(fileList[i])
                } else {
                    size + fileList[i].length()
                }
            }
        }
    }
    return size
}



