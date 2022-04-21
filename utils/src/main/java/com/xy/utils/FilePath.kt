package com.xy.utils

import android.content.Context
import android.os.Environment
import androidx.annotation.StringRes
import java.io.File


private fun getSdcardPath() :String{
    return Environment.getExternalStorageDirectory().toString() + File.separator
}

private fun getDownPath():String{
    return getSdcardPath() + "Download" + File.separator
}

private fun getAppDownPath(context: Context,@StringRes strRes:Int):String{
    return getDownPath() + context.getResString(strRes) + File.separator
}

/**
 * 获取文件夹路径    各种类型
 */
fun getSdImageDir(context: Context,@StringRes strRes:Int):String{
    return getAppDownPath(context, strRes) + "image" + File.separator
}

fun getVideoDir(context: Context,@StringRes strRes:Int):String{
    return getAppDownPath(context, strRes) +"video" + File.separator
}

fun getMusicDir(context: Context,@StringRes strRes:Int):String{
    return getAppDownPath(context, strRes) +"music" + File.separator
}

fun getDocumentDir(context: Context,@StringRes strRes:Int):String{
    return getAppDownPath(context, strRes) +"document" + File.separator
}

fun getApkDir(context: Context,@StringRes strRes:Int):String{
    return getAppDownPath(context, strRes) +"apk" + File.separator
}

fun getZipDir(context: Context,@StringRes strRes:Int):String{
    return getAppDownPath(context, strRes) +"zip" + File.separator
}

/**
 * 获取文件路径    各种类型
 */
fun getSdImagePath(context: Context,@StringRes strRes:Int,fileName:String):String{
    return getSdImageDir(context, strRes) + fileName
}


fun getSdVideoPath(context: Context,@StringRes strRes:Int,fileName:String):String{
    return getVideoDir(context, strRes) + fileName
}

fun getSdMusicPath(context: Context,@StringRes strRes:Int,fileName:String):String{
    return getMusicDir(context, strRes) + fileName
}

fun getSdDocumentPath(context: Context,@StringRes strRes:Int,fileName:String):String{
    return getDocumentDir(context, strRes) + fileName
}

fun getSdApkPath(context: Context,@StringRes strRes:Int,fileName:String):String{
    return getApkDir(context, strRes) + fileName
}
fun getSdZipPath(context: Context,@StringRes strRes:Int,fileName:String):String{
    return getZipDir(context, strRes) + fileName
}