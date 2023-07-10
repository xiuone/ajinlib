package com.luck.picture.lib.utils

import android.content.Context
import android.net.Uri
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
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

/**
 * @author：luck
 * @date：2019-11-08 19:25
 * @describe：SandboxTransformUtils
 */
object SandboxTransformUtils {
    /**
     * 把外部目录下的图片拷贝至沙盒内
     *
     * @param ctx
     * @param url
     * @param mineType
     * @param customFileName
     * @return
     */
    /**
     * 把外部目录下的图片拷贝至沙盒内
     *
     * @param ctx
     * @param url
     * @param mineType
     * @return
     */
    @JvmOverloads
    fun copyPathToSandbox(
        ctx: Context,
        url: String?,
        mineType: String?,
        customFileName: String? = ""
    ): String? {
        try {
            if (isHasHttp(url!!)) {
                return null
            }
            val inputStream: InputStream?
            val sandboxPath = PictureFileUtils.createFilePath(ctx, mineType, customFileName)
            inputStream = if (isContent(url)) {
                openInputStream(ctx, Uri.parse(url))
            } else {
                FileInputStream(url)
            }
            val copyFileSuccess =
                PictureFileUtils.writeFileFromIS(inputStream, FileOutputStream(sandboxPath))
            if (copyFileSuccess) {
                return sandboxPath
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}