package com.luck.picture.lib.basic

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
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
import androidx.fragment.app.Fragment
import com.luck.picture.lib.PictureOnlyCameraFragment
import com.luck.picture.lib.PictureSelectorPreviewFragment
import com.luck.picture.lib.PictureSelectorSystemFragment
import com.luck.picture.lib.R
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.immersive.ImmersiveManager
import com.luck.picture.lib.utils.StyleUtils
import java.util.ArrayList

/**
 * @author：luck
 * @date：2022/2/10 6:07 下午
 * @describe：PictureSelectorTransparentActivity
 */
class PictureSelectorTransparentActivity : AppCompatActivity() {
    private var selectorConfig: SelectorConfig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSelectorConfig()
        immersive()
        setContentView(R.layout.ps_empty)
        if (isExternalPreview) {
            // TODO ignore
        } else {
            setActivitySize()
        }
        setupFragment()
    }

    private fun initSelectorConfig() {
        selectorConfig = SelectorProviders.instance.selectorConfig
    }

    private val isExternalPreview: Boolean
        private get() {
            val modeTypeSource = intent.getIntExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, 0)
            return modeTypeSource == PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE
        }

    private fun immersive() {
        if (selectorConfig!!.selectorStyle == null) {
            SelectorProviders.instance.selectorConfig
        }
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
        val fragmentTag: String
        var targetFragment: Fragment? = null
        val modeTypeSource = intent.getIntExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, 0)
        if (modeTypeSource == PictureConfig.MODE_TYPE_SYSTEM_SOURCE) {
            fragmentTag = PictureSelectorSystemFragment.TAG
            targetFragment = PictureSelectorSystemFragment.newInstance()
        } else if (modeTypeSource == PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE) {
            if (selectorConfig!!.onInjectActivityPreviewListener != null) {
                targetFragment =
                    selectorConfig!!.onInjectActivityPreviewListener.onInjectPreviewFragment()
            }
            if (targetFragment != null) {
                fragmentTag = (targetFragment as PictureSelectorPreviewFragment).fragmentTag
            } else {
                fragmentTag = PictureSelectorPreviewFragment.TAG
                targetFragment = PictureSelectorPreviewFragment.newInstance()
            }
            val position = intent.getIntExtra(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, 0)
            val previewData = ArrayList(
                selectorConfig!!.selectedPreviewResult
            )
            val isDisplayDelete = intent
                .getBooleanExtra(PictureConfig.EXTRA_EXTERNAL_PREVIEW_DISPLAY_DELETE, false)
            targetFragment.setExternalPreviewData(
                position,
                previewData.size,
                previewData,
                isDisplayDelete
            )
        } else {
            fragmentTag = PictureOnlyCameraFragment.TAG
            targetFragment = PictureOnlyCameraFragment.newInstance()
        }
        val supportFragmentManager = supportFragmentManager
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
        FragmentInjectManager.injectSystemRoomFragment(
            supportFragmentManager,
            fragmentTag,
            targetFragment
        )
    }

    @SuppressLint("RtlHardcoded")
    private fun setActivitySize() {
        val window = window
        window.setGravity(Gravity.LEFT or Gravity.TOP)
        val params = window.attributes
        params.x = 0
        params.y = 0
        params.height = 1
        params.width = 1
        window.attributes = params
    }

    override fun finish() {
        super.finish()
        val modeTypeSource = intent.getIntExtra(PictureConfig.EXTRA_MODE_TYPE_SOURCE, 0)
        if (modeTypeSource == PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE && !selectorConfig!!.isPreviewZoomEffect) {
            val windowAnimationStyle = selectorConfig!!.selectorStyle.windowAnimationStyle
            overridePendingTransition(0, windowAnimationStyle.activityExitAnimation)
        } else {
            overridePendingTransition(0, R.anim.ps_anim_fade_out)
        }
    }
}