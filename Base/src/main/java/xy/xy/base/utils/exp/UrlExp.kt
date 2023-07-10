package xy.xy.base.utils.exp

import android.content.Context
import xy.xy.base.utils.encrypt.getMd5

fun Context.getAppUrlFile(url:String?,type: xy.xy.base.utils.FileTypeEnum):String?{
    url?:return null
    val fileName = "${url.getMd5()}.${type.suffix}"
    val dir = when(type){
        xy.xy.base.utils.FileTypeEnum.APK->getAppApkDir()
        xy.xy.base.utils.FileTypeEnum.PNG, xy.xy.base.utils.FileTypeEnum.JPEG, xy.xy.base.utils.FileTypeEnum.JPG->getAppImageDir()
        xy.xy.base.utils.FileTypeEnum.MP4->getAppVideoDir()
        xy.xy.base.utils.FileTypeEnum.MP3->getAppMusicDir()
        xy.xy.base.utils.FileTypeEnum.ZIP, xy.xy.base.utils.FileTypeEnum.RAR->getAppZipDir()
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