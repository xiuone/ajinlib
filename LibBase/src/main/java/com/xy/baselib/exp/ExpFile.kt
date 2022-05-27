package com.xy.baselib.exp

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.*
import java.math.BigDecimal


private const val TAG = "FileUtils"

//修改文件权限
fun String.setPermission() {
    val command = "chmod 777 $this"
    val runtime = Runtime.getRuntime()
    try {
        runtime.exec(command)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}


private fun String.createDir() {
    val file = File(this)
    if (!file.exists()) {
        file.mkdirs()
    }
}

fun String.createDirs(){
    val file = File(this)
    val parentFile = file.parentFile
    if (!parentFile.exists()) {
        parentFile.toString().createDirs()
        parentFile.toString().createDir()
    }
}

fun String.isFileExist(): Boolean {
    val file = File(this)
    return !file.isDirectory && file.exists()
}

fun String.isDirExist(): Boolean {
    val file = File(this)
    return file.isDirectory && file.exists()
}


/**
 * 格式化单位
 */
fun Long.getFormatSize(): String {
    val kiloByte = this.toDouble() / 1024
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
 * 获取文件
 */
fun String.getFileSize(): Long {
    var size = 0.0
    val file = File(this);
    if (!file.exists()) return 0
    if (file.isFile){
        return file.length()
    }else{
        val fileList = file.listFiles()
        var size = 0L
        for (i in fileList.indices){
            val childFile = fileList[i]
            if (childFile.isFile){
                size +=  childFile.length()
            }else {
                size += childFile.toString().getFileSize()
            }
        }
        return size;
    }
}




/**
 * 删除某个文件
 */
fun String.deleteFile(): Boolean {
    val dir = File(this)
    if (dir.exists() && dir.isDirectory){
        val children: Array<String> = dir.list()
        for (i in children.indices) {
            val childFile = File(dir,children[i])
            if (childFile.isFile)
                childFile.delete()
            else
                childFile.toString().deleteFile()
        }
        return dir.delete();
    }else{
        return dir.delete();
    }
}


/**
 * 复制文件
 * @param from
 * @param toPathName
 * @return
 */
fun InputStream.copyFile(toPathName: String?): Boolean {
    var outPutStream: OutputStream? = null
    try {
        toPathName?.createDirs()
        toPathName?.deleteFile()
        outPutStream = BufferedOutputStream(FileOutputStream(toPathName))
        val buf = ByteArray(1024)
        var c: Int
        while (read(buf).also { c = it } > 0) {
            outPutStream.write(buf, 0, c)
        }
         return true
    } catch (ex: Exception) {
        ex.printStackTrace()
        false
    } finally {
        outPutStream?.closeRe()
        closeRe()
    }
    return false
}



fun String.reFileName(newPathName: String?): Boolean {
    newPathName?:return false
    newPathName?.createDirs()
    newPathName?.deleteFile()
    val file = File(this)
    file.renameTo(File(newPathName))
    return true
}


/**
 * 保存设置数据到文件
 * @param context
 * @param text
 * @return
 */
fun Context.saveDataToFile( text: String): String? {
    val path = filesDir.toString() + File.separator + System.currentTimeMillis() + ".data"
    Log.e(TAG, "save_path::$path")
    val file = File(path)
    if (file.exists()) {
        file.delete()
    }
    var raf: RandomAccessFile? = null
    try {
        file.createNewFile()
        raf = RandomAccessFile(file, "rwd")
        raf.seek(file.length())
        raf.write(text.toByteArray())
        return path
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        if (raf != null) {
            try {
                raf.closeRe()
            } catch (ignore: Exception) {
            }
        }
    }
    return null
}

/**
 * 写入数据
 */
fun String.writeFile(filePathName: String?): Boolean {
    try {
        return toByteArray(charset("utf-8")).writeFile( filePathName)
    } catch (ex: UnsupportedEncodingException) {
        ex.printStackTrace()
    }
    return false
}

/**
 * 将字节数据写入到文件
 * <br></br>覆盖
 * @param data
 * @param filePathName
 * @return
 */
fun ByteArray.writeFile(filePathName: String?): Boolean {
    var outStream: OutputStream? = null
    try {
        val destFile = File(filePathName)
        if (destFile.exists()) {
            destFile.delete()
        } else {
            destFile.createNewFile()
        }
        outStream = BufferedOutputStream(FileOutputStream(filePathName))
        outStream.write(this)
        outStream.flush()
    } catch (ex: Exception) {
        return false
    } finally {
        if (outStream != null) {
            try {
                outStream.closeRe()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    return true
}


fun String.readFile(): String {
    var bis: BufferedInputStream? = null
    var baos: ByteArrayOutputStream? = null
    try {
        val byteInSize = 4096
        val file = File(this)
        if (!isFileExist())return ""
        baos = ByteArrayOutputStream()
        bis = BufferedInputStream(FileInputStream(file))
        var length = 0
        val buffer = ByteArray(byteInSize)
        while (bis.read(buffer, 0, byteInSize).also { length = it } > 0) {
            baos.write(buffer, 0, length)
        }
        if (baos.size() > 0) {
            return String(baos.toByteArray())
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        bis?.closeRe()
        baos?.closeRe()
    }
    return ""
}

fun Context.readAssets( fileName: String?): String? {
    try {
        val `is` = assets.open(fileName!!)
        val lenght = `is`.available()
        val buffer = ByteArray(lenght)
        `is`.read(buffer)
        return String(buffer,charset("utf-8"))
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
    return null
}


/**
 * 获取缓存值
 */
fun Context.getTotalCacheSize(): Long {
    var cacheSize: Long = cacheDir.toString().getFileSize()
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        cacheSize += externalCacheDir.toString().getFileSize()
    }
    return cacheSize
}

/**
 * 获取缓存值
 */
fun Context.getTotalCacheFormatSize(): String {
    return getTotalCacheSize().getFormatSize()
}

/**
 * 清除所有缓存
 */
fun Context.clearAllCache() {
    cacheDir.toString().deleteFile()
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        externalCacheDir.toString().deleteFile()
    }
}

fun Closeable.closeRe() {
    try {
        close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

