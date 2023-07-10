package com.luck.picture.lib.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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

/**
 * @author：luck
 * @date：2021/11/17 4:42 下午
 * @describe：ActivityCompatHelper
 */
object ActivityCompatHelper {
    private const val MIN_FRAGMENT_COUNT = 1
    fun isDestroy(activity: Activity?): Boolean {
        return if (activity == null) {
            true
        } else activity.isFinishing || activity.isDestroyed
    }

    /**
     * 验证Fragment是否已存在
     *
     * @param fragmentTag Fragment标签
     * @return
     */
    fun checkFragmentNonExits(activity: FragmentActivity, fragmentTag: String?): Boolean {
        if (isDestroy(activity)) {
            return false
        }
        val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
        return fragment == null
    }

    fun assertValidRequest(context: Context?): Boolean {
        if (context is Activity) {
            return !isDestroy(context)
        } else if (context is ContextWrapper) {
            val contextWrapper = context
            if (contextWrapper.baseContext is Activity) {
                val activity = contextWrapper.baseContext as Activity
                return !isDestroy(activity)
            }
        }
        return true
    }

    /**
     * 验证当前是否是根Fragment
     *
     * @param activity
     * @return
     */
    fun checkRootFragment(activity: FragmentActivity): Boolean {
        return if (isDestroy(activity)) {
            false
        } else activity.supportFragmentManager.backStackEntryCount == MIN_FRAGMENT_COUNT
    }
}