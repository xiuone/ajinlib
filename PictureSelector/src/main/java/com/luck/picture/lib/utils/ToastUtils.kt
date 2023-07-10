package com.luck.picture.lib.utils

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
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
 * @date：2022/1/8 3:29 下午
 * @describe：ToastUtils
 */
object ToastUtils {
    /**
     * show toast content
     *
     * @param context
     * @param text
     */
    fun showToast(context: Context, text: String?) {
        if (isFastDoubleClick && TextUtils.equals(text, mLastText)) {
            return
        }
        var appContext = instance!!.appContext
        if (appContext == null) {
            appContext = context.applicationContext
        }
        if (isInUiThread) {
            Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
            mLastText = text
        } else {
            runOnUiThread {
                var appContext = instance!!.appContext
                if (appContext == null) {
                    appContext = context.applicationContext
                }
                Toast.makeText(appContext, text, Toast.LENGTH_SHORT).show()
                mLastText = text
            }
        }
    }

    private const val TIME: Long = 1000
    private var lastClickTime: Long = 0
    private var mLastText: String? = null
    val isFastDoubleClick: Boolean
        get() {
            val time = System.currentTimeMillis()
            if (time - lastClickTime < TIME) {
                return true
            }
            lastClickTime = time
            return false
        }
}