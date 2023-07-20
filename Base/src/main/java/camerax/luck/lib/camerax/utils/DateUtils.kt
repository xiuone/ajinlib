package camerax.luck.lib.camerax.utils

import java.text.SimpleDateFormat

/**
 * @author：luck
 * @date：2021/11/29 8:33 下午
 * @describe：DateUtils
 */
object DateUtils {

    private val sf = SimpleDateFormat("yyyyMMddHHmmssSSS")
    /**
     * 根据时间戳创建文件名
     * @param prefix 前缀名
     * @return
     */
    fun getCreateFileName(prefix: String): String {
        val millis = System.currentTimeMillis()
        return prefix + sf.format(millis)
    }
}