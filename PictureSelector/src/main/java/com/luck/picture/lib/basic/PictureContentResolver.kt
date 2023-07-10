package com.luck.picture.lib.basic

import android.content.Context
import android.net.Uri
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import com.luck.picture.lib.app.PictureAppMaster.appContext
import com.luck.picture.lib.app.PictureAppMaster.pictureSelectorEngine
import com.luck.picture.lib.PictureOnlyCameraFragment.Companion.newInstance
import com.luck.picture.lib.PictureOnlyCameraFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorPreviewFragment.setExternalPreviewData
import com.luck.picture.lib.PictureSelectorSystemFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorFragment.Companion.newInstance
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.FileDirMap
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

/**
 * @author：luck
 * @date：2021/5/26 9:22 PM
 * @describe：PictureContentResolver
 */
object PictureContentResolver {
    /**
     * ContentResolver openInputStream
     *
     * @param context
     * @param uri
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun openInputStream(context: Context?, uri: Uri?): InputStream? {
        try {
            return context!!.contentResolver.openInputStream(uri!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * ContentResolver OutputStream
     *
     * @param context
     * @param uri
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun openOutputStream(context: Context, uri: Uri?): OutputStream? {
        try {
            return context.contentResolver.openOutputStream(uri!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}