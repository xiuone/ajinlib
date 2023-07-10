package xy.xy.base.utils.exp

import android.content.Context
import android.os.Environment
import xy.xy.base.utils.Logger
import java.io.File


fun Context.getAppPath():String = filesDir.toString() + File.separator

private fun Context.getSdcardPath() :String{
    Logger.d("$this")
    return Environment.getExternalStorageDirectory().toString() + File.separator
}

fun Context.getDownPath():String = getSdcardPath() + "Download" + File.separator

fun Context.getAppDownPath(strRes:String?):String = getDownPath() + if (!strRes.isNullOrEmpty())(strRes + File.separator) else ""

/**
 * 获取文件夹路径    各种类型
 */
fun Context.getAppDir(strRes:String):String = getAppPath() + strRes + File.separator

fun Context.getAppImageDir():String = getAppDir("image")

fun Context.getAppVideoDir():String = getAppDir("video")

fun Context.getAppMusicDir():String= getAppDir("music")

fun Context.getAppDocumentDir():String = getAppDir("document")

fun Context.getAppApkDir():String = getAppDir("apk")

fun Context.getAppZipDir():String = getAppDir("zip")

/**
 * 获取文件路径    各种类型
 */
fun Context.getAppImagePath(fileName:String):String = getAppImageDir() + fileName


fun Context.getAppVideoPath(fileName:String):String = getAppVideoDir() + fileName

fun Context.getAppMusicPath(fileName:String):String = getAppMusicDir() + fileName

fun Context.getAppDocumentPath(fileName:String):String = getAppDocumentDir() + fileName

fun Context.getAppApkPath(fileName:String):String = getAppApkDir() + fileName

fun Context.getAppZipPath(fileName:String):String = getAppZipDir() + fileName




/**
 * 获取文件夹路径    各种类型
 */
fun Context.getSdImageDir(strRes:String?):String{
    return getAppDownPath(strRes) + "image" + File.separator
}

fun Context.getVideoDir(strRes:String?):String{
    return getAppDownPath(strRes) +"video" + File.separator
}

fun Context.getAudioDir(strRes:String?):String{
    return getAppDownPath(strRes) +"audio" + File.separator
}

fun Context.getMusicDir(strRes:String?):String{
    return getAppDownPath(strRes) +"music" + File.separator
}

fun Context.getDocumentDir(strRes:String?):String{
    return getAppDownPath(strRes) +"document" + File.separator
}

fun Context.getApkDir(strRes:String?):String{
    return getAppDownPath(strRes) +"apk" + File.separator
}

fun Context.getZipDir(strRes:String?):String{
    return getAppDownPath(strRes) +"zip" + File.separator
}

/**
 * 获取文件路径    各种类型
 */
fun Context.getSdImagePath(strRes:String?,fileName:String):String{
    return getSdImageDir( strRes) + fileName
}


fun Context.getSdVideoPath( strRes:String?,fileName:String):String{
    return getVideoDir( strRes) + fileName
}

fun Context.getSdMusicPath(strRes:String?,fileName:String):String{
    return getMusicDir( strRes) + fileName
}

fun Context.getSdDocumentPath( strRes:String?,fileName:String):String{
    return getDocumentDir( strRes) + fileName
}

fun Context.getSdApkPath( strRes:String?,fileName:String):String{
    return getApkDir( strRes) + fileName
}
fun Context.getSdZipPath( strRes:String?,fileName:String):String{
    return getZipDir( strRes) + fileName
}
