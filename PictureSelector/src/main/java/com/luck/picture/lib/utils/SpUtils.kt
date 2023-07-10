package com.luck.picture.lib.utils

import android.content.Context
import android.content.SharedPreferences
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
import com.luck.picture.lib.config.PictureConfig

/**
 * @author：luck
 * @date：2022/3/15 6:26 下午
 * @describe：SpUtils
 */
object SpUtils {
    private var pictureSpUtils: SharedPreferences? = null
    private fun getSp(context: Context): SharedPreferences? {
        if (pictureSpUtils == null) {
            pictureSpUtils =
                context.getSharedPreferences(PictureConfig.SP_NAME, Context.MODE_PRIVATE)
        }
        return pictureSpUtils
    }

    fun putString(context: Context, key: String?, value: String?) {
        getSp(context)!!.edit().putString(key, value).apply()
    }

    fun putBoolean(context: Context, key: String?, value: Boolean) {
        getSp(context)!!.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(context: Context, key: String?, defValue: Boolean): Boolean {
        return getSp(context)!!.getBoolean(key, defValue)
    }
}