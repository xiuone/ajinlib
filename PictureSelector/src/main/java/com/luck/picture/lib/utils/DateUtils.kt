package com.luck.picture.lib.utils

import android.annotation.SuppressLint
import android.content.Context
import com.luck.picture.lib.config.PictureMimeType.isContent
import com.luck.picture.lib.basic.PictureContentResolver.openInputStream
import com.luck.picture.lib.basic.PictureContentResolver.openOutputStream
import com.luck.picture.lib.immersive.RomUtils.isSamsung
import com.luck.picture.lib.thread.PictureThreadUtils.executeByIo
import com.luck.picture.lib.config.PictureMimeType.isHasAudio
import com.luck.picture.lib.config.PictureMimeType.isHasVideo
import com.luck.picture.lib.config.PictureMimeType.isHasGif
import com.luck.picture.lib.config.PictureMimeType.isUrlHasGif
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.thread.PictureThreadUtils.cancel
import com.luck.picture.lib.interfaces.OnCallbackListener.onCall
import com.luck.picture.lib.config.PictureMimeType.isHasImage
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import com.luck.picture.lib.app.PictureAppMaster.appContext
import com.luck.picture.lib.config.SelectMimeType.ofImage
import com.luck.picture.lib.config.PictureMimeType.getLastSourceSuffix
import com.luck.picture.lib.thread.PictureThreadUtils.isInUiThread
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.utils.FileDirMap
import com.luck.picture.lib.config.SelectorConfig
import androidx.core.content.FileProvider
import kotlin.jvm.JvmOverloads
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeCompat
import com.luck.picture.lib.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author：luck
 * @date：2017-5-25 23:30
 * @describe：DateUtils
 */
object DateUtils {
    @SuppressLint("SimpleDateFormat")
    private val SF = SimpleDateFormat("yyyyMMddHHmmssSSS")

    @SuppressLint("SimpleDateFormat")
    private val SDF = SimpleDateFormat("yyyy-MM")

    @SuppressLint("SimpleDateFormat")
    private val SDF_YEAR = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val currentTimeMillis: Long
        get() {
            val timeToString = ValueOf.toString(System.currentTimeMillis())
            return toLong(
                if (timeToString!!.length > 10) timeToString.substring(
                    0,
                    10
                ) else timeToString
            )
        }

    fun getDataFormat(context: Context, time: Long): String {
        var time = time
        time = if (time.toString().length > 10) time else time * 1000
        return if (isThisWeek(time)) {
            context.getString(R.string.ps_current_week)
        } else if (isThisMonth(time)) {
            context.getString(R.string.ps_current_month)
        } else {
            SDF.format(time)
        }
    }

    fun getYearDataFormat(time: Long): String {
        var time = time
        time = if (time.toString().length > 10) time else time * 1000
        return SDF_YEAR.format(time)
    }

    private fun isThisWeek(time: Long): Boolean {
        val calendar = Calendar.getInstance()
        val currentWeek = calendar[Calendar.WEEK_OF_YEAR]
        calendar.time = Date(time)
        val paramWeek = calendar[Calendar.WEEK_OF_YEAR]
        return paramWeek == currentWeek
    }

    fun isThisMonth(time: Long): Boolean {
        val date = Date(time)
        val param = SDF.format(date)
        val now = SDF.format(Date())
        return param == now
    }

    /**
     * millisecondToSecond
     *
     * @param duration millisecond
     * @return
     */
    fun millisecondToSecond(duration: Long): Long {
        return duration / 1000 * 1000
    }

    /**
     * 判断两个时间戳相差多少秒
     *
     * @param d
     * @return
     */
    fun dateDiffer(d: Long): Int {
        return try {
            val l1 = currentTimeMillis
            val interval = l1 - d
            Math.abs(interval).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * 时间戳转换成时间格式
     *
     * @param timeMs
     * @return
     */
    fun formatDurationTime(timeMs: Long): String {
        var timeMs = timeMs
        val prefix = if (timeMs < 0) "-" else ""
        timeMs = Math.abs(timeMs)
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) String.format(
            Locale.getDefault(),
            "%s%d:%02d:%02d",
            prefix,
            hours,
            minutes,
            seconds
        ) else String.format(
            Locale.getDefault(), "%s%02d:%02d", prefix, minutes, seconds
        )
    }

    /**
     * 根据时间戳创建文件名
     *
     * @param prefix 前缀名
     * @return
     */
    fun getCreateFileName(prefix: String): String {
        val millis = System.currentTimeMillis()
        return prefix + SF.format(millis)
    }

    /**
     * 根据时间戳创建文件名
     *
     * @return
     */
    val createFileName: String
        get() {
            val millis = System.currentTimeMillis()
            return SF.format(millis)
        }

    /**
     * 计算两个时间间隔
     *
     * @param sTime
     * @param eTime
     * @return
     */
    fun cdTime(sTime: Long, eTime: Long): String {
        val diff = eTime - sTime
        return if (diff > 1000) diff / 1000.toString() + "秒" else diff.toString() + "毫秒"
    }
}