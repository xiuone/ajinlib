package com.xy.baselib.exp

import android.content.Context
import android.os.Environment
import androidx.annotation.StringRes
import java.io.File


private fun getSdcardPath() :String{
    return Environment.getExternalStorageDirectory().toString() + File.separator
}

fun Context.getDownPath():String{
    return getSdcardPath() + "Download" + File.separator
}

fun getAppDownPath(context: Context, strRes:String):String{
    return context.getDownPath() + strRes + File.separator
}

/**
 * 获取文件夹路径    各种类型
 */
fun Context.getSdImageDir(strRes:String):String{
    return getAppDownPath(this, strRes) + "image" + File.separator
}

fun Context.getVideoDir(strRes:String):String{
    return getAppDownPath(this, strRes) +"video" + File.separator
}

fun Context.getMusicDir(strRes:String):String{
    return getAppDownPath(this, strRes) +"music" + File.separator
}

fun Context.getDocumentDir(strRes:String):String{
    return getAppDownPath(this, strRes) +"document" + File.separator
}

fun Context.getApkDir(strRes:String):String{
    return getAppDownPath(this, strRes) +"apk" + File.separator
}

fun Context.getZipDir(strRes:String):String{
    return getAppDownPath(this, strRes) +"zip" + File.separator
}

/**
 * 获取文件路径    各种类型
 */
fun Context.getSdImagePath(strRes:String,fileName:String):String{
    return getSdImageDir( strRes) + fileName
}


fun Context.getSdVideoPath( strRes:String,fileName:String):String{
    return getVideoDir( strRes) + fileName
}

fun Context.getSdMusicPath(strRes:String,fileName:String):String{
    return getMusicDir( strRes) + fileName
}

fun Context.getSdDocumentPath( strRes:String,fileName:String):String{
    return getDocumentDir( strRes) + fileName
}

fun Context.getSdApkPath( strRes:String,fileName:String):String{
    return getApkDir( strRes) + fileName
}
fun Context.getSdZipPath( strRes:String,fileName:String):String{
    return getZipDir( strRes) + fileName
}