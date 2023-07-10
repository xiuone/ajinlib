package com.luck.picture.lib.basic

import android.content.Intent
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.PictureSelectorPreviewFragment
import com.luck.picture.lib.R
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.engine.VideoPlayerEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCustomLoadingListener
import com.luck.picture.lib.interfaces.OnExternalPreviewEventListener
import com.luck.picture.lib.interfaces.OnInjectActivityPreviewListener
import com.luck.picture.lib.interfaces.OnInjectLayoutResourceListener
import com.luck.picture.lib.magical.BuildRecycleItemViewParams
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.utils.ActivityCompatHelper
import com.luck.picture.lib.utils.DensityUtil
import com.luck.picture.lib.utils.DoubleUtils
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.util.ArrayList

/**
 * @author：luck
 * @date：2022/1/17 6:10 下午
 * @describe：PictureSelectionPreviewModel
 */
class PictureSelectionPreviewModel(private val selector: PictureSelector) {
    private val selectionConfig: SelectorConfig

    /**
     * Image Load the engine
     *
     * @param engine Image Load the engine
     *
     *
     * [
](https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/GlideEngine.java) *
     * @return
     */
    fun setImageEngine(engine: ImageEngine?): PictureSelectionPreviewModel {
        selectionConfig.imageEngine = engine
        return this
    }

    /**
     * Set up player engine
     *
     *
     * Used to preview custom player instances，MediaPlayer by default
     *
     * @param engine
     * @return
     */
    fun setVideoPlayerEngine(engine: VideoPlayerEngine<*>?): PictureSelectionPreviewModel {
        selectionConfig.videoPlayerEngine = engine
        return this
    }

    /**
     * PictureSelector theme style settings
     *
     * @param uiStyle
     *
     *
     * Use [                It consists of the following parts and can be set separately][PictureSelectorStyle]
     * [com.luck.picture.lib.style.TitleBarStyle]
     * [com.luck.picture.lib.style.AlbumWindowStyle]
     * [com.luck.picture.lib.style.SelectMainStyle]
     * [com.luck.picture.lib.style.BottomNavBarStyle]
     * [com.luck.picture.lib.style.PictureWindowAnimationStyle]
     *
     *
     * @return PictureSelectorStyle
     */
    fun setSelectorUIStyle(uiStyle: PictureSelectorStyle?): PictureSelectionPreviewModel {
        if (uiStyle != null) {
            selectionConfig.selectorStyle = uiStyle
        }
        return this
    }

    /**
     * Set App Language
     *
     * @param language [LanguageConfig]
     * @return PictureSelectionModel
     */
    fun setLanguage(language: Int): PictureSelectionPreviewModel {
        selectionConfig.language = language
        return this
    }

    /**
     * Set App default Language
     *
     * @param defaultLanguage default language [LanguageConfig]
     * @return PictureSelectionModel
     */
    fun setDefaultLanguage(defaultLanguage: Int): PictureSelectionPreviewModel {
        selectionConfig.defaultLanguage = defaultLanguage
        return this
    }

    /**
     * Intercept custom inject layout events, Users can implement their own layout
     * on the premise that the view ID must be consistent
     *
     * @param listener
     * @return
     */
    fun setInjectLayoutResourceListener(listener: OnInjectLayoutResourceListener?): PictureSelectionPreviewModel {
        selectionConfig.isInjectLayoutResource = listener != null
        selectionConfig.onLayoutResourceListener = listener
        return this
    }

    /**
     * View lifecycle listener
     *
     * @param viewLifecycle
     * @return
     */
    fun setAttachViewLifecycle(viewLifecycle: IBridgeViewLifecycle?): PictureSelectionPreviewModel {
        selectionConfig.viewLifecycle = viewLifecycle
        return this
    }

    /**
     * Using the system player
     *
     * @param isUseSystemVideoPlayer
     */
    fun isUseSystemVideoPlayer(isUseSystemVideoPlayer: Boolean): PictureSelectionPreviewModel {
        selectionConfig.isUseSystemVideoPlayer = isUseSystemVideoPlayer
        return this
    }

    /**
     * Preview Full Screen Mode
     *
     * @param isFullScreenModel
     * @return
     */
    fun isPreviewFullScreenMode(isFullScreenModel: Boolean): PictureSelectionPreviewModel {
        selectionConfig.isPreviewFullScreenMode = isFullScreenModel
        return this
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @param isPreviewZoomEffect
     * @param listView  Use [,]
     */
    fun isPreviewZoomEffect(
        isPreviewZoomEffect: Boolean,
        listView: ViewGroup
    ): PictureSelectionPreviewModel {
        return isPreviewZoomEffect(
            isPreviewZoomEffect,
            selectionConfig.isPreviewFullScreenMode,
            listView
        )
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isEnableVideoSize Use []
     */
    @Deprecated("")
    fun isEnableVideoSize(isEnableVideoSize: Boolean): PictureSelectionPreviewModel {
        selectionConfig.isSyncWidthAndHeight = isEnableVideoSize
        return this
    }

    /**
     * It is forbidden to correct or synchronize the width and height of the video
     *
     * @param isSyncWidthAndHeight
     * @return
     */
    fun isSyncWidthAndHeight(isSyncWidthAndHeight: Boolean): PictureSelectionPreviewModel {
        selectionConfig.isSyncWidthAndHeight = isSyncWidthAndHeight
        return this
    }

    /**
     * Preview Zoom Effect Mode
     *
     * @param isPreviewZoomEffect
     * @param isFullScreenModel
     * @param listView   Use [,]
     */
    fun isPreviewZoomEffect(
        isPreviewZoomEffect: Boolean,
        isFullScreenModel: Boolean,
        listView: ViewGroup
    ): PictureSelectionPreviewModel {
        if (listView is RecyclerView || listView is ListView) {
            if (isPreviewZoomEffect) {
                if (isFullScreenModel) {
                    BuildRecycleItemViewParams.generateViewParams(listView, 0)
                } else {
                    BuildRecycleItemViewParams.generateViewParams(
                        listView, DensityUtil.getStatusBarHeight(
                            selector.activity
                        )
                    )
                }
            }
            selectionConfig.isPreviewZoomEffect = isPreviewZoomEffect
        } else {
            throw IllegalArgumentException(
                listView.javaClass.canonicalName
                        + " Must be " + RecyclerView::class.java + " or " + ListView::class.java
            )
        }
        return this
    }

    /**
     * Whether to play video automatically when previewing
     *
     * @param isAutoPlay
     * @return
     */
    fun isAutoVideoPlay(isAutoPlay: Boolean): PictureSelectionPreviewModel {
        selectionConfig.isAutoVideoPlay = isAutoPlay
        return this
    }

    /**
     * loop video
     *
     * @param isLoopAutoPlay
     * @return
     */
    fun isLoopAutoVideoPlay(isLoopAutoPlay: Boolean): PictureSelectionPreviewModel {
        selectionConfig.isLoopAutoPlay = isLoopAutoPlay
        return this
    }

    /**
     * The video supports pause and resume
     *
     * @param isPauseResumePlay
     * @return
     */
    fun isVideoPauseResumePlay(isPauseResumePlay: Boolean): PictureSelectionPreviewModel {
        selectionConfig.isPauseResumePlay = isPauseResumePlay
        return this
    }

    /**
     * Intercept external preview click events, and users can implement their own preview framework
     *
     * @param listener
     * @return
     */
    fun setExternalPreviewEventListener(listener: OnExternalPreviewEventListener?): PictureSelectionPreviewModel {
        selectionConfig.onExternalPreviewEventListener = listener
        return this
    }

    /**
     * startActivityPreview(); Preview mode, custom preview callback
     *
     * @param listener
     * @return
     */
    fun setInjectActivityPreviewFragment(listener: OnInjectActivityPreviewListener?): PictureSelectionPreviewModel {
        selectionConfig.onInjectActivityPreviewListener = listener
        return this
    }

    /**
     * Custom show loading dialog
     *
     * @param listener
     * @return
     */
    fun setCustomLoadingListener(listener: OnCustomLoadingListener?): PictureSelectionPreviewModel {
        selectionConfig.onCustomLoadingListener = listener
        return this
    }

    /**
     * @param isHidePreviewDownload Previews do not show downloads
     * @return
     */
    fun isHidePreviewDownload(isHidePreviewDownload: Boolean): PictureSelectionPreviewModel {
        selectionConfig.isHidePreviewDownload = isHidePreviewDownload
        return this
    }

    /**
     * preview LocalMedia
     *
     * @param currentPosition
     * @param isDisplayDelete
     * @param list
     */
    fun startFragmentPreview(
        currentPosition: Int,
        isDisplayDelete: Boolean,
        list: ArrayList<LocalMedia?>?
    ) {
        startFragmentPreview(null, currentPosition, isDisplayDelete, list)
    }

    /**
     * preview LocalMedia
     *
     * @param previewFragment PictureSelectorPreviewFragment
     * @param currentPosition current position
     * @param isDisplayDelete if visible delete
     * @param list            preview data
     */
    fun startFragmentPreview(
        previewFragment: PictureSelectorPreviewFragment?,
        currentPosition: Int,
        isDisplayDelete: Boolean,
        list: ArrayList<LocalMedia?>?
    ) {
        var previewFragment = previewFragment
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw NullPointerException("imageEngine is null,Please implement ImageEngine")
            }
            if (list == null || list.size == 0) {
                throw NullPointerException("preview data is null")
            }
            var fragmentManager: FragmentManager? = null
            if (activity is FragmentActivity) {
                fragmentManager = activity.supportFragmentManager
            }
            if (fragmentManager == null) {
                throw NullPointerException("FragmentManager cannot be null")
            }
            val fragmentTag: String
            if (previewFragment != null) {
                fragmentTag = previewFragment.fragmentTag
            } else {
                fragmentTag = PictureSelectorPreviewFragment.TAG
                previewFragment = PictureSelectorPreviewFragment.newInstance()
            }
            if (ActivityCompatHelper.checkFragmentNonExits(
                    activity as FragmentActivity,
                    fragmentTag
                )
            ) {
                val previewData = ArrayList(list)
                previewFragment.setExternalPreviewData(
                    currentPosition,
                    previewData.size,
                    previewData,
                    isDisplayDelete
                )
                FragmentInjectManager.injectSystemRoomFragment(
                    fragmentManager,
                    fragmentTag,
                    previewFragment
                )
            }
        }
    }

    /**
     * preview LocalMedia
     *
     * @param currentPosition current position
     * @param isDisplayDelete if visible delete
     * @param list            preview data
     *
     *
     * You can do it [] interface, custom Preview
     *
     */
    fun startActivityPreview(
        currentPosition: Int,
        isDisplayDelete: Boolean,
        list: ArrayList<LocalMedia?>?
    ) {
        if (!DoubleUtils.isFastDoubleClick()) {
            val activity = selector.activity
                ?: throw NullPointerException("Activity cannot be null")
            if (selectionConfig.imageEngine == null && selectionConfig.chooseMode != SelectMimeType.ofAudio()) {
                throw NullPointerException("imageEngine is null,Please implement ImageEngine")
            }
            if (list == null || list.size == 0) {
                throw NullPointerException("preview data is null")
            }
            val intent = Intent(activity, PictureSelectorTransparentActivity::class.java)
            selectionConfig.addSelectedPreviewResult(list)
            intent.putExtra(PictureConfig.EXTRA_EXTERNAL_PREVIEW, true)
            intent.putExtra(
                PictureConfig.EXTRA_MODE_TYPE_SOURCE,
                PictureConfig.MODE_TYPE_EXTERNAL_PREVIEW_SOURCE
            )
            intent.putExtra(PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION, currentPosition)
            intent.putExtra(PictureConfig.EXTRA_EXTERNAL_PREVIEW_DISPLAY_DELETE, isDisplayDelete)
            val fragment = selector.fragment
            if (fragment != null) {
                fragment.startActivity(intent)
            } else {
                activity.startActivity(intent)
            }
            if (selectionConfig.isPreviewZoomEffect) {
                activity.overridePendingTransition(R.anim.ps_anim_fade_in, R.anim.ps_anim_fade_in)
            } else {
                val windowAnimationStyle = selectionConfig.selectorStyle.windowAnimationStyle
                activity.overridePendingTransition(
                    windowAnimationStyle.activityEnterAnimation,
                    R.anim.ps_anim_fade_in
                )
            }
        }
    }

    init {
        selectionConfig = SelectorConfig()
        SelectorProviders.instance.addSelectorConfigQueue(selectionConfig)
        selectionConfig.isPreviewZoomEffect = false
    }
}