package xy.xy.base.utils.exp

import xy.xy.base.utils.Logger
import java.util.*
import kotlin.math.abs

/**
 * 获取随机昵称
 */
fun Random.getRandomName(): String {
    val len = abs(nextInt(3)) +4
    var ret = StringBuffer()
    for (i in 0 until len) {
        val hightPos = 176 + abs(nextInt(39)) // 获取高位值
        val lowPos = 161 + abs(nextInt(93)) // 获取低位值
        val byteArray = ByteArray(2)
        byteArray[0] = hightPos.toByte()
        byteArray[1] = lowPos.toByte()
        val str = byteArray.toStringStr()
        ret.append(str)
    }
    return ret.toString()
}

/**
 *转成String
 */
private fun ByteArray.toStringStr():String{
    try {
        return String(this)
    } catch (ex: Exception) {
        ex.printStackTrace()
        Logger.e("=======ByteArray 转String失败${ex.message}")
    }
    return ""
}