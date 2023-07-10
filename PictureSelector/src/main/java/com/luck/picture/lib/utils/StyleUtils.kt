package com.luck.picture.lib.utils

import android.content.Context
import android.graphics.ColorFilter
import android.text.TextUtils
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
import java.util.regex.Pattern

/**
 * @author：luck
 * @date：2021/11/20 3:27 下午
 * @describe：StyleUtils
 */
object StyleUtils {
    private const val INVALID = 0

    /**
     * 验证样式资源的合法性
     *
     * @param resource
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun checkStyleValidity(resource: Int): Boolean {
        return resource != INVALID
    }

    /**
     * 验证文本的合法性
     *
     * @param text
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun checkTextValidity(text: String?): Boolean {
        return !TextUtils.isEmpty(text)
    }

    /**
     * 验证文本是否有动态匹配符
     *
     * @param text
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun checkTextFormatValidity(text: String?): Boolean {
        val pattern = "\\([^)]*\\)"
        val compile = Pattern.compile(pattern)
        val matcher = compile.matcher(text)
        return matcher.find()
    }

    /**
     * 验证文本是否有2个动态匹配符
     *
     * @param text
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun checkTextTwoFormatValidity(text: String?): Boolean {
        val pattern = "%[^%]*\\d"
        val compile = Pattern.compile(pattern)
        val matcher = compile.matcher(text)
        var count = 0
        while (matcher.find()) {
            count++
        }
        return count >= 2
    }

    /**
     * 验证大小的合法性
     *
     * @param size
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun checkSizeValidity(size: Int): Boolean {
        return size > INVALID
    }

    /**
     * 验证数组的合法性
     *
     * @param size
     * @return
     */
    fun checkArrayValidity(array: IntArray?): Boolean {
        return array != null && array.size > 0
    }

    /**
     * getColorFilter
     *
     * @param context
     * @param color
     * @return
     */
    fun getColorFilter(context: Context?, color: Int): ColorFilter? {
        return BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(
                context!!, color
            ), BlendModeCompat.SRC_ATOP
        )
    }
}