package com.xy.baselib.exp


fun String.subStringStart( checkChase: String): String {
    var url = this
    val len = url.indexOf(checkChase)
    if (len != -1)
        url = url.substring(0, len)
    return url
}


fun String.subStringEndEnd( checkChase: String): String {
    var url = this
    val len = url.lastIndexOf(checkChase!!)
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
    val len = url.indexOf(checkChase!!)
    if (len != -1) {
        url = if (len + 1 < url.length) {
            url.substring(len + 1)
        } else {
            ""
        }
    }
    return url
}