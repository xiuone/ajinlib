package com.xy.base.utils.exp

import com.github.stuxuhai.jpinyin.PinyinException
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper


val latterHead by lazy {  "↑" }
val latterUnKnow by lazy { "#" }
val letterKey by lazy { arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I",
    "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#") }




fun String?.toDBC(): String {
    if (this == null)return ""
    val c = toCharArray()
    for (i in c.indices) {
        if (c[i].equals(12288)) {
            c[i] = 32.toChar()
            continue
        }
        if (c[i] > 65280.toChar() && c[i] < 65375.toChar()) c[i] = (c[i] - 65248)
    }
    return String(c)
}

fun String.subStringStartIndexAndMore( checkChase: Int): String {
    if (this.length > checkChase){
        return "${substring(0,checkChase)}..."
    }
    return this
}


fun String.subStringStart( checkChase: String): String {
    var url = this
    val len = url.indexOf(checkChase)
    if (len != -1)
        url = url.substring(0, len)
    return url
}


fun String.subStringEndEnd( checkChase: String): String {
    var url = this
    val len = url.lastIndexOf(checkChase)
    if (len != -1) {
        url = if (len + 1 < url.length) {
            url.substring(len + 1)
        } else {
            ""
        }
    }
    return url
}

fun String.subStringStartEnd(checkChase: String): String {
    var url = this
    val len = url.indexOf(checkChase)
    if (len != -1) {
        url = if (len + 1 < url.length) {
            url.substring(len + 1)
        } else {
            ""
        }
    }
    return url
}



/**
 * 获取拼音
 * @param name
 * @return
 */
fun String.getPinYin(de:String? = "#"): String {
    var name = this.trim()
    try {
        name =  PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE).toUpperCase()
        if (name.isNotEmpty()) return name
    } catch (e: PinyinException) {
        e.printStackTrace()
    }
    return de?:""
}

/**
 * 获取拼音首字母
 * @param name
 * @return
 */
fun String.getFirstPinYin(): String {
    var newName = getPinYin()
    if (newName.isEmpty()) return latterUnKnow
    newName = newName.substring(0, 1)
    for (index in letterKey.indices) {
        if (newName.toUpperCase() == letterKey[index]) {
            return newName.toUpperCase()
        }
    }
    return latterUnKnow
}