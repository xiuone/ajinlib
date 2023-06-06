package com.xy.base.utils.exp

import android.content.Context
import com.xy.base.utils.encrypt.getMd5

fun Context.getAppUrlFile(url:String?,type: com.xy.base.utils.FileTypeEnum):String?{
    url?:return null
    val fileName = "${url.getMd5()}.${type.suffix}"
    val dir = when(type){
        com.xy.base.utils.FileTypeEnum.APK->getAppApkDir()
        com.xy.base.utils.FileTypeEnum.PNG, com.xy.base.utils.FileTypeEnum.JPEG, com.xy.base.utils.FileTypeEnum.JPG->getAppImageDir()
        com.xy.base.utils.FileTypeEnum.MP4->getAppVideoDir()
        com.xy.base.utils.FileTypeEnum.MP3->getAppMusicDir()
        com.xy.base.utils.FileTypeEnum.ZIP, com.xy.base.utils.FileTypeEnum.RAR->getAppZipDir()
    }
    val file = "$dir$fileName"
    file.createDirs()
    return file
}

/**
 * 获取域名
 */
fun String.getHost(): String {
    var content = this.replace("https", "")
    content = content.replace("http", "")
    content = content.replace("://", "")
    content = content.subStringStart( "?")
    content = content.subStringStart( "%")
    content = content.subStringStart( "/")
    content = content.subStringStart( ":")
    return content
}