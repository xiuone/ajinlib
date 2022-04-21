package com.xy.utils


/**
 * 用来做校验用的。。。。。。。。。。。
 */



/**
 * 验证手机格式
 */
fun String.isMobileNO(): Boolean {
    if (isNullOrEmpty())return false
    val telRegex = "[1][3456789]\\d{9}"
    return matches(Regex(telRegex, RegexOption.IGNORE_CASE))
}

/**
 * 验证身份证格式
 */
fun String.isIdCardNO(): Boolean {
    if (isNullOrEmpty())return false
    val idRegex = "[1-9]\\d{5}(18|19|20|(3\\d))\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$"
    return matches(Regex(idRegex, RegexOption.IGNORE_CASE))
}