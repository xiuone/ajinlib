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
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaFolder
import java.util.*

/**
 * @author：luck
 * @date：2021/11/11 6:11 下午
 * @describe：排序类
 */
object SortUtils {
    /**
     * Sort by the number of files
     *
     * @param imageFolders
     */
    fun sortFolder(imageFolders: List<LocalMediaFolder>?) {
        Collections.sort(imageFolders) { lhs: LocalMediaFolder, rhs: LocalMediaFolder ->
            if (lhs.data == null || rhs.data == null) {
                return@sort 0
            }
            val lSize = lhs.folderTotalNum
            val rSize = rhs.folderTotalNum
            Integer.compare(rSize, lSize)
        }
    }

    /**
     * Sort by the add Time of files
     *
     * @param list
     */
    fun sortLocalMediaAddedTime(list: List<LocalMedia>?) {
        Collections.sort(list) { lhs: LocalMedia, rhs: LocalMedia ->
            val lAddedTime = lhs.dateAddedTime
            val rAddedTime = rhs.dateAddedTime
            java.lang.Long.compare(rAddedTime, lAddedTime)
        }
    }
}