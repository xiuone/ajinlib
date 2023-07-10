package com.luck.picture.lib.utils

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
import java.lang.Exception

/**
 * @author：luck
 * @date：2019-11-12 14:27
 * @describe：类型转换工具类
 */
object ValueOf {
    @kotlin.jvm.JvmStatic
    fun toString(o: Any): String {
        var value = ""
        try {
            value = o.toString()
        } catch (e: Exception) {
        }
        return value
    }

    @JvmOverloads
    fun toDouble(o: Any?, defaultValue: Int = 0): Double {
        if (o == null) {
            return defaultValue.toDouble()
        }
        val value: Double
        value = try {
            o.toString().trim { it <= ' ' }.toDouble()
        } catch (e: Exception) {
            defaultValue.toDouble()
        }
        return value
    }

    @JvmOverloads
    fun toLong(o: Any?, defaultValue: Long = 0): Long {
        if (o == null) {
            return defaultValue
        }
        var value: Long = 0
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if (s.contains(".")) {
                s.substring(0, s.lastIndexOf(".")).toLong()
            } else {
                s.toLong()
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    @JvmOverloads
    fun toFloat(o: Any?, defaultValue: Long = 0): Float {
        if (o == null) {
            return defaultValue.toFloat()
        }
        var value = 0f
        value = try {
            val s = o.toString().trim { it <= ' ' }
            s.toFloat()
        } catch (e: Exception) {
            defaultValue.toFloat()
        }
        return value
    }

    @JvmOverloads
    fun toInt(o: Any?, defaultValue: Int = 0): Int {
        if (o == null) {
            return defaultValue
        }
        val value: Int
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if (s.contains(".")) {
                s.substring(0, s.lastIndexOf(".")).toInt()
            } else {
                s.toInt()
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    @JvmOverloads
    fun toBoolean(o: Any?, defaultValue: Boolean = false): Boolean {
        if (o == null) {
            return false
        }
        val value: Boolean
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if ("false" == s.trim { it <= ' ' }) {
                false
            } else {
                true
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    fun <T> to(o: Any?, defaultValue: T): T {
        if (o == null) {
            return defaultValue
        }
        val value = o as T
        return value
    }
}