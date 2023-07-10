package com.luck.picture.lib.utils

import android.content.Context
import android.content.Intent
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
import com.luck.picture.lib.basic.PictureFileProvider
import java.io.File

/**
 * @author：luck
 * @date：2023/3/25 6:21 下午
 * @describe：IntentUtils
 */
object IntentUtils {
    fun startSystemPlayerVideo(context: Context, path: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        val isParseUri = isContent(path!!) || isHasHttp(
            path
        )
        val data: Uri
        data = if (SdkVersionUtils.isQ()) {
            if (isParseUri) Uri.parse(path) else Uri.fromFile(File(path))
        } else if (SdkVersionUtils.isMaxN()) {
            if (isParseUri) Uri.parse(path) else PictureFileProvider.getUriForFile(
                context,
                context.packageName + ".luckProvider",
                File(path)
            )
        } else {
            if (isParseUri) Uri.parse(path) else Uri.fromFile(File(path))
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(data, "video/*")
        context.startActivity(intent)
    }
}