package com.luck.picture.lib.basic

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import androidx.fragment.app.Fragment
import com.luck.picture.lib.R
import com.luck.picture.lib.config.*
import com.luck.picture.lib.dialog.PhotoItemSelectedDialog
import com.luck.picture.lib.dialog.PictureLoadingDialog
import com.luck.picture.lib.dialog.RemindDialog
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.immersive.ImmersiveManager
import com.luck.picture.lib.interfaces.OnCallbackIndexListener
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.language.PictureLanguageUtils
import com.luck.picture.lib.loader.IBridgeMediaLoader
import com.luck.picture.lib.manager.SelectedManager
import com.luck.picture.lib.service.ForegroundService
import com.luck.picture.lib.thread.PictureThreadUtils
import com.luck.picture.lib.utils.*
import org.json.JSONArray
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.Exception
import java.lang.NullPointerException
import java.util.ArrayList
import java.util.HashSet
import java.util.concurrent.ConcurrentHashMap

/**
 * @author：luck
 * @date：2021/11/19 10:02 下午
 * @describe：PictureCommonFragment
 */
abstract class PictureCommonFragment : Fragment(), IPictureSelectorCommonEvent {

    /**
     * IBridgePictureBehavior
     */
    protected var iBridgePictureBehavior: IBridgePictureBehavior? = null

    /**
     * page
     */
    protected var mPage = 1

    /**
     * Media Loader engine
     */
    protected var mLoader: IBridgeMediaLoader? = null

    /**
     * PictureSelector Config
     */
    protected var selectorConfig: SelectorConfig? = null

    /**
     * Loading Dialog
     */
    private var mLoadingDialog: Dialog? = null

    /**
     * click sound
     */
    private var soundPool: SoundPool? = null

    /**
     * click sound effect id
     */
    private var soundID = 0

    /**
     * fragment enter anim duration
     */
    private var enterAnimDuration: Long = 0

    /**
     * tipsDialog
     */
    protected var tipsDialog: Dialog? = null

    /**
     * Context
     */
    private var context: Context? = null
    open val fragmentTag: String?
        get() = TAG

    override fun onCreateLoader() {}
    override val resourceId: Int
        get() = 0

    override fun onFragmentResume() {}
    override fun reStartSavedInstance(savedInstanceState: Bundle?) {}
    override fun onCheckOriginalChange() {}
    override fun dispatchCameraMediaResult(media: LocalMedia?) {}
    override fun onSelectedChange(isAddRemove: Boolean, currentMedia: LocalMedia?) {}
    override fun onFixedSelectedChange(oldLocalMedia: LocalMedia?) {}
    override fun sendChangeSubSelectPositionEvent(adapterChange: Boolean) {}
    override fun handlePermissionSettingResult(permissions: Array<String?>?) {}
    override fun onEditMedia(intent: Intent?) {}
    override fun onEnterFragment() {}
    override fun onExitFragment() {}
    protected val appContext: Context?
        protected get() {
            val ctx = getContext()
            if (ctx != null) {
                return ctx
            } else {
                val appContext = instance!!.appContext
                if (appContext != null) {
                    return appContext
                }
            }
            return context
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (mPermissionResultCallback != null) {
            PermissionChecker.getInstance()
                .onRequestPermissionsResult(grantResults, mPermissionResultCallback)
            mPermissionResultCallback = null
        }
    }

    /**
     * Set PermissionResultCallback
     *
     * @param callback
     */
    fun setPermissionsResultAction(callback: PermissionResultCallback?) {
        mPermissionResultCallback = callback
    }

    override fun handlePermissionDenied(permissionArray: Array<String>) {
        PermissionConfig.CURRENT_REQUEST_PERMISSION = permissionArray
        if (permissionArray != null && permissionArray.size > 0) {
            SpUtils.putBoolean(appContext, permissionArray[0], true)
        }
        if (selectorConfig!!.onPermissionDeniedListener != null) {
            onPermissionExplainEvent(false, null)
            selectorConfig!!.onPermissionDeniedListener
                .onDenied(this, permissionArray, PictureConfig.REQUEST_GO_SETTING,
                    object : OnCallbackListener<Boolean?> {
                        override fun onCall(isResult: Boolean) {
                            if (isResult) {
                                handlePermissionSettingResult(PermissionConfig.CURRENT_REQUEST_PERMISSION)
                            }
                        }
                    })
        } else {
            PermissionUtil.goIntentSetting(this, PictureConfig.REQUEST_GO_SETTING)
        }
    }

    /**
     * 使用PictureSelector 默认方式进入
     *
     * @return
     */
    protected val isNormalDefaultEnter: Boolean
        protected get() = activity is PictureSelectorSupporterActivity || activity is PictureSelectorTransparentActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (resourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) {
            inflater.inflate(resourceId, container, false)
        } else super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectorConfig = SelectorProviders.instance.selectorConfig
        FileDirMap.init(view.context)
        if (selectorConfig.viewLifecycle != null) {
            selectorConfig.viewLifecycle.onViewCreated(this, view, savedInstanceState)
        }
        mLoadingDialog = if (selectorConfig.onCustomLoadingListener != null) {
            selectorConfig.onCustomLoadingListener.create(appContext)
        } else {
            PictureLoadingDialog(appContext)
        }
        setRequestedOrientation()
        setTranslucentStatusBar()
        setRootViewKeyListener(requireView())
        if (selectorConfig.isOpenClickSound && !selectorConfig.isOnlyCamera) {
            soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
            soundID = soundPool!!.load(appContext, R.raw.ps_click_music, 1)
        }
    }

    /**
     * 设置透明状态栏
     */
    private fun setTranslucentStatusBar() {
        if (selectorConfig!!.isPreviewFullScreenMode) {
            val selectMainStyle = selectorConfig!!.selectorStyle.selectMainStyle
            ImmersiveManager.translucentStatusBar(
                requireActivity(),
                selectMainStyle.isDarkStatusBarBlack
            )
        }
    }

    /**
     * 设置回退监听
     *
     * @param view
     */
    fun setRootViewKeyListener(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onKeyBackFragmentFinish()
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initAppLanguage()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val windowAnimationStyle = selectorConfig!!.selectorStyle.windowAnimationStyle
        val loadAnimation: Animation
        if (enter) {
            loadAnimation = if (windowAnimationStyle.activityEnterAnimation != 0) {
                AnimationUtils.loadAnimation(
                    appContext,
                    windowAnimationStyle.activityEnterAnimation
                )
            } else {
                AnimationUtils.loadAnimation(
                    appContext,
                    R.anim.ps_anim_alpha_enter
                )
            }
            enterAnimationDuration = loadAnimation.duration
            onEnterFragment()
        } else {
            loadAnimation = if (windowAnimationStyle.activityExitAnimation != 0) {
                AnimationUtils.loadAnimation(
                    appContext,
                    windowAnimationStyle.activityExitAnimation
                )
            } else {
                AnimationUtils.loadAnimation(
                    appContext,
                    R.anim.ps_anim_alpha_exit
                )
            }
            onExitFragment()
        }
        return loadAnimation
    }

    var enterAnimationDuration: Long
        get() {
            val DIFFERENCE: Long = 50
            val duration =
                if (enterAnimDuration > DIFFERENCE) enterAnimDuration - DIFFERENCE else enterAnimDuration
            return if (duration >= 0) duration else 0
        }
        set(duration) {
            enterAnimDuration = duration
        }

    override fun confirmSelect(currentMedia: LocalMedia, isSelected: Boolean): Int {
        if (selectorConfig!!.onSelectFilterListener != null) {
            if (selectorConfig!!.onSelectFilterListener.onSelectFilter(currentMedia)) {
                var isSelectLimit = false
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext,
                            currentMedia,
                            selectorConfig,
                            SelectLimitType.SELECT_NOT_SUPPORT_SELECT_LIMIT
                        )
                }
                if (isSelectLimit) {
                } else {
                    ToastUtils.showToast(appContext, getString(R.string.ps_select_no_support))
                }
                return SelectedManager.INVALID
            }
        }
        val checkSelectValidity = isCheckSelectValidity(currentMedia, isSelected)
        if (checkSelectValidity != SelectedManager.SUCCESS) {
            return SelectedManager.INVALID
        }
        val selectedResult: MutableList<LocalMedia> = selectorConfig!!.selectedResult
        val resultCode: Int
        if (isSelected) {
            selectedResult.remove(currentMedia)
            resultCode = SelectedManager.REMOVE
        } else {
            if (selectorConfig!!.selectionMode == SelectModeConfig.SINGLE) {
                if (selectedResult.size > 0) {
                    sendFixedSelectedChangeEvent(selectedResult[0])
                    selectedResult.clear()
                }
            }
            selectedResult.add(currentMedia)
            currentMedia.num = selectedResult.size
            resultCode = SelectedManager.ADD_SUCCESS
            playClickEffect()
        }
        sendSelectedChangeEvent(resultCode == SelectedManager.ADD_SUCCESS, currentMedia)
        return resultCode
    }

    /**
     * 验证选择的合法性
     *
     * @param currentMedia 当前选中资源
     * @param isSelected   选中或是取消
     * @return
     */
    protected fun isCheckSelectValidity(currentMedia: LocalMedia, isSelected: Boolean): Int {
        val curMimeType = currentMedia.mimeType
        val curDuration = currentMedia.duration
        val curFileSize = currentMedia.size
        val selectedResult: List<LocalMedia> = selectorConfig!!.selectedResult
        if (selectorConfig!!.isWithVideoImage) {
            // 共选型模式
            var selectVideoSize = 0
            for (i in selectedResult.indices) {
                val mimeType = selectedResult[i].mimeType
                if (PictureMimeType.isHasVideo(mimeType)) {
                    selectVideoSize++
                }
            }
            if (checkWithMimeTypeValidity(
                    currentMedia,
                    isSelected,
                    curMimeType,
                    selectVideoSize,
                    curFileSize,
                    curDuration
                )
            ) {
                return SelectedManager.INVALID
            }
        } else {
            // 单一型模式
            if (checkOnlyMimeTypeValidity(
                    currentMedia,
                    isSelected,
                    curMimeType,
                    selectorConfig!!.resultFirstMimeType,
                    curFileSize,
                    curDuration
                )
            ) {
                return SelectedManager.INVALID
            }
        }
        return SelectedManager.SUCCESS
    }

    @SuppressLint("StringFormatInvalid", "StringFormatMatches")
    override fun checkWithMimeTypeValidity(
        media: LocalMedia?,
        isSelected: Boolean,
        curMimeType: String,
        selectVideoSize: Int,
        fileSize: Long,
        duration: Long
    ): Boolean {
        if (selectorConfig!!.selectMaxFileSize > 0) {
            if (fileSize > selectorConfig!!.selectMaxFileSize) {
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext, media, selectorConfig,
                            SelectLimitType.SELECT_MAX_FILE_SIZE_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                val maxFileSize = PictureFileUtils.formatFileSize(
                    selectorConfig!!.selectMaxFileSize
                )
                showTipsDialog(getString(R.string.ps_select_max_size, maxFileSize))
                return true
            }
        }
        if (selectorConfig!!.selectMinFileSize > 0) {
            if (fileSize < selectorConfig!!.selectMinFileSize) {
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext, media, selectorConfig,
                            SelectLimitType.SELECT_MIN_FILE_SIZE_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                val minFileSize = PictureFileUtils.formatFileSize(
                    selectorConfig!!.selectMinFileSize
                )
                showTipsDialog(getString(R.string.ps_select_min_size, minFileSize))
                return true
            }
        }
        if (PictureMimeType.isHasVideo(curMimeType)) {
            if (selectorConfig!!.selectionMode == SelectModeConfig.MULTIPLE) {
                if (selectorConfig!!.maxVideoSelectNum <= 0) {
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext,
                                media,
                                selectorConfig,
                                SelectLimitType.SELECT_NOT_WITH_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    // 如果视频可选数量是0
                    showTipsDialog(getString(R.string.ps_rule))
                    return true
                }
                if (!isSelected && selectorConfig!!.selectedResult.size >= selectorConfig!!.maxSelectNum) {
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext,
                                media,
                                selectorConfig,
                                SelectLimitType.SELECT_MAX_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    showTipsDialog(
                        getString(
                            R.string.ps_message_max_num,
                            selectorConfig!!.maxSelectNum
                        )
                    )
                    return true
                }
                if (!isSelected && selectVideoSize >= selectorConfig!!.maxVideoSelectNum) {
                    // 如果选择的是视频
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext,
                                media,
                                selectorConfig,
                                SelectLimitType.SELECT_MAX_VIDEO_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    showTipsDialog(
                        getTipsMsg(
                            appContext, curMimeType, selectorConfig!!.maxVideoSelectNum
                        )
                    )
                    return true
                }
            }
            if (!isSelected && selectorConfig!!.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(
                    duration
                ) < selectorConfig!!.selectMinDurationSecond
            ) {
                // 视频小于最低指定的长度
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext, media, selectorConfig,
                            SelectLimitType.SELECT_MIN_VIDEO_SECOND_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_select_video_min_second,
                        selectorConfig!!.selectMinDurationSecond / 1000
                    )
                )
                return true
            }
            if (!isSelected && selectorConfig!!.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(
                    duration
                ) > selectorConfig!!.selectMaxDurationSecond
            ) {
                // 视频时长超过了指定的长度
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext, media, selectorConfig,
                            SelectLimitType.SELECT_MAX_VIDEO_SECOND_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_select_video_max_second,
                        selectorConfig!!.selectMaxDurationSecond / 1000
                    )
                )
                return true
            }
        } else {
            if (selectorConfig!!.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && selectorConfig!!.selectedResult.size >= selectorConfig!!.maxSelectNum) {
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext, media, selectorConfig,
                                SelectLimitType.SELECT_MAX_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    showTipsDialog(
                        getString(
                            R.string.ps_message_max_num,
                            selectorConfig!!.maxSelectNum
                        )
                    )
                    return true
                }
            }
        }
        return false
    }

    @SuppressLint("StringFormatInvalid")
    override fun checkOnlyMimeTypeValidity(
        media: LocalMedia?,
        isSelected: Boolean,
        curMimeType: String,
        existMimeType: String?,
        fileSize: Long,
        duration: Long
    ): Boolean {
        if (PictureMimeType.isMimeTypeSame(existMimeType, curMimeType)) {
            // ignore
        } else {
            if (selectorConfig!!.onSelectLimitTipsListener != null) {
                val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                    .onSelectLimitTips(
                        appContext,
                        media,
                        selectorConfig,
                        SelectLimitType.SELECT_NOT_WITH_SELECT_LIMIT
                    )
                if (isSelectLimit) {
                    return true
                }
            }
            showTipsDialog(getString(R.string.ps_rule))
            return true
        }
        if (selectorConfig!!.selectMaxFileSize > 0) {
            if (fileSize > selectorConfig!!.selectMaxFileSize) {
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext, media, selectorConfig,
                            SelectLimitType.SELECT_MAX_FILE_SIZE_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                val maxFileSize = PictureFileUtils.formatFileSize(
                    selectorConfig!!.selectMaxFileSize
                )
                showTipsDialog(getString(R.string.ps_select_max_size, maxFileSize))
                return true
            }
        }
        if (selectorConfig!!.selectMinFileSize > 0) {
            if (fileSize < selectorConfig!!.selectMinFileSize) {
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext, media, selectorConfig,
                            SelectLimitType.SELECT_MIN_FILE_SIZE_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                val minFileSize = PictureFileUtils.formatFileSize(
                    selectorConfig!!.selectMinFileSize
                )
                showTipsDialog(getString(R.string.ps_select_min_size, minFileSize))
                return true
            }
        }
        if (PictureMimeType.isHasVideo(curMimeType)) {
            if (selectorConfig!!.selectionMode == SelectModeConfig.MULTIPLE) {
                selectorConfig!!.maxVideoSelectNum =
                    if (selectorConfig!!.maxVideoSelectNum > 0) selectorConfig!!.maxVideoSelectNum else selectorConfig!!.maxSelectNum
                if (!isSelected && selectorConfig!!.selectCount >= selectorConfig!!.maxVideoSelectNum) {
                    // 如果先选择的是视频
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext,
                                media,
                                selectorConfig,
                                SelectLimitType.SELECT_MAX_VIDEO_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    showTipsDialog(
                        getTipsMsg(
                            appContext, curMimeType, selectorConfig!!.maxVideoSelectNum
                        )
                    )
                    return true
                }
            }
            if (!isSelected && selectorConfig!!.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(
                    duration
                ) < selectorConfig!!.selectMinDurationSecond
            ) {
                // 视频小于最低指定的长度
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext,
                            media,
                            selectorConfig,
                            SelectLimitType.SELECT_MIN_VIDEO_SECOND_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_select_video_min_second,
                        selectorConfig!!.selectMinDurationSecond / 1000
                    )
                )
                return true
            }
            if (!isSelected && selectorConfig!!.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(
                    duration
                ) > selectorConfig!!.selectMaxDurationSecond
            ) {
                // 视频时长超过了指定的长度
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext,
                            media,
                            selectorConfig,
                            SelectLimitType.SELECT_MAX_VIDEO_SECOND_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_select_video_max_second,
                        selectorConfig!!.selectMaxDurationSecond / 1000
                    )
                )
                return true
            }
        } else if (PictureMimeType.isHasAudio(curMimeType)) {
            if (selectorConfig!!.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && selectorConfig!!.selectedResult.size >= selectorConfig!!.maxSelectNum) {
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext,
                                media,
                                selectorConfig,
                                SelectLimitType.SELECT_MAX_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    showTipsDialog(
                        getTipsMsg(
                            appContext, curMimeType, selectorConfig!!.maxSelectNum
                        )
                    )
                    return true
                }
            }
            if (!isSelected && selectorConfig!!.selectMinDurationSecond > 0 && DateUtils.millisecondToSecond(
                    duration
                ) < selectorConfig!!.selectMinDurationSecond
            ) {
                // 音频小于最低指定的长度
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext,
                            media,
                            selectorConfig,
                            SelectLimitType.SELECT_MIN_AUDIO_SECOND_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_select_audio_min_second,
                        selectorConfig!!.selectMinDurationSecond / 1000
                    )
                )
                return true
            }
            if (!isSelected && selectorConfig!!.selectMaxDurationSecond > 0 && DateUtils.millisecondToSecond(
                    duration
                ) > selectorConfig!!.selectMaxDurationSecond
            ) {
                // 音频时长超过了指定的长度
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext,
                            media,
                            selectorConfig,
                            SelectLimitType.SELECT_MAX_AUDIO_SECOND_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_select_audio_max_second,
                        selectorConfig!!.selectMaxDurationSecond / 1000
                    )
                )
                return true
            }
        } else {
            if (selectorConfig!!.selectionMode == SelectModeConfig.MULTIPLE) {
                if (!isSelected && selectorConfig!!.selectedResult.size >= selectorConfig!!.maxSelectNum) {
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext,
                                media,
                                selectorConfig,
                                SelectLimitType.SELECT_MAX_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    showTipsDialog(
                        getTipsMsg(
                            appContext, curMimeType, selectorConfig!!.maxSelectNum
                        )
                    )
                    return true
                }
            }
        }
        return false
    }

    /**
     * 提示Dialog
     *
     * @param tips
     */
    private fun showTipsDialog(tips: String) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return
        }
        try {
            if (tipsDialog != null && tipsDialog!!.isShowing) {
                return
            }
            tipsDialog = RemindDialog.buildDialog(appContext, tips)
            tipsDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun sendSelectedChangeEvent(isAddRemove: Boolean, currentMedia: LocalMedia?) {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            val fragments = activity!!.supportFragmentManager.fragments
            for (i in fragments.indices) {
                val fragment = fragments[i]
                if (fragment is PictureCommonFragment) {
                    fragment.onSelectedChange(isAddRemove, currentMedia)
                }
            }
        }
    }

    override fun sendFixedSelectedChangeEvent(currentMedia: LocalMedia?) {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            val fragments = activity!!.supportFragmentManager.fragments
            for (i in fragments.indices) {
                val fragment = fragments[i]
                if (fragment is PictureCommonFragment) {
                    fragment.onFixedSelectedChange(currentMedia)
                }
            }
        }
    }

    override fun sendSelectedOriginalChangeEvent() {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            val fragments = activity!!.supportFragmentManager.fragments
            for (i in fragments.indices) {
                val fragment = fragments[i]
                if (fragment is PictureCommonFragment) {
                    fragment.onCheckOriginalChange()
                }
            }
        }
    }

    override fun openSelectedCamera() {
        when (selectorConfig!!.chooseMode) {
            SelectMimeType.TYPE_ALL -> if (selectorConfig!!.ofAllCameraType == SelectMimeType.ofImage()) {
                openImageCamera()
            } else if (selectorConfig!!.ofAllCameraType == SelectMimeType.ofVideo()) {
                openVideoCamera()
            } else {
                onSelectedOnlyCamera()
            }
            SelectMimeType.TYPE_IMAGE -> openImageCamera()
            SelectMimeType.TYPE_VIDEO -> openVideoCamera()
            SelectMimeType.TYPE_AUDIO -> openSoundRecording()
            else -> {}
        }
    }

    override fun onSelectedOnlyCamera() {
        val selectedDialog = PhotoItemSelectedDialog.newInstance()
        selectedDialog.setOnItemClickListener { v, position ->
            when (position) {
                PhotoItemSelectedDialog.IMAGE_CAMERA -> if (selectorConfig!!.onCameraInterceptListener != null) {
                    onInterceptCameraEvent(SelectMimeType.TYPE_IMAGE)
                } else {
                    openImageCamera()
                }
                PhotoItemSelectedDialog.VIDEO_CAMERA -> if (selectorConfig!!.onCameraInterceptListener != null) {
                    onInterceptCameraEvent(SelectMimeType.TYPE_VIDEO)
                } else {
                    openVideoCamera()
                }
                else -> {}
            }
        }
        selectedDialog.setOnDismissListener { isCancel, dialog ->
            if (selectorConfig!!.isOnlyCamera && isCancel) {
                onKeyBackFragmentFinish()
            }
        }
        selectedDialog.show(childFragmentManager, "PhotoItemSelectedDialog")
    }

    override fun openImageCamera() {
        onPermissionExplainEvent(true, PermissionConfig.CAMERA)
        if (selectorConfig!!.onPermissionsEventListener != null) {
            onApplyPermissionsEvent(PermissionEvent.EVENT_IMAGE_CAMERA, PermissionConfig.CAMERA)
        } else {
            PermissionChecker.getInstance().requestPermissions(this, PermissionConfig.CAMERA,
                object : PermissionResultCallback() {
                    fun onGranted() {
                        startCameraImageCapture()
                    }

                    fun onDenied() {
                        handlePermissionDenied(PermissionConfig.CAMERA)
                    }
                })
        }
    }

    /**
     * Start ACTION_IMAGE_CAPTURE
     */
    protected fun startCameraImageCapture() {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            onPermissionExplainEvent(false, null)
            if (selectorConfig!!.onCameraInterceptListener != null) {
                onInterceptCameraEvent(SelectMimeType.TYPE_IMAGE)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (cameraIntent.resolveActivity(activity!!.packageManager) != null) {
                    ForegroundService.startForegroundService(
                        appContext,
                        selectorConfig!!.isCameraForegroundService
                    )
                    val imageUri = MediaStoreUtils.createCameraOutImageUri(
                        appContext, selectorConfig
                    )
                    if (imageUri != null) {
                        if (selectorConfig!!.isCameraAroundState) {
                            cameraIntent.putExtra(
                                PictureConfig.CAMERA_FACING,
                                PictureConfig.CAMERA_BEFORE
                            )
                        }
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA)
                    }
                }
            }
        }
    }

    override fun openVideoCamera() {
        onPermissionExplainEvent(true, PermissionConfig.CAMERA)
        if (selectorConfig!!.onPermissionsEventListener != null) {
            onApplyPermissionsEvent(PermissionEvent.EVENT_VIDEO_CAMERA, PermissionConfig.CAMERA)
        } else {
            PermissionChecker.getInstance().requestPermissions(this, PermissionConfig.CAMERA,
                object : PermissionResultCallback() {
                    fun onGranted() {
                        startCameraVideoCapture()
                    }

                    fun onDenied() {
                        handlePermissionDenied(PermissionConfig.CAMERA)
                    }
                })
        }
    }

    /**
     * Start ACTION_VIDEO_CAPTURE
     */
    protected fun startCameraVideoCapture() {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            onPermissionExplainEvent(false, null)
            if (selectorConfig!!.onCameraInterceptListener != null) {
                onInterceptCameraEvent(SelectMimeType.TYPE_VIDEO)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                if (cameraIntent.resolveActivity(activity!!.packageManager) != null) {
                    ForegroundService.startForegroundService(
                        appContext,
                        selectorConfig!!.isCameraForegroundService
                    )
                    val videoUri = MediaStoreUtils.createCameraOutVideoUri(
                        appContext, selectorConfig
                    )
                    if (videoUri != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                        if (selectorConfig!!.isCameraAroundState) {
                            cameraIntent.putExtra(
                                PictureConfig.CAMERA_FACING,
                                PictureConfig.CAMERA_BEFORE
                            )
                        }
                        cameraIntent.putExtra(
                            PictureConfig.EXTRA_QUICK_CAPTURE,
                            selectorConfig!!.isQuickCapture
                        )
                        cameraIntent.putExtra(
                            MediaStore.EXTRA_DURATION_LIMIT,
                            selectorConfig!!.recordVideoMaxSecond
                        )
                        cameraIntent.putExtra(
                            MediaStore.EXTRA_VIDEO_QUALITY,
                            selectorConfig!!.videoQuality
                        )
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA)
                    }
                }
            }
        }
    }

    override fun openSoundRecording() {
        if (selectorConfig!!.onRecordAudioListener != null) {
            ForegroundService.startForegroundService(
                appContext,
                selectorConfig!!.isCameraForegroundService
            )
            selectorConfig!!.onRecordAudioListener.onRecordAudio(this, PictureConfig.REQUEST_CAMERA)
        } else {
            throw NullPointerException(OnRecordAudioInterceptListener::class.java.simpleName + " interface needs to be implemented for recording")
        }
    }

    /**
     * 拦截相机事件并处理返回结果
     */
    override fun onInterceptCameraEvent(cameraMode: Int) {
        ForegroundService.startForegroundService(
            appContext,
            selectorConfig!!.isCameraForegroundService
        )
        selectorConfig!!.onCameraInterceptListener.openCamera(
            this,
            cameraMode,
            PictureConfig.REQUEST_CAMERA
        )
    }

    /**
     * 权限申请
     *
     * @param permissionArray
     */
    override fun onApplyPermissionsEvent(event: Int, permissionArray: Array<String?>?) {
        selectorConfig!!.onPermissionsEventListener.requestPermission(
            this, permissionArray
        ) { permissionArray, isResult ->
            if (isResult) {
                if (event == PermissionEvent.EVENT_VIDEO_CAMERA) {
                    startCameraVideoCapture()
                } else {
                    startCameraImageCapture()
                }
            } else {
                handlePermissionDenied(permissionArray)
            }
        }
    }

    /**
     * 权限说明
     *
     * @param permissionArray
     */
    override fun onPermissionExplainEvent(
        isDisplayExplain: Boolean,
        permissionArray: Array<String?>?
    ) {
        if (selectorConfig!!.onPermissionDescriptionListener != null) {
            if (isDisplayExplain) {
                if (PermissionChecker.isCheckSelfPermission(appContext, permissionArray)) {
                    SpUtils.putBoolean(appContext, permissionArray!![0], false)
                } else {
                    if (!SpUtils.getBoolean(appContext, permissionArray!![0], false)) {
                        selectorConfig!!.onPermissionDescriptionListener.onPermissionDescription(
                            this,
                            permissionArray
                        )
                    }
                }
            } else {
                selectorConfig!!.onPermissionDescriptionListener.onDismiss(this)
            }
        }
    }

    /**
     * 点击选择的音效
     */
    private fun playClickEffect() {
        if (soundPool != null && selectorConfig!!.isOpenClickSound) {
            soundPool!!.play(soundID, 0.1f, 0.5f, 0, 1, 1f)
        }
    }

    /**
     * 释放音效资源
     */
    private fun releaseSoundPool() {
        try {
            if (soundPool != null) {
                soundPool!!.release()
                soundPool = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ForegroundService.stopService(appContext)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                dispatchHandleCamera(data)
            } else if (requestCode == Crop.REQUEST_EDIT_CROP) {
                onEditMedia(data)
            } else if (requestCode == Crop.REQUEST_CROP) {
                val selectedResult: List<LocalMedia> = selectorConfig!!.selectedResult
                try {
                    if (selectedResult.size == 1) {
                        val media = selectedResult[0]
                        val output = Crop.getOutput(
                            data!!
                        )
                        media.cutPath = if (output != null) output.path else ""
                        media.isCut = !TextUtils.isEmpty(media.cutPath)
                        media.cropImageWidth = Crop.getOutputImageWidth(
                            data
                        )
                        media.cropImageHeight = Crop.getOutputImageHeight(
                            data
                        )
                        media.cropOffsetX = Crop.getOutputImageOffsetX(
                            data
                        )
                        media.cropOffsetY = Crop.getOutputImageOffsetY(
                            data
                        )
                        media.cropResultAspectRatio = Crop.getOutputCropAspectRatio(
                            data
                        )
                        media.customData = Crop.getOutputCustomExtraData(
                            data
                        )
                        media.sandboxPath = media.cutPath
                    } else {
                        var extra = data!!.getStringExtra(MediaStore.EXTRA_OUTPUT)
                        if (TextUtils.isEmpty(extra)) {
                            extra = data.getStringExtra(CustomIntentKey.EXTRA_OUTPUT_URI)
                        }
                        val array = JSONArray(extra)
                        if (array.length() == selectedResult.size) {
                            for (i in selectedResult.indices) {
                                val media = selectedResult[i]
                                val item = array.optJSONObject(i)
                                media.cutPath = item.optString(CustomIntentKey.EXTRA_OUT_PUT_PATH)
                                media.isCut = !TextUtils.isEmpty(media.cutPath)
                                media.cropImageWidth =
                                    item.optInt(CustomIntentKey.EXTRA_IMAGE_WIDTH)
                                media.cropImageHeight =
                                    item.optInt(CustomIntentKey.EXTRA_IMAGE_HEIGHT)
                                media.cropOffsetX = item.optInt(CustomIntentKey.EXTRA_OFFSET_X)
                                media.cropOffsetY = item.optInt(CustomIntentKey.EXTRA_OFFSET_Y)
                                media.cropResultAspectRatio =
                                    item.optDouble(CustomIntentKey.EXTRA_ASPECT_RATIO).toFloat()
                                media.customData =
                                    item.optString(CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA)
                                media.sandboxPath = media.cutPath
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtils.showToast(appContext, e.message)
                }
                val result = ArrayList(selectedResult)
                if (checkCompressValidity()) {
                    onCompress(result)
                } else if (checkOldCompressValidity()) {
                    onOldCompress(result)
                } else {
                    onResultEvent(result)
                }
            }
        } else if (resultCode == Crop.RESULT_CROP_ERROR) {
            val throwable = if (data != null) Crop.getError(data) else Throwable("image crop error")
            if (throwable != null) {
                ToastUtils.showToast(appContext, throwable.message)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                if (!TextUtils.isEmpty(selectorConfig!!.cameraPath)) {
                    MediaUtils.deleteUri(appContext, selectorConfig!!.cameraPath)
                    selectorConfig!!.cameraPath = ""
                }
            } else if (requestCode == PictureConfig.REQUEST_GO_SETTING) {
                handlePermissionSettingResult(PermissionConfig.CURRENT_REQUEST_PERMISSION)
            }
        }
    }

    /**
     * 相机事件回调处理
     */
    private fun dispatchHandleCamera(intent: Intent?) {
        PictureThreadUtils.executeByIo(object : PictureThreadUtils.SimpleTask<LocalMedia?>() {
            override fun doInBackground(): LocalMedia {
                val outputPath = getOutputPath(intent)
                if (!TextUtils.isEmpty(outputPath)) {
                    selectorConfig!!.cameraPath = outputPath
                }
                if (TextUtils.isEmpty(selectorConfig!!.cameraPath)) {
                    return null
                }
                if (selectorConfig!!.chooseMode == SelectMimeType.ofAudio()) {
                    copyOutputAudioToDir()
                }
                val media = buildLocalMedia(
                    selectorConfig!!.cameraPath
                )
                media.isCameraSource = true
                return media
            }

            override fun onSuccess(result: LocalMedia) {
                PictureThreadUtils.cancel(this)
                if (result != null) {
                    onScannerScanFile(result)
                    dispatchCameraMediaResult(result)
                }
                selectorConfig!!.cameraPath = ""
            }
        })
    }

    /**
     * copy录音文件至指定目录
     */
    private fun copyOutputAudioToDir() {
        try {
            if (!TextUtils.isEmpty(selectorConfig!!.outPutAudioDir)) {
                val inputStream = if (PictureMimeType.isContent(
                        selectorConfig!!.cameraPath
                    )
                ) PictureContentResolver.openInputStream(
                    appContext, Uri.parse(selectorConfig!!.cameraPath)
                ) else FileInputStream(
                    selectorConfig!!.cameraPath
                )
                val audioFileName: String
                audioFileName = if (TextUtils.isEmpty(selectorConfig!!.outPutAudioFileName)) {
                    ""
                } else {
                    if (selectorConfig!!.isOnlyCamera) selectorConfig!!.outPutAudioFileName else System.currentTimeMillis()
                        .toString() + "_" + selectorConfig!!.outPutAudioFileName
                }
                val outputFile = PictureFileUtils.createCameraFile(
                    appContext,
                    selectorConfig!!.chooseMode, audioFileName, "", selectorConfig!!.outPutAudioDir
                )
                val outputStream = FileOutputStream(outputFile.absolutePath)
                if (PictureFileUtils.writeFileFromIS(inputStream, outputStream)) {
                    MediaUtils.deleteUri(appContext, selectorConfig!!.cameraPath)
                    selectorConfig!!.cameraPath = outputFile.absolutePath
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * 尝试匹配查找自定义相机返回的路径
     *
     * @param data
     * @return
     */
    protected fun getOutputPath(data: Intent?): String? {
        if (data == null) {
            return null
        }
        var outPutUri = data.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
        val cameraPath = selectorConfig!!.cameraPath
        val isCameraFileExists =
            TextUtils.isEmpty(cameraPath) || PictureMimeType.isContent(cameraPath) || File(
                cameraPath
            ).exists()
        if ((selectorConfig!!.chooseMode == SelectMimeType.ofAudio() || !isCameraFileExists) && outPutUri == null) {
            outPutUri = data.data
        }
        if (outPutUri == null) {
            return null
        }
        return if (PictureMimeType.isContent(outPutUri.toString())) outPutUri.toString() else outPutUri.path
    }

    /**
     * 刷新相册
     *
     * @param media 要刷新的对象
     */
    private fun onScannerScanFile(media: LocalMedia) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return
        }
        if (SdkVersionUtils.isQ()) {
            if (PictureMimeType.isHasVideo(media.mimeType) && PictureMimeType.isContent(media.path)) {
                PictureMediaScannerConnection(activity, media.realPath)
            }
        } else {
            val path = if (PictureMimeType.isContent(media.path)) media.realPath else media.path
            PictureMediaScannerConnection(activity, path)
            if (PictureMimeType.isHasImage(media.mimeType)) {
                val dirFile = File(path)
                val lastImageId = MediaUtils.getDCIMLastImageId(
                    appContext, dirFile.parent
                )
                if (lastImageId != -1) {
                    MediaUtils.removeMedia(appContext, lastImageId)
                }
            }
        }
    }

    /**
     * buildLocalMedia
     *
     * @param absolutePath
     */
    protected fun buildLocalMedia(absolutePath: String?): LocalMedia {
        val media = LocalMedia.generateLocalMedia(
            appContext, absolutePath
        )
        media.chooseModel = selectorConfig!!.chooseMode
        if (SdkVersionUtils.isQ() && !PictureMimeType.isContent(absolutePath)) {
            media.sandboxPath = absolutePath
        } else {
            media.sandboxPath = null
        }
        if (selectorConfig!!.isCameraRotateImage && PictureMimeType.isHasImage(media.mimeType)) {
            BitmapUtils.rotateImage(appContext, absolutePath)
        }
        return media
    }

    /**
     * 验证完成选择的先决条件
     *
     * @return
     */
    private fun checkCompleteSelectLimit(): Boolean {
        if (selectorConfig!!.selectionMode != SelectModeConfig.MULTIPLE || selectorConfig!!.isOnlyCamera) {
            return false
        }
        if (selectorConfig!!.isWithVideoImage) {
            // 共选型模式
            val selectedResult = selectorConfig!!.selectedResult
            var selectImageSize = 0
            var selectVideoSize = 0
            for (i in selectedResult.indices) {
                val mimeType = selectedResult[i].mimeType
                if (PictureMimeType.isHasVideo(mimeType)) {
                    selectVideoSize++
                } else {
                    selectImageSize++
                }
            }
            if (selectorConfig!!.minSelectNum > 0) {
                if (selectImageSize < selectorConfig!!.minSelectNum) {
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext,
                                null,
                                selectorConfig,
                                SelectLimitType.SELECT_MIN_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    showTipsDialog(
                        getString(
                            R.string.ps_min_img_num,
                            selectorConfig!!.minSelectNum.toString()
                        )
                    )
                    return true
                }
            }
            if (selectorConfig!!.minVideoSelectNum > 0) {
                if (selectVideoSize < selectorConfig!!.minVideoSelectNum) {
                    if (selectorConfig!!.onSelectLimitTipsListener != null) {
                        val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                            .onSelectLimitTips(
                                appContext,
                                null,
                                selectorConfig,
                                SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT
                            )
                        if (isSelectLimit) {
                            return true
                        }
                    }
                    showTipsDialog(
                        getString(
                            R.string.ps_min_video_num,
                            selectorConfig!!.minVideoSelectNum.toString()
                        )
                    )
                    return true
                }
            }
        } else {
            // 单类型模式
            val mimeType = selectorConfig!!.resultFirstMimeType
            if (PictureMimeType.isHasImage(mimeType) && selectorConfig!!.minSelectNum > 0 && selectorConfig!!.selectCount < selectorConfig!!.minSelectNum) {
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext,
                            null,
                            selectorConfig,
                            SelectLimitType.SELECT_MIN_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_min_img_num,
                        selectorConfig!!.minSelectNum.toString()
                    )
                )
                return true
            }
            if (PictureMimeType.isHasVideo(mimeType) && selectorConfig!!.minVideoSelectNum > 0 && selectorConfig!!.selectCount < selectorConfig!!.minVideoSelectNum) {
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext,
                            null,
                            selectorConfig,
                            SelectLimitType.SELECT_MIN_VIDEO_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_min_video_num,
                        selectorConfig!!.minVideoSelectNum.toString()
                    )
                )
                return true
            }
            if (PictureMimeType.isHasAudio(mimeType) && selectorConfig!!.minAudioSelectNum > 0 && selectorConfig!!.selectCount < selectorConfig!!.minAudioSelectNum) {
                if (selectorConfig!!.onSelectLimitTipsListener != null) {
                    val isSelectLimit = selectorConfig!!.onSelectLimitTipsListener
                        .onSelectLimitTips(
                            appContext,
                            null,
                            selectorConfig,
                            SelectLimitType.SELECT_MIN_AUDIO_SELECT_LIMIT
                        )
                    if (isSelectLimit) {
                        return true
                    }
                }
                showTipsDialog(
                    getString(
                        R.string.ps_min_audio_num,
                        selectorConfig!!.minAudioSelectNum.toString()
                    )
                )
                return true
            }
        }
        return false
    }

    /**
     * 分发处理结果，比如压缩、裁剪、沙盒路径转换
     */
    protected fun dispatchTransformResult() {
        if (checkCompleteSelectLimit()) {
            return
        }
        if (!isAdded) {
            return
        }
        val selectedResult = selectorConfig!!.selectedResult
        val result = ArrayList(selectedResult)
        if (checkCropValidity()) {
            onCrop(result)
        } else if (checkOldCropValidity()) {
            onOldCrop(result)
        } else if (checkCompressValidity()) {
            onCompress(result)
        } else if (checkOldCompressValidity()) {
            onOldCompress(result)
        } else {
            onResultEvent(result)
        }
    }

    override fun onCrop(result: ArrayList<LocalMedia>) {
        var srcUri: Uri? = null
        var destinationUri: Uri? = null
        val dataCropSource = ArrayList<String>()
        for (i in result.indices) {
            val media = result[i]
            dataCropSource.add(media.availablePath)
            if (srcUri == null && PictureMimeType.isHasImage(media.mimeType)) {
                val currentCropPath = media.availablePath
                srcUri =
                    if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(
                            currentCropPath
                        )
                    ) {
                        Uri.parse(currentCropPath)
                    } else {
                        Uri.fromFile(File(currentCropPath))
                    }
                val fileName = DateUtils.getCreateFileName("CROP_") + ".jpg"
                val context = appContext
                val externalFilesDir =
                    File(FileDirMap.getFileDirPath(context, SelectMimeType.TYPE_IMAGE))
                val outputFile = File(externalFilesDir.absolutePath, fileName)
                destinationUri = Uri.fromFile(outputFile)
            }
        }
        selectorConfig!!.cropFileEngine.onStartCrop(
            this,
            srcUri,
            destinationUri,
            dataCropSource,
            Crop.REQUEST_CROP
        )
    }

    override fun onOldCrop(result: ArrayList<LocalMedia>) {
        var currentLocalMedia: LocalMedia? = null
        for (i in result.indices) {
            val item = result[i]
            if (PictureMimeType.isHasImage(result[i].mimeType)) {
                currentLocalMedia = item
                break
            }
        }
        selectorConfig!!.cropEngine.onStartCrop(this, currentLocalMedia, result, Crop.REQUEST_CROP)
    }

    override fun onCompress(result: ArrayList<LocalMedia>) {
        showLoading()
        val queue = ConcurrentHashMap<String, LocalMedia>()
        val source = ArrayList<Uri>()
        for (i in result.indices) {
            val media = result[i]
            val availablePath = media.availablePath
            if (PictureMimeType.isHasHttp(availablePath)) {
                continue
            }
            if (selectorConfig!!.isCheckOriginalImage && selectorConfig!!.isOriginalSkipCompress) {
                continue
            }
            if (PictureMimeType.isHasImage(media.mimeType)) {
                val uri =
                    if (PictureMimeType.isContent(availablePath)) Uri.parse(availablePath) else Uri.fromFile(
                        File(availablePath)
                    )
                source.add(uri)
                queue[availablePath] = media
            }
        }
        if (queue.size == 0) {
            onResultEvent(result)
        } else {
            selectorConfig!!.compressFileEngine.onStartCompress(
                appContext,
                source
            ) { srcPath, compressPath ->
                if (TextUtils.isEmpty(srcPath)) {
                    onResultEvent(result)
                } else {
                    val media = queue[srcPath]
                    if (media != null) {
                        if (SdkVersionUtils.isQ()) {
                            if (!TextUtils.isEmpty(compressPath) && (compressPath.contains("Android/data/")
                                        || compressPath.contains("data/user/"))
                            ) {
                                media.compressPath = compressPath
                                media.isCompressed = !TextUtils.isEmpty(compressPath)
                                media.sandboxPath = media.compressPath
                            }
                        } else {
                            media.compressPath = compressPath
                            media.isCompressed = !TextUtils.isEmpty(compressPath)
                        }
                        queue.remove(srcPath)
                    }
                    if (queue.size == 0) {
                        onResultEvent(result)
                    }
                }
            }
        }
    }

    override fun onOldCompress(result: ArrayList<LocalMedia>) {
        showLoading()
        if (selectorConfig!!.isCheckOriginalImage && selectorConfig!!.isOriginalSkipCompress) {
            onResultEvent(result)
        } else {
            selectorConfig!!.compressEngine.onStartCompress(
                appContext, result,
                object : OnCallbackListener<ArrayList<LocalMedia?>?> {
                    override fun onCall(result: ArrayList<LocalMedia>) {
                        onResultEvent(result)
                    }
                })
        }
    }

    override fun checkCropValidity(): Boolean {
        if (selectorConfig!!.cropFileEngine != null) {
            val filterSet = HashSet<String>()
            val filters = selectorConfig!!.skipCropList
            if (filters != null && filters.size > 0) {
                filterSet.addAll(filters)
            }
            return if (selectorConfig!!.selectCount == 1) {
                val mimeType = selectorConfig!!.resultFirstMimeType
                val isHasImage = PictureMimeType.isHasImage(mimeType)
                if (isHasImage) {
                    if (filterSet.contains(mimeType)) {
                        return false
                    }
                }
                isHasImage
            } else {
                var notSupportCropCount = 0
                for (i in 0 until selectorConfig!!.selectCount) {
                    val media = selectorConfig!!.selectedResult[i]
                    if (PictureMimeType.isHasImage(media.mimeType)) {
                        if (filterSet.contains(media.mimeType)) {
                            notSupportCropCount++
                        }
                    }
                }
                notSupportCropCount != selectorConfig!!.selectCount
            }
        }
        return false
    }

    override fun checkOldCropValidity(): Boolean {
        if (selectorConfig!!.cropEngine != null) {
            val filterSet = HashSet<String>()
            val filters = selectorConfig!!.skipCropList
            if (filters != null && filters.size > 0) {
                filterSet.addAll(filters)
            }
            return if (selectorConfig!!.selectCount == 1) {
                val mimeType = selectorConfig!!.resultFirstMimeType
                val isHasImage = PictureMimeType.isHasImage(mimeType)
                if (isHasImage) {
                    if (filterSet.contains(mimeType)) {
                        return false
                    }
                }
                isHasImage
            } else {
                var notSupportCropCount = 0
                for (i in 0 until selectorConfig!!.selectCount) {
                    val media = selectorConfig!!.selectedResult[i]
                    if (PictureMimeType.isHasImage(media.mimeType)) {
                        if (filterSet.contains(media.mimeType)) {
                            notSupportCropCount++
                        }
                    }
                }
                notSupportCropCount != selectorConfig!!.selectCount
            }
        }
        return false
    }

    override fun checkCompressValidity(): Boolean {
        if (selectorConfig!!.compressFileEngine != null) {
            for (i in 0 until selectorConfig!!.selectCount) {
                val media = selectorConfig!!.selectedResult[i]
                if (PictureMimeType.isHasImage(media.mimeType)) {
                    return true
                }
            }
        }
        return false
    }

    override fun checkOldCompressValidity(): Boolean {
        if (selectorConfig!!.compressEngine != null) {
            for (i in 0 until selectorConfig!!.selectCount) {
                val media = selectorConfig!!.selectedResult[i]
                if (PictureMimeType.isHasImage(media.mimeType)) {
                    return true
                }
            }
        }
        return false
    }

    override fun checkTransformSandboxFile(): Boolean {
        return SdkVersionUtils.isQ() && selectorConfig!!.uriToFileTransformEngine != null
    }

    override fun checkOldTransformSandboxFile(): Boolean {
        return SdkVersionUtils.isQ() && selectorConfig!!.sandboxFileEngine != null
    }

    override fun checkAddBitmapWatermark(): Boolean {
        return selectorConfig!!.onBitmapWatermarkListener != null
    }

    override fun checkVideoThumbnail(): Boolean {
        return selectorConfig!!.onVideoThumbnailEventListener != null
    }

    /**
     * 处理视频的缩略图
     *
     * @param result
     */
    private fun videoThumbnail(result: ArrayList<LocalMedia>) {
        val queue = ConcurrentHashMap<String, LocalMedia>()
        for (i in result.indices) {
            val media = result[i]
            val availablePath = media.availablePath
            if (PictureMimeType.isHasVideo(media.mimeType) || PictureMimeType.isUrlHasVideo(
                    availablePath
                )
            ) {
                queue[availablePath] = media
            }
        }
        if (queue.size == 0) {
            onCallBackResult(result)
        } else {
            for ((key) in queue) {
                selectorConfig!!.onVideoThumbnailEventListener.onVideoThumbnail(
                    appContext,
                    key
                ) { srcPath, resultPath ->
                    val media = queue[srcPath]
                    if (media != null) {
                        media.videoThumbnailPath = resultPath
                        queue.remove(srcPath)
                    }
                    if (queue.size == 0) {
                        onCallBackResult(result)
                    }
                }
            }
        }
    }

    /**
     * 添加水印
     */
    private fun addBitmapWatermark(result: ArrayList<LocalMedia>) {
        val queue = ConcurrentHashMap<String, LocalMedia>()
        for (i in result.indices) {
            val media = result[i]
            if (PictureMimeType.isHasAudio(media.mimeType)) {
                continue
            }
            val availablePath = media.availablePath
            queue[availablePath] = media
        }
        if (queue.size == 0) {
            dispatchWatermarkResult(result)
        } else {
            for ((srcPath1, media1) in queue) {
                selectorConfig!!.onBitmapWatermarkListener.onAddBitmapWatermark(
                    appContext,
                    srcPath, media.mimeType
                ) { srcPath, resultPath ->
                    if (TextUtils.isEmpty(srcPath)) {
                        dispatchWatermarkResult(result)
                    } else {
                        val media = queue[srcPath]
                        if (media != null) {
                            media.watermarkPath = resultPath
                            queue.remove(srcPath)
                        }
                        if (queue.size == 0) {
                            dispatchWatermarkResult(result)
                        }
                    }
                }
            }
        }
    }

    /**
     * dispatchUriToFileTransformResult
     *
     * @param result
     */
    private fun dispatchUriToFileTransformResult(result: ArrayList<LocalMedia>) {
        showLoading()
        if (checkAddBitmapWatermark()) {
            addBitmapWatermark(result)
        } else if (checkVideoThumbnail()) {
            videoThumbnail(result)
        } else {
            onCallBackResult(result)
        }
    }

    /**
     * dispatchWatermarkResult
     *
     * @param result
     */
    private fun dispatchWatermarkResult(result: ArrayList<LocalMedia>) {
        if (checkVideoThumbnail()) {
            videoThumbnail(result)
        } else {
            onCallBackResult(result)
        }
    }

    /**
     * SDK > 29 把外部资源copy一份至应用沙盒内
     *
     * @param result
     */
    private fun uriToFileTransform29(result: ArrayList<LocalMedia>) {
        showLoading()
        val queue = ConcurrentHashMap<String, LocalMedia>()
        for (i in result.indices) {
            val media = result[i]
            queue[media.path] = media
        }
        if (queue.size == 0) {
            dispatchUriToFileTransformResult(result)
        } else {
            PictureThreadUtils.executeByIo<ArrayList<LocalMedia>>(object :
                PictureThreadUtils.SimpleTask<ArrayList<LocalMedia?>?>() {
                override fun doInBackground(): ArrayList<LocalMedia?>? {
                    for ((_, media1) in queue) {
                        if (selectorConfig!!.isCheckOriginalImage || TextUtils.isEmpty(media.sandboxPath)) {
                            selectorConfig!!.uriToFileTransformEngine.onUriToFileAsyncTransform(
                                appContext,
                                media.path,
                                media.mimeType,
                                OnKeyValueResultCallbackListener { srcPath, resultPath ->
                                    if (TextUtils.isEmpty(srcPath)) {
                                        return@OnKeyValueResultCallbackListener
                                    }
                                    val media = queue[srcPath]
                                    if (media != null) {
                                        if (TextUtils.isEmpty(media.sandboxPath)) {
                                            media.sandboxPath = resultPath
                                        }
                                        if (selectorConfig!!.isCheckOriginalImage) {
                                            media.originalPath = resultPath
                                            media.isOriginal = !TextUtils.isEmpty(resultPath)
                                        }
                                        queue.remove(srcPath)
                                    }
                                })
                        }
                    }
                    return result
                }

                override fun onSuccess(result: ArrayList<LocalMedia>) {
                    PictureThreadUtils.cancel(this)
                    dispatchUriToFileTransformResult(result)
                }
            })
        }
    }

    /**
     * SDK > 29 把外部资源copy一份至应用沙盒内
     *
     * @param result
     */
    @Deprecated("")
    private fun copyExternalPathToAppInDirFor29(result: ArrayList<LocalMedia>) {
        showLoading()
        PictureThreadUtils.executeByIo<ArrayList<LocalMedia>>(object :
            PictureThreadUtils.SimpleTask<ArrayList<LocalMedia?>?>() {
            override fun doInBackground(): ArrayList<LocalMedia?>? {
                for (i in result.indices) {
                    val media = result[i]
                    selectorConfig!!.sandboxFileEngine.onStartSandboxFileTransform(
                        appContext, selectorConfig!!.isCheckOriginalImage, i,
                        media, object : OnCallbackIndexListener<LocalMedia?> {
                            override fun onCall(data: LocalMedia, index: Int) {
                                val media = result[index]
                                media.sandboxPath = data.sandboxPath
                                if (selectorConfig!!.isCheckOriginalImage) {
                                    media.originalPath = data.originalPath
                                    media.isOriginal = !TextUtils.isEmpty(data.originalPath)
                                }
                            }
                        })
                }
                return result
            }

            override fun onSuccess(result: ArrayList<LocalMedia>) {
                PictureThreadUtils.cancel(this)
                dispatchUriToFileTransformResult(result)
            }
        })
    }

    /**
     * 构造原图数据
     *
     * @param result
     */
    private fun mergeOriginalImage(result: ArrayList<LocalMedia>) {
        if (selectorConfig!!.isCheckOriginalImage) {
            for (i in result.indices) {
                val media = result[i]
                media.isOriginal = true
                media.originalPath = media.path
            }
        }
    }

    /**
     * 返回处理完成后的选择结果
     */
    override fun onResultEvent(result: ArrayList<LocalMedia>) {
        if (checkTransformSandboxFile()) {
            uriToFileTransform29(result)
        } else if (checkOldTransformSandboxFile()) {
            copyExternalPathToAppInDirFor29(result)
        } else {
            mergeOriginalImage(result)
            dispatchUriToFileTransformResult(result)
        }
    }

    /**
     * 返回结果
     */
    private fun onCallBackResult(result: ArrayList<LocalMedia>) {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            dismissLoading()
            if (selectorConfig!!.isActivityResultBack) {
                activity!!.setResult(
                    Activity.RESULT_OK,
                    PictureSelector.Companion.putIntentResult(result)
                )
                onSelectFinish(Activity.RESULT_OK, result)
            } else {
                if (selectorConfig!!.onResultCallListener != null) {
                    selectorConfig!!.onResultCallListener.onResult(result)
                }
            }
            onExitPictureSelector()
        }
    }

    /**
     * set app language
     */
    override fun initAppLanguage() {
        if (selectorConfig == null) {
            selectorConfig = SelectorProviders.instance.selectorConfig
        }
        if (selectorConfig != null && selectorConfig!!.language != LanguageConfig.UNKNOWN_LANGUAGE) {
            PictureLanguageUtils.setAppLanguage(
                activity,
                selectorConfig!!.language,
                selectorConfig!!.defaultLanguage
            )
        }
    }

    override fun onRecreateEngine() {
        createImageLoaderEngine()
        createVideoPlayerEngine()
        createCompressEngine()
        createSandboxFileEngine()
        createLoaderDataEngine()
        createResultCallbackListener()
        createLayoutResourceListener()
    }

    override fun onKeyBackFragmentFinish() {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            if (selectorConfig!!.isActivityResultBack) {
                activity!!.setResult(Activity.RESULT_CANCELED)
                onSelectFinish(Activity.RESULT_CANCELED, null)
            } else {
                if (selectorConfig!!.onResultCallListener != null) {
                    selectorConfig!!.onResultCallListener.onCancel()
                }
            }
            onExitPictureSelector()
        }
    }

    override fun onDestroy() {
        releaseSoundPool()
        super.onDestroy()
    }

    override fun showLoading() {
        try {
            if (ActivityCompatHelper.isDestroy(activity)) {
                return
            }
            if (!mLoadingDialog!!.isShowing) {
                mLoadingDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismissLoading() {
        try {
            if (ActivityCompatHelper.isDestroy(activity)) {
                return
            }
            if (mLoadingDialog!!.isShowing) {
                mLoadingDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        initAppLanguage()
        onRecreateEngine()
        super.onAttach(context)
        this.context = context
        if (parentFragment is IBridgePictureBehavior) {
            iBridgePictureBehavior = parentFragment as IBridgePictureBehavior?
        } else if (context is IBridgePictureBehavior) {
            iBridgePictureBehavior = context
        }
    }

    /**
     * setRequestedOrientation
     */
    protected fun setRequestedOrientation() {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return
        }
        activity!!.requestedOrientation = selectorConfig!!.requestedOrientation
    }

    /**
     * back current Fragment
     */
    protected fun onBackCurrentFragment() {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            if (!isStateSaved) {
                if (selectorConfig!!.viewLifecycle != null) {
                    selectorConfig!!.viewLifecycle.onDestroy(this)
                }
                activity!!.supportFragmentManager.popBackStack()
            }
            val fragments = activity!!.supportFragmentManager.fragments
            for (i in fragments.indices) {
                val fragment = fragments[i]
                if (fragment is PictureCommonFragment) {
                    fragment.onFragmentResume()
                }
            }
        }
    }

    /**
     * onSelectFinish
     *
     * @param resultCode
     * @param result
     */
    protected fun onSelectFinish(resultCode: Int, result: ArrayList<LocalMedia>?) {
        if (null != iBridgePictureBehavior) {
            val selectorResult = getResult(resultCode, result)
            iBridgePictureBehavior!!.onSelectFinish(selectorResult)
        }
    }

    /**
     * exit PictureSelector
     */
    protected open fun onExitPictureSelector() {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            if (isNormalDefaultEnter) {
                if (selectorConfig!!.viewLifecycle != null) {
                    selectorConfig!!.viewLifecycle.onDestroy(this)
                }
                activity!!.finish()
            } else {
                val fragments = activity!!.supportFragmentManager.fragments
                for (i in fragments.indices) {
                    val fragment = fragments[i]
                    if (fragment is PictureCommonFragment) {
                        onBackCurrentFragment()
                    }
                }
            }
        }
        SelectorProviders.instance.destroy()
    }

    /**
     * Get the image loading engine again, provided that the user implements the IApp interface in the Application
     */
    private fun createImageLoaderEngine() {
        if (selectorConfig!!.imageEngine == null) {
            val baseEngine = instance!!.pictureSelectorEngine
            if (baseEngine != null) {
                selectorConfig!!.imageEngine = baseEngine.createImageLoaderEngine()
            }
        }
    }

    /**
     * Get the video player engine again, provided that the user implements the IApp interface in the Application
     */
    private fun createVideoPlayerEngine() {
        if (selectorConfig!!.videoPlayerEngine == null) {
            val baseEngine = instance!!.pictureSelectorEngine
            if (baseEngine != null) {
                selectorConfig!!.videoPlayerEngine = baseEngine.createVideoPlayerEngine()
            }
        }
    }

    /**
     * Get the image loader data engine again, provided that the user implements the IApp interface in the Application
     */
    private fun createLoaderDataEngine() {
        if (selectorConfig!!.isLoaderDataEngine) {
            if (selectorConfig!!.loaderDataEngine == null) {
                val baseEngine = instance!!.pictureSelectorEngine
                if (baseEngine != null) selectorConfig!!.loaderDataEngine =
                    baseEngine.createLoaderDataEngine()
            }
        }
        if (selectorConfig!!.isLoaderFactoryEngine) {
            if (selectorConfig!!.loaderFactory == null) {
                val baseEngine = instance!!.pictureSelectorEngine
                if (baseEngine != null) selectorConfig!!.loaderFactory = baseEngine.onCreateLoader()
            }
        }
    }

    /**
     * Get the image compress engine again, provided that the user implements the IApp interface in the Application
     */
    private fun createCompressEngine() {
        if (selectorConfig!!.isCompressEngine) {
            if (selectorConfig!!.compressFileEngine == null) {
                val baseEngine = instance!!.pictureSelectorEngine
                if (baseEngine != null) selectorConfig!!.compressFileEngine =
                    baseEngine.createCompressFileEngine()
            }
            if (selectorConfig!!.compressEngine == null) {
                val baseEngine = instance!!.pictureSelectorEngine
                if (baseEngine != null) selectorConfig!!.compressEngine =
                    baseEngine.createCompressEngine()
            }
        }
    }

    /**
     * Get the Sandbox engine again, provided that the user implements the IApp interface in the Application
     */
    private fun createSandboxFileEngine() {
        if (selectorConfig!!.isSandboxFileEngine) {
            if (selectorConfig!!.uriToFileTransformEngine == null) {
                val baseEngine = instance!!.pictureSelectorEngine
                if (baseEngine != null) selectorConfig!!.uriToFileTransformEngine =
                    baseEngine.createUriToFileTransformEngine()
            }
            if (selectorConfig!!.sandboxFileEngine == null) {
                val baseEngine = instance!!.pictureSelectorEngine
                if (baseEngine != null) selectorConfig!!.sandboxFileEngine =
                    baseEngine.createSandboxFileEngine()
            }
        }
    }

    /**
     * Retrieve the result callback listener, provided that the user implements the IApp interface in the Application
     */
    private fun createResultCallbackListener() {
        if (selectorConfig!!.isResultListenerBack) {
            if (selectorConfig!!.onResultCallListener == null) {
                val baseEngine = instance!!.pictureSelectorEngine
                if (baseEngine != null) {
                    selectorConfig!!.onResultCallListener = baseEngine.resultCallbackListener
                }
            }
        }
    }

    /**
     * Retrieve the layout callback listener, provided that the user implements the IApp interface in the Application
     */
    private fun createLayoutResourceListener() {
        if (selectorConfig!!.isInjectLayoutResource) {
            if (selectorConfig!!.onLayoutResourceListener == null) {
                val baseEngine = instance!!.pictureSelectorEngine
                if (baseEngine != null) {
                    selectorConfig!!.onLayoutResourceListener =
                        baseEngine.createLayoutResourceListener()
                }
            }
        }
    }

    /**
     * generate result
     *
     * @param data result
     * @return
     */
    protected fun getResult(resultCode: Int, data: ArrayList<LocalMedia>?): SelectorResult {
        return SelectorResult(
            resultCode,
            if (data != null) PictureSelector.Companion.putIntentResult(data) else null
        )
    }

    /**
     * SelectorResult
     */
    class SelectorResult(var mResultCode: Int, var mResultData: Intent?)
    companion object {
        val TAG = PictureCommonFragment::class.java.simpleName

        /**
         * 根据类型获取相应的Toast文案
         *
         * @param context
         * @param mimeType
         * @param maxSelectNum
         * @return
         */
        @SuppressLint("StringFormatInvalid")
        private fun getTipsMsg(context: Context?, mimeType: String, maxSelectNum: Int): String {
            return if (PictureMimeType.isHasVideo(mimeType)) {
                context!!.getString(
                    R.string.ps_message_video_max_num,
                    maxSelectNum.toString()
                )
            } else if (PictureMimeType.isHasAudio(mimeType)) {
                context!!.getString(
                    R.string.ps_message_audio_max_num,
                    maxSelectNum.toString()
                )
            } else {
                context!!.getString(
                    R.string.ps_message_max_num,
                    maxSelectNum.toString()
                )
            }
        }
    }
}