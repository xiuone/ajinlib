package com.xy.base.utils.exp

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

private const val nameKey = "name"//名字
private const val birthDayKey = "date"//生日
private const val sexKey = "sex"//生日
private const val tokenKey = "token"//生日
private const val info = "info"//提示信息
private const val id = "id"//id信息
private const val id2 = "id2"//id信息
private const val friend = "friend"//朋友
private const val video = "video"//视频
private const val typeStr = "type"//类型
private const val position = "position"//类型
private const val page = "page"//类型
private const val status = "status"//类型
private const val latitude = "latitude"//经纬度
private const val longitude = "longitude"//经纬度
private const val title = "title"//经纬度
private const val tag = "tag"//经纬度
private const val url = "url"//链接
private const val html = "html"//文本

/**
 * token
 */
fun Intent?.putToken(token:String?):Intent?{
    this?.putExtra(tokenKey,token)
    return this
}

fun Intent?.getToken():String? = this?.getStringExtra(tokenKey)


//性别
fun Intent?.putSex(sex:Int):Intent?{
    this?.putExtra(sexKey,sex)
    return this
}

fun Intent?.getSex(def:Int):Int = this?.getIntExtra(sexKey,def)?:def

/**
 * 昵称
 */
fun Intent?.putName(name:String?):Intent?{
    this?.putExtra(nameKey,name)
    return this
}

fun Intent?.getName():String? = this?.getStringExtra(nameKey)

/**
 * 生日
 */
fun Intent?.putBirthDay(birthDay:String?):Intent?{
    this?.putExtra(birthDayKey,birthDay)
    return this
}

fun Intent?.getBirthDay():String? = this?.getStringExtra(birthDayKey)

/**
 * info
 */
fun Intent?.putInfo(string: String?):Intent?{
    this?.putExtra(info,string)
    return this
}
fun Bundle?.putInfo(string: String?):Bundle?{
    this?.putString(info,string)
    return this
}
fun Intent?.putInfoSerializable(value: Serializable?):Intent?{
    this?.putExtra(info,value)
    return this
}
fun Bundle?.putInfoSerializable(value: Serializable?):Bundle?{
    this?.putSerializable(info,value)
    return this
}
fun Intent?.putInfoParcelable(value: Parcelable?):Intent?{
    this?.putExtra(info,value)
    return this
}

fun Intent?.putInfoArrayStringList(string: MutableList<String>?):Intent?{
    if (string is ArrayList<String>)
        this?.putStringArrayListExtra(info,string)
    return this
}


fun Intent?.getInfo():String? = this?.getStringExtra(info)
fun Bundle?.getInfo():String? = this?.getString(info)

fun Intent?.getInfoArrayStringList():MutableList<String> = this?.getStringArrayListExtra(info)?:ArrayList()

fun Intent?.getInfoSerializable() = this?.getSerializableExtra(info)
fun Bundle?.getInfoSerializable() = this?.getSerializable(info)
fun <T :Parcelable> Intent?.getInfoInfoParcelable() = this?.getParcelableExtra<T>(info)



/**
 * 类型
 */
fun Intent?.putTypeStr(value: String):Intent?{
    this?.putExtra(typeStr,value)
    return this
}
fun Intent?.putTypeInt(value: Int):Intent?{
    this?.putExtra(typeStr,value)
    return this
}
fun Intent?.putTypeLong(value: Long):Intent?{
    this?.putExtra(typeStr,value)
    return this
}
fun Bundle?.putTypeInt(value: Int):Bundle?{
    this?.putInt(typeStr,value)
    return this
}
fun Intent?.getTypeStr():String? = this?.getStringExtra(typeStr)
fun Intent?.getTypeInt(def:Int = 0):Int = this?.getIntExtra(typeStr,def)?:def
fun Intent?.getTypeLong(def:Long = 0):Long = this?.getLongExtra(typeStr,def)?:def
fun Bundle?.getTypeInt(def:Int = 0):Int = this?.getInt(typeStr)?:def
fun Bundle?.getTypeLong(def:Long = 0):Long = this?.getLong(typeStr)?:def

fun Intent?.putTitle(value: String?):Intent?{
    this?.putExtra(title,value)
    return this
}

fun Intent?.getTitle():String? = this?.getStringExtra(title)


/**
 * id
 */
fun Intent?.putIdStr(string: String?):Intent?{
    this?.putExtra(id,string)
    return this
}
fun Intent?.putId2Str(string: String?):Intent?{
    this?.putExtra(id2,string)
    return this
}
fun Intent?.putIdLong(string: Long):Intent?{
    this?.putExtra(id,string)
    return this
}
fun Bundle?.putIdLong(string: Long):Bundle?{
    this?.putLong(id,string)
    return this
}

fun Intent?.getIdStr():String? = this?.getStringExtra(id)
fun Intent?.getId2Str():String? = this?.getStringExtra(id2)
fun Intent?.getIdLong(def:Long = 0):Long = this?.getLongExtra(id,def)?:def
fun Bundle?.getIdLong(def: Long = 0):Long = this?.getLong(id,def)?:def


/**
 * friend
 */
fun Intent?.putFriendStatus(value: Boolean):Intent?{
    this?.putExtra(friend,value)
    return this
}
fun Bundle?.putFriendStatus(value: Boolean):Bundle?{
    this?.putBoolean(friend,value)
    return this
}
fun Intent?.getFriendStatus():Boolean = this?.getBooleanExtra(friend,true)?:true
fun Bundle?.getFriendStatus():Boolean = this?.getBoolean(friend,true)?:true


/**
 * video
 */
fun Intent?.putVideoStatus(value: Boolean):Intent?{
    this?.putExtra(video,value)
    return this
}
fun Bundle?.putVideoStatus(value: Boolean):Bundle?{
    this?.putBoolean(video,value)
    return this
}


fun Intent?.getVideoStatus():Boolean = this?.getBooleanExtra(video,true)?:true
fun Bundle?.getVideoStatus():Boolean = this?.getBoolean(video,true)?:true


fun Intent?.putPositionInt(value: Int):Intent?{
    this?.putExtra(position,value)
    return this
}

fun Bundle?.putPositionInt(value: Int):Bundle?{
    this?.putInt(position,value)
    return this
}
fun Intent?.getPosition(def:Int=0):Int = this?.getIntExtra(position,def)?:def
fun Bundle?.getPosition(def:Int=0):Int = this?.getInt(position,def)?:def


fun Intent?.putPageInt(value: Int):Intent?{
    this?.putExtra(page,value)
    return this
}
fun Bundle?.putPageInt(value: Int):Bundle?{
    this?.putInt(page,value)
    return this
}

fun Intent?.getPageInt(def:Int=0):Int = this?.getIntExtra(page,def)?:def
fun Bundle?.getPage(def:Int=0):Int = this?.getInt(page,def)?:def

fun Intent?.putTagInt(value: Int):Intent?{
    this?.putExtra(tag,value)
    return this
}
fun Intent?.putTagString(value: String?):Intent?{
    this?.putExtra(tag,value)
    return this
}
fun Bundle?.putTagInt(value: Int):Bundle?{
    this?.putInt(tag,value)
    return this
}
fun Bundle?.putTagString(value: String?):Bundle?{
    this?.putString(tag,value)
    return this
}
fun Intent?.getTagInt(def:Int=0):Int = this?.getIntExtra(tag,def)?:def
fun Intent?.getTagString(def:String?=null):String? = this?.getStringExtra(tag)?:def
fun Bundle?.getTagInt(def:Int=0):Int = this?.getInt(tag,def)?:def
fun Bundle?.getTagString(def:String?=null):String? = this?.getString(tag,def)?:def



fun Intent?.putStatusBoolean(value: Boolean):Intent?{
    this?.putExtra(status,value)
    return this
}
fun Bundle?.putStatusBoolean(value: Boolean):Bundle?{
    this?.putBoolean(tag,value)
    return this
}
fun Intent?.getStatusBoolean(def:Boolean = false):Boolean = this?.getBooleanExtra(position,def)?:def
fun Bundle?.getStatusBoolean(def:Boolean = false):Boolean = this?.getBoolean(position,def)?:def


fun Intent?.putUrl(value: String?):Intent?{
    this?.putExtra(url,value)
    return this
}
fun Intent?.putHtml(value: String?):Intent?{
    this?.putExtra(html,value)
    return this
}
fun Intent?.getUrl(def:String?=null):String? = this?.getStringExtra(url)?:def
fun Intent?.getHtml(def:String?=null):String? = this?.getStringExtra(html)?:def




fun Intent?.putLatitude(value:Double?):Intent?{
    if (value != null){
        this?.putExtra(latitude,value)
    }
    return this
}
fun Intent?.getLatitude(def:Double) :Double= this?.getDoubleExtra(latitude,def)?:def

fun Intent?.putLongitude(value:Double?):Intent?{
    if (value != null){
        this?.putExtra(longitude,value)
    }
    return this
}
fun Intent?.getLongitude(def:Double) :Double= this?.getDoubleExtra(longitude,def)?:def

