package com.xy.base.utils.encrypt

import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

/**
 * 获取字符串MD5值，32字节字符串
 * @param val
 * @return
 */
fun String.getMd5(): String {
    if (isEmpty())return ""
    try {
        val md5: MessageDigest = MessageDigest.getInstance("MD5")
        md5.update(toByteArray())
        val m = md5.digest() // 加密
        return getString(m)
    } catch (e: NoSuchAlgorithmException) { }
    return this
}

private fun getString(b: ByteArray): String {
    val sb = StringBuffer()
    for (i in b.indices) {
        sb.append(String.format("%02x", b[i]))
    }
    return sb.toString()
}



fun InputStream.getMD5(): String? {
    val bufferSize = 256 * 1024
    var digestInputStream: DigestInputStream? = null
    return try {
        var messageDigest = MessageDigest.getInstance("MD5")
        digestInputStream = DigestInputStream(this, messageDigest)
        val buffer = ByteArray(bufferSize)
        while (digestInputStream.read(buffer) > 0);
        messageDigest = digestInputStream.messageDigest
        val resultByteArray = messageDigest.digest()
        byteArrayToHex(resultByteArray)
    } catch (e: java.lang.Exception) {
        null
    } finally {
        try {
            digestInputStream?.close()
        } catch (e: java.lang.Exception) {
        }
        try {
            this.close()
        } catch (e: java.lang.Exception) {
        }
    }
}

private fun toHex(b: ByteArray): String { // String to byte
    val sb = StringBuilder(b.size * 2)
    for (i in b.indices) {
        sb.append(HEX_DIGITS[(b[i] and 0xf0.toByte()).toInt().ushr(4) ])
        sb.append(HEX_DIGITS[(b[i] and 0x0f).toInt()])
    }
    return sb.toString()
}


fun byteArrayToHex(byteArray: ByteArray): String {
    val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val resultCharArray = CharArray(byteArray.size * 2)
    var index = 0
    for (b in byteArray) {
        resultCharArray[index++] = hexDigits[b.toInt().ushr(4)  and 0xf]
        resultCharArray[index++] = hexDigits[(b and 0xf).toInt()]
    }
    return String(resultCharArray)
}