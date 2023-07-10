package com.luck.picture.lib.basic

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
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
import com.luck.picture.lib.PictureSelectorFragment
import com.luck.picture.lib.R
import com.luck.picture.lib.immersive.ImmersiveManager
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.language.PictureLanguageUtils
import com.luck.picture.lib.utils.StyleUtils

/**
 * @author：luck
 * @date：2021/11/17 9:59 上午
 * @describe：PictureSelectorSupporterActivity
 */
class PictureSelectorSupporterActivity : AppCompatActivity() {
    private var selectorConfig: SelectorConfig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSelectorConfig()
        immersive()
        setContentView(R.layout.ps_activity_container)
        setupFragment()
    }

    private fun initSelectorConfig() {
        selectorConfig = SelectorProviders.instance.selectorConfig
    }

    private fun immersive() {
        val mainStyle = selectorConfig!!.selectorStyle.selectMainStyle
        var statusBarColor = mainStyle.statusBarColor
        var navigationBarColor = mainStyle.navigationBarColor
        val isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack
        if (!StyleUtils.checkStyleValidity(statusBarColor)) {
            statusBarColor = ContextCompat.getColor(this, R.color.ps_color_grey)
        }
        if (!StyleUtils.checkStyleValidity(navigationBarColor)) {
            navigationBarColor = ContextCompat.getColor(this, R.color.ps_color_grey)
        }
        ImmersiveManager.immersiveAboveAPI23(
            this,
            statusBarColor,
            navigationBarColor,
            isDarkStatusBarBlack
        )
    }

    private fun setupFragment() {
        FragmentInjectManager.injectFragment(
            this, PictureSelectorFragment.TAG,
            PictureSelectorFragment.newInstance()
        )
    }

    /**
     * set app language
     */
    fun initAppLanguage() {
        if (selectorConfig != null && selectorConfig!!.language != LanguageConfig.UNKNOWN_LANGUAGE && !selectorConfig!!.isOnlyCamera) {
            PictureLanguageUtils.setAppLanguage(
                this,
                selectorConfig!!.language,
                selectorConfig!!.defaultLanguage
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initAppLanguage()
    }

    override fun attachBaseContext(newBase: Context) {
        val selectorConfig = SelectorProviders.instance.selectorConfig
        if (selectorConfig != null) {
            super.attachBaseContext(
                PictureContextWrapper.Companion.wrap(
                    newBase,
                    selectorConfig.language,
                    selectorConfig.defaultLanguage
                )
            )
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun finish() {
        super.finish()
        if (selectorConfig != null) {
            val windowAnimationStyle = selectorConfig!!.selectorStyle.windowAnimationStyle
            overridePendingTransition(0, windowAnimationStyle.activityExitAnimation)
        }
    }
}