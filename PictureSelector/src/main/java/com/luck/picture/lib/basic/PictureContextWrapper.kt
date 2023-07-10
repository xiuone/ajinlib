package com.luck.picture.lib.basic

import android.content.Context
import android.content.ContextWrapper
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
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.language.PictureLanguageUtils

/**
 * @author：luck
 * @date：2019-12-15 19:34
 * @describe：ContextWrapper
 */
class PictureContextWrapper(base: Context?) : ContextWrapper(base) {
    override fun getSystemService(name: String): Any {
        return if (AUDIO_SERVICE == name) {
            applicationContext.getSystemService(name)
        } else super.getSystemService(name)
    }

    companion object {
        fun wrap(context: Context?, language: Int, defaultLanguage: Int): ContextWrapper {
            if (language != LanguageConfig.UNKNOWN_LANGUAGE) {
                PictureLanguageUtils.setAppLanguage(context, language, defaultLanguage)
            }
            return PictureContextWrapper(context)
        }
    }
}