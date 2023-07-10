package com.luck.picture.lib.utils

import android.content.Context
import android.os.Environment
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
import com.luck.picture.lib.config.SelectMimeType
import java.util.HashMap

/**
 * @author：luck
 * @date：2022/9/20 7:57 下午
 * @describe：FileDirMap
 */
object FileDirMap {
    private val dirMap = HashMap<Int, String?>()
    fun init(context: Context) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        if (null == dirMap[SelectMimeType.TYPE_IMAGE]) {
            dirMap[SelectMimeType.TYPE_IMAGE] =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
                    .path
        }
        if (null == dirMap[SelectMimeType.TYPE_VIDEO]) {
            dirMap[SelectMimeType.TYPE_VIDEO] =
                context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)!!
                    .path
        }
        if (null == dirMap[SelectMimeType.TYPE_AUDIO]) {
            dirMap[SelectMimeType.TYPE_AUDIO] =
                context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!
                    .path
        }
    }

    fun getFileDirPath(context: Context, type: Int): String? {
        var dir = dirMap[type]
        if (null == dir) {
            init(context)
            dir = dirMap[type]
        }
        return dir
    }

    fun clear() {
        dirMap.clear()
    }
}