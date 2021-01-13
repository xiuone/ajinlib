package com.xy.baselib.utils

import android.text.TextUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object ExamineUtils {
    /**
     * 验证手机格式
     */
    fun isMobileNO(mobiles: String): Boolean {
        val telRegex = "[1][3456789]\\d{9}"
        return if (TextUtils.isEmpty(mobiles)) {
            false
        } else {
            mobiles.matches(Regex(telRegex, RegexOption.IGNORE_CASE))
        }
    }

    fun checkDate(date: String, format: String?): Boolean {
        val df: DateFormat = SimpleDateFormat(format)
        var d: Date? = null
        d = try {
            df.parse(date)
        } catch (e: Exception) {
            return false
        }
        val s1 = df.format(d)
        return date == s1
    }

    /**
     * 验证身份证格式
     */
    fun isIdCardNO(idCard: String): Boolean {
        /**
         * 1．号码的结构 　　- 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。
         * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。 　　
         * 2．地址码 　　表示编码对象常住户口所在县（县级市、旗、区）的行政区划代码，按GB/T2260的规定执行。 　　
         * 3．出生日期码 　　表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。 　　
         * 4．顺序码 　　表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配给女性。 　　
         * 5．校验码 　　根据前面十七位数字码，按照ISO 7064:1983.MOD 11-2校验码计算出来的检验码。
         * ————————————————
         * 版权声明：本文为CSDN博主「qiphon3650」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
         * 原文链接：https://blog.csdn.net/qiphon3650/article/details/95541641
         */
        val idRegex =
            "[1-9]\\d{5}(18|19|20|(3\\d))\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$"
        return if (TextUtils.isEmpty(idCard)) {
            false
        } else {
            idCard.matches(Regex(idRegex, RegexOption.IGNORE_CASE))
        }
    }

}