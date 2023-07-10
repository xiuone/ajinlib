package com.luck.picture.lib

import android.annotation.SuppressLint
import android.app.Service
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.luck.picture.lib.adapter.PictureImageGridAdapter
import com.luck.picture.lib.animators.AlphaInAnimationAdapter
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.animators.SlideInBottomAnimationAdapter
import com.luck.picture.lib.basic.FragmentInjectManager
import com.luck.picture.lib.basic.IPictureSelectorEvent
import com.luck.picture.lib.basic.PictureCommonFragment
import com.luck.picture.lib.config.*
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.dialog.AlbumListPopWindow
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaFolder
import com.luck.picture.lib.interfaces.*
import com.luck.picture.lib.loader.IBridgeMediaLoader
import com.luck.picture.lib.loader.LocalMediaLoader
import com.luck.picture.lib.loader.LocalMediaPageLoader
import com.luck.picture.lib.magical.BuildRecycleItemViewParams
import com.luck.picture.lib.manager.SelectedManager
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.utils.*
import com.luck.picture.lib.widget.*
import java.io.File
import java.lang.Exception
import java.lang.NullPointerException
import java.util.ArrayList
import java.util.HashSet

/**
 * @author：luck
 * @date：2021/11/17 10:24 上午
 * @describe：PictureSelectorFragment
 */
class PictureSelectorFragment : PictureCommonFragment(), OnRecyclerViewPreloadMoreListener,
    IPictureSelectorEvent {
    private var mRecycler: RecyclerPreloadView? = null
    private var tvDataEmpty: TextView? = null
    private var titleBar: TitleBar? = null
    private var bottomNarBar: BottomNavBar? = null
    private var completeSelectView: CompleteSelectView? = null
    private var tvCurrentDataTime: TextView? = null
    private var intervalClickTime: Long = 0
    private var allFolderSize = 0
    private var currentPosition = -1

    /**
     * Use camera to callback
     */
    private var isCameraCallback = false

    /**
     * memory recycling
     */
    private var isMemoryRecycling = false
    private var isDisplayCamera = false
    private var mAdapter: PictureImageGridAdapter? = null
    private var albumListPopWindow: AlbumListPopWindow? = null
    private var mDragSelectTouchListener: SlideSelectTouchListener? = null
    override fun getFragmentTag(): String {
        return TAG
    }

    override fun getResourceId(): Int {
        val layoutResourceId = InjectResourceSource.getLayoutResource(
            context, InjectResourceSource.MAIN_SELECTOR_LAYOUT_RESOURCE, selectorConfig
        )
        return if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) {
            layoutResourceId
        } else R.layout.ps_fragment_selector
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSelectedChange(isAddRemove: Boolean, currentMedia: LocalMedia) {
        bottomNarBar!!.setSelectedChange()
        completeSelectView!!.setSelectedChange(false)
        // 刷新列表数据
        if (checkNotifyStrategy(isAddRemove)) {
            mAdapter!!.notifyItemPositionChanged(currentMedia.position)
            mRecycler!!.postDelayed(
                { mAdapter!!.notifyDataSetChanged() },
                SELECT_ANIM_DURATION.toLong()
            )
        } else {
            mAdapter!!.notifyItemPositionChanged(currentMedia.position)
        }
        if (!isAddRemove) {
            sendChangeSubSelectPositionEvent(true)
        }
    }

    override fun onFixedSelectedChange(oldLocalMedia: LocalMedia) {
        mAdapter!!.notifyItemPositionChanged(oldLocalMedia.position)
    }

    override fun sendChangeSubSelectPositionEvent(adapterChange: Boolean) {
        if (selectorConfig.selectorStyle.selectMainStyle.isSelectNumberStyle) {
            for (index in 0 until selectorConfig.selectCount) {
                val media = selectorConfig.selectedResult[index]
                media.num = index + 1
                if (adapterChange) {
                    mAdapter!!.notifyItemPositionChanged(media.position)
                }
            }
        }
    }

    override fun onCheckOriginalChange() {
        bottomNarBar!!.setOriginalCheck()
    }

    /**
     * 刷新列表策略
     *
     * @param isAddRemove
     * @return
     */
    private fun checkNotifyStrategy(isAddRemove: Boolean): Boolean {
        var isNotifyAll = false
        if (selectorConfig.isMaxSelectEnabledMask) {
            if (selectorConfig.isWithVideoImage) {
                if (selectorConfig.selectionMode == SelectModeConfig.SINGLE) {
                    // ignore
                } else {
                    isNotifyAll = (selectorConfig.selectCount == selectorConfig.maxSelectNum
                            || !isAddRemove && selectorConfig.selectCount == selectorConfig.maxSelectNum - 1)
                }
            } else {
                isNotifyAll =
                    if (selectorConfig.selectCount == 0 || isAddRemove && selectorConfig.selectCount == 1) {
                        // 首次添加或单选，选择数量变为0了，都notifyDataSetChanged
                        true
                    } else {
                        if (PictureMimeType.isHasVideo(selectorConfig.resultFirstMimeType)) {
                            val maxSelectNum =
                                if (selectorConfig.maxVideoSelectNum > 0) selectorConfig.maxVideoSelectNum else selectorConfig.maxSelectNum
                            (selectorConfig.selectCount == maxSelectNum
                                    || !isAddRemove && selectorConfig.selectCount == maxSelectNum - 1)
                        } else {
                            (selectorConfig.selectCount == selectorConfig.maxSelectNum
                                    || !isAddRemove && selectorConfig.selectCount == selectorConfig.maxSelectNum - 1)
                        }
                    }
            }
        }
        return isNotifyAll
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE, allFolderSize)
        outState.putInt(PictureConfig.EXTRA_CURRENT_PAGE, mPage)
        outState.putInt(
            PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION,
            mRecycler!!.lastVisiblePosition
        )
        outState.putBoolean(PictureConfig.EXTRA_DISPLAY_CAMERA, mAdapter!!.isDisplayCamera)
        selectorConfig.addAlbumDataSource(albumListPopWindow!!.albumList)
        selectorConfig.addDataSource(mAdapter!!.data)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reStartSavedInstance(savedInstanceState!!)
        isMemoryRecycling = savedInstanceState != null
        tvDataEmpty = view.findViewById(R.id.tv_data_empty)
        completeSelectView = view.findViewById(R.id.ps_complete_select)
        titleBar = view.findViewById(R.id.title_bar)
        bottomNarBar = view.findViewById(R.id.bottom_nar_bar)
        tvCurrentDataTime = view.findViewById(R.id.tv_current_data_time)
        onCreateLoader()
        initAlbumListPopWindow()
        initTitleBar()
        initComplete()
        initRecycler(view)
        initBottomNavBar()
        if (isMemoryRecycling) {
            recoverSaveInstanceData()
        } else {
            requestLoadData()
        }
    }

    override fun onFragmentResume() {
        setRootViewKeyListener(requireView())
    }

    override fun reStartSavedInstance(savedInstanceState: Bundle) {
        if (savedInstanceState != null) {
            allFolderSize = savedInstanceState.getInt(PictureConfig.EXTRA_ALL_FOLDER_SIZE)
            mPage = savedInstanceState.getInt(PictureConfig.EXTRA_CURRENT_PAGE, mPage)
            currentPosition = savedInstanceState.getInt(
                PictureConfig.EXTRA_PREVIEW_CURRENT_POSITION,
                currentPosition
            )
            isDisplayCamera = savedInstanceState.getBoolean(
                PictureConfig.EXTRA_DISPLAY_CAMERA,
                selectorConfig.isDisplayCamera
            )
        } else {
            isDisplayCamera = selectorConfig.isDisplayCamera
        }
    }

    /**
     * 完成按钮
     */
    private fun initComplete() {
        if (selectorConfig.selectionMode == SelectModeConfig.SINGLE && selectorConfig.isDirectReturnSingle) {
            selectorConfig.selectorStyle.titleBarStyle.isHideCancelButton = false
            titleBar!!.titleCancelView.visibility = View.VISIBLE
            completeSelectView!!.visibility = View.GONE
        } else {
            completeSelectView!!.setCompleteSelectViewStyle()
            completeSelectView!!.setSelectedChange(false)
            val selectMainStyle = selectorConfig.selectorStyle.selectMainStyle
            if (selectMainStyle.isCompleteSelectRelativeTop) {
                if (completeSelectView!!.layoutParams is ConstraintLayout.LayoutParams) {
                    (completeSelectView!!.layoutParams as ConstraintLayout.LayoutParams).topToTop =
                        R.id.title_bar
                    (completeSelectView!!.layoutParams as ConstraintLayout.LayoutParams).bottomToBottom =
                        R.id.title_bar
                    if (selectorConfig.isPreviewFullScreenMode) {
                        (completeSelectView
                            .getLayoutParams() as ConstraintLayout.LayoutParams).topMargin =
                            DensityUtil.getStatusBarHeight(
                                context
                            )
                    }
                } else if (completeSelectView!!.layoutParams is RelativeLayout.LayoutParams) {
                    if (selectorConfig.isPreviewFullScreenMode) {
                        (completeSelectView
                            .getLayoutParams() as RelativeLayout.LayoutParams).topMargin =
                            DensityUtil.getStatusBarHeight(
                                context
                            )
                    }
                }
            }
            completeSelectView!!.setOnClickListener {
                if (selectorConfig.isEmptyResultReturn && selectorConfig.selectCount == 0) {
                    onExitPictureSelector()
                } else {
                    dispatchTransformResult()
                }
            }
        }
    }

    override fun onCreateLoader() {
        if (selectorConfig.loaderFactory != null) {
            mLoader = selectorConfig.loaderFactory.onCreateLoader()
            if (mLoader == null) {
                throw NullPointerException("No available " + IBridgeMediaLoader::class.java + " loader found")
            }
        } else {
            mLoader = if (selectorConfig.isPageStrategy) LocalMediaPageLoader(
                appContext, selectorConfig
            ) else LocalMediaLoader(
                appContext, selectorConfig
            )
        }
    }

    private fun initTitleBar() {
        if (selectorConfig.selectorStyle.titleBarStyle.isHideTitleBar) {
            titleBar!!.visibility = View.GONE
        }
        titleBar!!.setTitleBarStyle()
        titleBar!!.setOnTitleBarListener(object : TitleBar.OnTitleBarListener() {
            override fun onTitleDoubleClick() {
                if (selectorConfig.isAutomaticTitleRecyclerTop) {
                    val intervalTime = 500
                    if (SystemClock.uptimeMillis() - intervalClickTime < intervalTime && mAdapter!!.itemCount > 0) {
                        mRecycler!!.scrollToPosition(0)
                    } else {
                        intervalClickTime = SystemClock.uptimeMillis()
                    }
                }
            }

            override fun onBackPressed() {
                if (albumListPopWindow!!.isShowing) {
                    albumListPopWindow!!.dismiss()
                } else {
                    onKeyBackFragmentFinish()
                }
            }

            override fun onShowAlbumPopWindow(anchor: View) {
                albumListPopWindow!!.showAsDropDown(anchor)
            }
        })
    }

    /**
     * initAlbumListPopWindow
     */
    private fun initAlbumListPopWindow() {
        albumListPopWindow = AlbumListPopWindow.buildPopWindow(context, selectorConfig)
        albumListPopWindow.setOnPopupWindowStatusListener(object :
            AlbumListPopWindow.OnPopupWindowStatusListener {
            override fun onShowPopupWindow() {
                if (!selectorConfig.isOnlySandboxDir) {
                    AnimUtils.rotateArrow(titleBar!!.imageArrow, true)
                }
            }

            override fun onDismissPopupWindow() {
                if (!selectorConfig.isOnlySandboxDir) {
                    AnimUtils.rotateArrow(titleBar!!.imageArrow, false)
                }
            }
        })
        addAlbumPopWindowAction()
    }

    private fun recoverSaveInstanceData() {
        mAdapter!!.isDisplayCamera = isDisplayCamera
        enterAnimationDuration = 0
        if (selectorConfig.isOnlySandboxDir) {
            handleInAppDirAllMedia(selectorConfig.currentLocalMediaFolder)
        } else {
            handleRecoverAlbumData(ArrayList(selectorConfig.albumDataSource))
        }
    }

    private fun handleRecoverAlbumData(albumData: List<LocalMediaFolder>) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return
        }
        if (albumData.size > 0) {
            val firstFolder: LocalMediaFolder
            if (selectorConfig.currentLocalMediaFolder != null) {
                firstFolder = selectorConfig.currentLocalMediaFolder
            } else {
                firstFolder = albumData[0]
                selectorConfig.currentLocalMediaFolder = firstFolder
            }
            titleBar!!.setTitle(firstFolder.folderName)
            albumListPopWindow!!.bindAlbumData(albumData)
            if (selectorConfig.isPageStrategy) {
                handleFirstPageMedia(ArrayList(selectorConfig.dataSource), true)
            } else {
                setAdapterData(firstFolder.data)
            }
        } else {
            showDataNull()
        }
    }

    private fun requestLoadData() {
        mAdapter!!.isDisplayCamera = isDisplayCamera
        if (PermissionChecker.isCheckReadStorage(selectorConfig.chooseMode, context)) {
            beginLoadData()
        } else {
            val readPermissionArray: Array<String> = PermissionConfig.getReadPermissionArray(
                appContext, selectorConfig.chooseMode
            )
            onPermissionExplainEvent(true, readPermissionArray)
            if (selectorConfig.onPermissionsEventListener != null) {
                onApplyPermissionsEvent(PermissionEvent.EVENT_SOURCE_DATA, readPermissionArray)
            } else {
                PermissionChecker.getInstance().requestPermissions(
                    this,
                    readPermissionArray,
                    object : PermissionResultCallback() {
                        fun onGranted() {
                            beginLoadData()
                        }

                        fun onDenied() {
                            handlePermissionDenied(readPermissionArray)
                        }
                    })
            }
        }
    }

    override fun onApplyPermissionsEvent(event: Int, permissionArray: Array<String>) {
        if (event != PermissionEvent.EVENT_SOURCE_DATA) {
            super.onApplyPermissionsEvent(event, permissionArray)
        } else {
            selectorConfig.onPermissionsEventListener.requestPermission(
                this,
                permissionArray
            ) { permissionArray, isResult ->
                if (isResult) {
                    beginLoadData()
                } else {
                    handlePermissionDenied(permissionArray)
                }
            }
        }
    }

    /**
     * 开始获取数据
     */
    private fun beginLoadData() {
        onPermissionExplainEvent(false, null)
        if (selectorConfig.isOnlySandboxDir) {
            loadOnlyInAppDirectoryAllMediaData()
        } else {
            loadAllAlbumData()
        }
    }

    override fun handlePermissionSettingResult(permissions: Array<String>) {
        if (permissions == null) {
            return
        }
        onPermissionExplainEvent(false, null)
        val isHasCamera =
            permissions.size > 0 && TextUtils.equals(permissions[0], PermissionConfig.CAMERA.get(0))
        val isHasPermissions: Boolean
        isHasPermissions = if (selectorConfig.onPermissionsEventListener != null) {
            selectorConfig.onPermissionsEventListener.hasPermissions(this, permissions)
        } else {
            PermissionChecker.isCheckSelfPermission(context, permissions)
        }
        if (isHasPermissions) {
            if (isHasCamera) {
                openSelectedCamera()
            } else {
                beginLoadData()
            }
        } else {
            if (isHasCamera) {
                ToastUtils.showToast(context, getString(R.string.ps_camera))
            } else {
                ToastUtils.showToast(context, getString(R.string.ps_jurisdiction))
                onKeyBackFragmentFinish()
            }
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = arrayOf<String>()
    }

    /**
     * 给AlbumListPopWindow添加事件
     */
    private fun addAlbumPopWindowAction() {
        albumListPopWindow!!.setOnIBridgeAlbumWidget { position, curFolder ->
            isDisplayCamera =
                selectorConfig.isDisplayCamera && curFolder.bucketId == PictureConfig.ALL.toLong()
            mAdapter!!.isDisplayCamera = isDisplayCamera
            titleBar!!.setTitle(curFolder.folderName)
            val lastFolder = selectorConfig.currentLocalMediaFolder
            val lastBucketId = lastFolder.bucketId
            if (selectorConfig.isPageStrategy) {
                if (curFolder.bucketId != lastBucketId) {
                    // 1、记录一下上一次相册数据加载到哪了，到时候切回来的时候要续上
                    lastFolder.data = mAdapter!!.data
                    lastFolder.currentDataPage = mPage
                    lastFolder.isHasMore = mRecycler!!.isEnabledLoadMore

                    // 2、判断当前相册是否请求过，如果请求过则不从MediaStore去拉取了
                    if (curFolder.data.size > 0 && !curFolder.isHasMore) {
                        setAdapterData(curFolder.data)
                        mPage = curFolder.currentDataPage
                        mRecycler!!.isEnabledLoadMore = curFolder.isHasMore
                        mRecycler!!.smoothScrollToPosition(0)
                    } else {
                        // 3、从MediaStore拉取数据
                        mPage = 1
                        if (selectorConfig.loaderDataEngine != null) {
                            selectorConfig.loaderDataEngine.loadFirstPageMediaData(
                                context,
                                curFolder.bucketId, mPage, selectorConfig.pageSize,
                                object : OnQueryDataResultListener<LocalMedia?>() {
                                    override fun onComplete(
                                        result: ArrayList<LocalMedia>,
                                        isHasMore: Boolean
                                    ) {
                                        handleSwitchAlbum(result, isHasMore)
                                    }
                                })
                        } else {
                            mLoader.loadPageMediaData(curFolder.bucketId,
                                mPage,
                                selectorConfig.pageSize,
                                object : OnQueryDataResultListener<LocalMedia?>() {
                                    override fun onComplete(
                                        result: ArrayList<LocalMedia>,
                                        isHasMore: Boolean
                                    ) {
                                        handleSwitchAlbum(result, isHasMore)
                                    }
                                })
                        }
                    }
                }
            } else {
                // 非分页模式直接导入该相册下的所有资源
                if (curFolder.bucketId != lastBucketId) {
                    setAdapterData(curFolder.data)
                    mRecycler!!.smoothScrollToPosition(0)
                }
            }
            selectorConfig.currentLocalMediaFolder = curFolder
            albumListPopWindow!!.dismiss()
            if (mDragSelectTouchListener != null && selectorConfig.isFastSlidingSelect) {
                mDragSelectTouchListener!!.setRecyclerViewHeaderCount(if (mAdapter!!.isDisplayCamera) 1 else 0)
            }
        }
    }

    private fun handleSwitchAlbum(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return
        }
        mRecycler!!.isEnabledLoadMore = isHasMore
        if (result.size == 0) {
            // 如果从MediaStore拉取都没有数据了，adapter里的可能是缓存所以也清除
            mAdapter!!.data.clear()
        }
        setAdapterData(result)
        mRecycler!!.onScrolled(0, 0)
        mRecycler!!.smoothScrollToPosition(0)
    }

    private fun initBottomNavBar() {
        bottomNarBar!!.setBottomNavBarStyle()
        bottomNarBar!!.setOnBottomNavBarListener(object : BottomNavBar.OnBottomNavBarListener() {
            override fun onPreview() {
                onStartPreview(0, true)
            }

            override fun onCheckOriginalChange() {
                sendSelectedOriginalChangeEvent()
            }
        })
        bottomNarBar!!.setSelectedChange()
    }

    override fun loadAllAlbumData() {
        if (selectorConfig.loaderDataEngine != null) {
            selectorConfig.loaderDataEngine.loadAllAlbumData(
                context,
                object : OnQueryAllAlbumListener<LocalMediaFolder?> {
                    override fun onComplete(result: List<LocalMediaFolder>) {
                        handleAllAlbumData(false, result)
                    }
                })
        } else {
            val isPreload = preloadPageFirstData()
            mLoader.loadAllAlbum(object : OnQueryAllAlbumListener<LocalMediaFolder?> {
                override fun onComplete(result: List<LocalMediaFolder>) {
                    handleAllAlbumData(isPreload, result)
                }
            })
        }
    }

    private fun preloadPageFirstData(): Boolean {
        var isPreload = false
        if (selectorConfig.isPageStrategy && selectorConfig.isPreloadFirst) {
            val firstFolder = LocalMediaFolder()
            firstFolder.bucketId = PictureConfig.ALL.toLong()
            if (TextUtils.isEmpty(selectorConfig.defaultAlbumName)) {
                titleBar!!.setTitle(
                    if (selectorConfig.chooseMode == SelectMimeType.ofAudio()) requireContext().getString(
                        R.string.ps_all_audio
                    ) else requireContext().getString(R.string.ps_camera_roll)
                )
            } else {
                titleBar!!.setTitle(selectorConfig.defaultAlbumName)
            }
            firstFolder.folderName = titleBar!!.titleText
            selectorConfig.currentLocalMediaFolder = firstFolder
            loadFirstPageMediaData(firstFolder.bucketId)
            isPreload = true
        }
        return isPreload
    }

    private fun handleAllAlbumData(isPreload: Boolean, result: List<LocalMediaFolder>) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return
        }
        if (result.size > 0) {
            val firstFolder: LocalMediaFolder
            if (isPreload) {
                firstFolder = result[0]
                selectorConfig.currentLocalMediaFolder = firstFolder
            } else {
                if (selectorConfig.currentLocalMediaFolder != null) {
                    firstFolder = selectorConfig.currentLocalMediaFolder
                } else {
                    firstFolder = result[0]
                    selectorConfig.currentLocalMediaFolder = firstFolder
                }
            }
            titleBar!!.setTitle(firstFolder.folderName)
            albumListPopWindow!!.bindAlbumData(result)
            if (selectorConfig.isPageStrategy) {
                if (selectorConfig.isPreloadFirst) {
                    mRecycler!!.isEnabledLoadMore = true
                } else {
                    loadFirstPageMediaData(firstFolder.bucketId)
                }
            } else {
                setAdapterData(firstFolder.data)
            }
        } else {
            showDataNull()
        }
    }

    override fun loadFirstPageMediaData(firstBucketId: Long) {
        mPage = 1
        mRecycler!!.isEnabledLoadMore = true
        if (selectorConfig.loaderDataEngine != null) {
            selectorConfig.loaderDataEngine.loadFirstPageMediaData(
                context,
                firstBucketId,
                mPage,
                mPage * selectorConfig.pageSize,
                object : OnQueryDataResultListener<LocalMedia?>() {
                    override fun onComplete(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
                        handleFirstPageMedia(result, isHasMore)
                    }
                })
        } else {
            mLoader.loadPageMediaData(firstBucketId, mPage, mPage * selectorConfig.pageSize,
                object : OnQueryDataResultListener<LocalMedia?>() {
                    override fun onComplete(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
                        handleFirstPageMedia(result, isHasMore)
                    }
                })
        }
    }

    private fun handleFirstPageMedia(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return
        }
        mRecycler!!.isEnabledLoadMore = isHasMore
        if (mRecycler!!.isEnabledLoadMore && result.size == 0) {
            // 如果isHasMore为true但result.size() = 0;
            // 那么有可能是开启了某些条件过滤，实际上是还有更多资源的再强制请求
            onRecyclerViewPreloadMore()
        } else {
            setAdapterData(result)
        }
    }

    override fun loadOnlyInAppDirectoryAllMediaData() {
        if (selectorConfig.loaderDataEngine != null) {
            selectorConfig.loaderDataEngine.loadOnlyInAppDirAllMediaData(
                context,
                object : OnQueryAlbumListener<LocalMediaFolder?> {
                    override fun onComplete(folder: LocalMediaFolder) {
                        handleInAppDirAllMedia(folder)
                    }
                })
        } else {
            mLoader.loadOnlyInAppDirAllMedia(object : OnQueryAlbumListener<LocalMediaFolder?> {
                override fun onComplete(folder: LocalMediaFolder) {
                    handleInAppDirAllMedia(folder)
                }
            })
        }
    }

    private fun handleInAppDirAllMedia(folder: LocalMediaFolder?) {
        if (!ActivityCompatHelper.isDestroy(activity)) {
            val sandboxDir = selectorConfig.sandboxDir
            val isNonNull = folder != null
            val folderName = if (isNonNull) folder!!.folderName else File(sandboxDir).name
            titleBar!!.setTitle(folderName)
            if (isNonNull) {
                selectorConfig.currentLocalMediaFolder = folder
                setAdapterData(folder!!.data)
            } else {
                showDataNull()
            }
        }
    }

    /**
     * 内存不足时，恢复RecyclerView定位位置
     */
    private fun recoveryRecyclerPosition() {
        if (currentPosition > 0) {
            mRecycler!!.post {
                mRecycler!!.scrollToPosition(currentPosition)
                mRecycler!!.lastVisiblePosition = currentPosition
            }
        }
    }

    private fun initRecycler(view: View) {
        mRecycler = view.findViewById(R.id.recycler)
        val selectorStyle = selectorConfig.selectorStyle
        val selectMainStyle = selectorStyle.selectMainStyle
        val listBackgroundColor = selectMainStyle.mainListBackgroundColor
        if (StyleUtils.checkStyleValidity(listBackgroundColor)) {
            mRecycler.setBackgroundColor(listBackgroundColor)
        } else {
            mRecycler.setBackgroundColor(ContextCompat.getColor(appContext, R.color.ps_color_black))
        }
        val imageSpanCount =
            if (selectorConfig.imageSpanCount <= 0) PictureConfig.DEFAULT_SPAN_COUNT else selectorConfig.imageSpanCount
        if (mRecycler.getItemDecorationCount() == 0) {
            if (StyleUtils.checkSizeValidity(selectMainStyle.adapterItemSpacingSize)) {
                mRecycler.addItemDecoration(
                    GridSpacingItemDecoration(
                        imageSpanCount,
                        selectMainStyle.adapterItemSpacingSize,
                        selectMainStyle.isAdapterItemIncludeEdge
                    )
                )
            } else {
                mRecycler.addItemDecoration(
                    GridSpacingItemDecoration(
                        imageSpanCount,
                        DensityUtil.dip2px(view.context, 1f),
                        selectMainStyle.isAdapterItemIncludeEdge
                    )
                )
            }
        }
        mRecycler.setLayoutManager(GridLayoutManager(context, imageSpanCount))
        val itemAnimator = mRecycler.getItemAnimator()
        if (itemAnimator != null) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
            mRecycler.setItemAnimator(null)
        }
        if (selectorConfig.isPageStrategy) {
            mRecycler.setReachBottomRow(RecyclerPreloadView.BOTTOM_PRELOAD)
            mRecycler.setOnRecyclerViewPreloadListener(this)
        } else {
            mRecycler.setHasFixedSize(true)
        }
        mAdapter = PictureImageGridAdapter(context, selectorConfig)
        mAdapter!!.isDisplayCamera = isDisplayCamera
        when (selectorConfig.animationMode) {
            AnimationType.ALPHA_IN_ANIMATION -> mRecycler.setAdapter(
                AlphaInAnimationAdapter(
                    mAdapter
                )
            )
            AnimationType.SLIDE_IN_BOTTOM_ANIMATION -> mRecycler.setAdapter(
                SlideInBottomAnimationAdapter(mAdapter)
            )
            else -> mRecycler.setAdapter(mAdapter)
        }
        addRecyclerAction()
    }

    private fun addRecyclerAction() {
        mAdapter!!.setOnItemClickListener(object : PictureImageGridAdapter.OnItemClickListener {
            override fun openCameraClick() {
                if (DoubleUtils.isFastDoubleClick()) {
                    return
                }
                openSelectedCamera()
            }

            override fun onSelected(selectedView: View, position: Int, media: LocalMedia): Int {
                val selectResultCode = confirmSelect(media, selectedView.isSelected)
                if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                    if (selectorConfig.onSelectAnimListener != null) {
                        val duration =
                            selectorConfig.onSelectAnimListener.onSelectAnim(selectedView)
                        if (duration > 0) {
                            SELECT_ANIM_DURATION = duration.toInt()
                        }
                    } else {
                        val animation = AnimationUtils.loadAnimation(
                            context, R.anim.ps_anim_modal_in
                        )
                        SELECT_ANIM_DURATION = animation.duration.toInt()
                        selectedView.startAnimation(animation)
                    }
                }
                return selectResultCode
            }

            override fun onItemClick(selectedView: View, position: Int, media: LocalMedia) {
                if (selectorConfig.selectionMode == SelectModeConfig.SINGLE && selectorConfig.isDirectReturnSingle) {
                    selectorConfig.selectedResult.clear()
                    val selectResultCode = confirmSelect(media, false)
                    if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                        dispatchTransformResult()
                    }
                } else {
                    if (DoubleUtils.isFastDoubleClick()) {
                        return
                    }
                    onStartPreview(position, false)
                }
            }

            override fun onItemLongClick(itemView: View, position: Int) {
                if (mDragSelectTouchListener != null && selectorConfig.isFastSlidingSelect) {
                    val vibrator = activity!!.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(50)
                    mDragSelectTouchListener!!.startSlideSelection(position)
                }
            }
        })
        mRecycler!!.setOnRecyclerViewScrollStateListener(object :
            OnRecyclerViewScrollStateListener {
            override fun onScrollFast() {
                if (selectorConfig.imageEngine != null) {
                    selectorConfig.imageEngine.pauseRequests(context)
                }
            }

            override fun onScrollSlow() {
                if (selectorConfig.imageEngine != null) {
                    selectorConfig.imageEngine.resumeRequests(context)
                }
            }
        })
        mRecycler!!.setOnRecyclerViewScrollListener(object : OnRecyclerViewScrollListener {
            override fun onScrolled(dx: Int, dy: Int) {
                setCurrentMediaCreateTimeText()
            }

            override fun onScrollStateChanged(state: Int) {
                if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
                    showCurrentMediaCreateTimeUI()
                } else if (state == RecyclerView.SCROLL_STATE_IDLE) {
                    hideCurrentMediaCreateTimeUI()
                }
            }
        })
        if (selectorConfig.isFastSlidingSelect) {
            val selectedPosition = HashSet<Int>()
            val slideSelectionHandler =
                SlideSelectionHandler(object : SlideSelectionHandler.ISelectionHandler {
                    override fun getSelection(): HashSet<Int> {
                        for (i in 0 until selectorConfig.selectCount) {
                            val media = selectorConfig.selectedResult[i]
                            selectedPosition.add(media.position)
                        }
                        return selectedPosition
                    }

                    override fun changeSelection(
                        start: Int,
                        end: Int,
                        isSelected: Boolean,
                        calledFromOnStart: Boolean
                    ) {
                        val adapterData = mAdapter!!.data
                        if (adapterData.size == 0 || start > adapterData.size) {
                            return
                        }
                        val media = adapterData[start]
                        val selectResultCode =
                            confirmSelect(media, selectorConfig.selectedResult.contains(media))
                        mDragSelectTouchListener!!.setActive(selectResultCode != SelectedManager.INVALID)
                    }
                })
            mDragSelectTouchListener = SlideSelectTouchListener()
                .setRecyclerViewHeaderCount(if (mAdapter!!.isDisplayCamera) 1 else 0)
                .withSelectListener(slideSelectionHandler)
            mRecycler!!.addOnItemTouchListener(mDragSelectTouchListener)
        }
    }

    /**
     * 显示当前资源时间轴
     */
    private fun setCurrentMediaCreateTimeText() {
        if (selectorConfig.isDisplayTimeAxis) {
            val position = mRecycler!!.firstVisiblePosition
            if (position != RecyclerView.NO_POSITION) {
                val data = mAdapter!!.data
                if (data.size > position && data[position].dateAddedTime > 0) {
                    tvCurrentDataTime!!.text = DateUtils.getDataFormat(
                        context,
                        data[position].dateAddedTime
                    )
                }
            }
        }
    }

    /**
     * 显示当前资源时间轴
     */
    private fun showCurrentMediaCreateTimeUI() {
        if (selectorConfig.isDisplayTimeAxis && mAdapter!!.data.size > 0) {
            if (tvCurrentDataTime!!.alpha == 0f) {
                tvCurrentDataTime!!.animate().setDuration(150).alphaBy(1.0f).start()
            }
        }
    }

    /**
     * 隐藏当前资源时间轴
     */
    private fun hideCurrentMediaCreateTimeUI() {
        if (selectorConfig.isDisplayTimeAxis && mAdapter!!.data.size > 0) {
            tvCurrentDataTime!!.animate().setDuration(250).alpha(0.0f).start()
        }
    }

    /**
     * 预览图片
     *
     * @param position        预览图片下标
     * @param isBottomPreview true 底部预览模式 false列表预览模式
     */
    private fun onStartPreview(position: Int, isBottomPreview: Boolean) {
        if (ActivityCompatHelper.checkFragmentNonExits(
                activity,
                PictureSelectorPreviewFragment.Companion.TAG
            )
        ) {
            val data: ArrayList<LocalMedia>
            val totalNum: Int
            var currentBucketId: Long = 0
            if (isBottomPreview) {
                data = ArrayList(selectorConfig.selectedResult)
                totalNum = data.size
            } else {
                data = ArrayList(mAdapter!!.data)
                val currentLocalMediaFolder = selectorConfig.currentLocalMediaFolder
                if (currentLocalMediaFolder != null) {
                    totalNum = currentLocalMediaFolder.folderTotalNum
                    currentBucketId = currentLocalMediaFolder.bucketId
                } else {
                    totalNum = data.size
                    currentBucketId =
                        if (data.size > 0) data[0].bucketId else PictureConfig.ALL.toLong()
                }
            }
            if (!isBottomPreview && selectorConfig.isPreviewZoomEffect) {
                BuildRecycleItemViewParams.generateViewParams(
                    mRecycler,
                    if (selectorConfig.isPreviewFullScreenMode) 0 else DensityUtil.getStatusBarHeight(
                        context
                    )
                )
            }
            if (selectorConfig.onPreviewInterceptListener != null) {
                selectorConfig.onPreviewInterceptListener
                    .onPreview(
                        context, position, totalNum, mPage, currentBucketId, titleBar!!.titleText,
                        mAdapter!!.isDisplayCamera, data, isBottomPreview
                    )
            } else {
                if (ActivityCompatHelper.checkFragmentNonExits(
                        activity,
                        PictureSelectorPreviewFragment.Companion.TAG
                    )
                ) {
                    val previewFragment: PictureSelectorPreviewFragment =
                        PictureSelectorPreviewFragment.Companion.newInstance()
                    previewFragment.setInternalPreviewData(
                        isBottomPreview, titleBar!!.titleText, mAdapter!!.isDisplayCamera,
                        position, totalNum, mPage, currentBucketId, data
                    )
                    FragmentInjectManager.injectFragment(
                        activity,
                        PictureSelectorPreviewFragment.Companion.TAG,
                        previewFragment
                    )
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapterData(result: ArrayList<LocalMedia>) {
        // 这个地方有个时间差，主要是解决进场动画和查询数据同时进行导致动画有点卡顿问题，
        // 主要是针对添加PictureSelectorFragment方式下
        val enterAnimationDuration = enterAnimationDuration
        if (enterAnimationDuration > 0) {
            requireView().postDelayed({ setAdapterDataComplete(result) }, enterAnimationDuration)
        } else {
            setAdapterDataComplete(result)
        }
    }

    private fun setAdapterDataComplete(result: ArrayList<LocalMedia>) {
        enterAnimationDuration = 0
        sendChangeSubSelectPositionEvent(false)
        mAdapter!!.setDataAndDataSetChanged(result)
        selectorConfig.dataSource.clear()
        selectorConfig.albumDataSource.clear()
        recoveryRecyclerPosition()
        if (mAdapter!!.isDataEmpty) {
            showDataNull()
        } else {
            hideDataNull()
        }
    }

    override fun onRecyclerViewPreloadMore() {
        if (isMemoryRecycling) {
            // 这里延迟是拍照导致的页面被回收，Fragment的重创会快于相机的onActivityResult的
            requireView().postDelayed({ loadMoreMediaData() }, 350)
        } else {
            loadMoreMediaData()
        }
    }

    /**
     * 加载更多
     */
    override fun loadMoreMediaData() {
        if (mRecycler!!.isEnabledLoadMore) {
            mPage++
            val localMediaFolder = selectorConfig.currentLocalMediaFolder
            val bucketId = localMediaFolder?.bucketId ?: 0
            if (selectorConfig.loaderDataEngine != null) {
                selectorConfig.loaderDataEngine.loadMoreMediaData(
                    context,
                    bucketId,
                    mPage,
                    selectorConfig.pageSize,
                    selectorConfig.pageSize,
                    object : OnQueryDataResultListener<LocalMedia?>() {
                        override fun onComplete(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
                            handleMoreMediaData(result, isHasMore)
                        }
                    })
            } else {
                mLoader.loadPageMediaData(bucketId, mPage, selectorConfig.pageSize,
                    object : OnQueryDataResultListener<LocalMedia?>() {
                        override fun onComplete(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
                            handleMoreMediaData(result, isHasMore)
                        }
                    })
            }
        }
    }

    private fun handleMoreMediaData(result: MutableList<LocalMedia>, isHasMore: Boolean) {
        if (ActivityCompatHelper.isDestroy(activity)) {
            return
        }
        mRecycler!!.isEnabledLoadMore = isHasMore
        if (mRecycler!!.isEnabledLoadMore) {
            removePageCameraRepeatData(result)
            if (result.size > 0) {
                val positionStart = mAdapter!!.data.size
                mAdapter!!.data.addAll(result)
                mAdapter!!.notifyItemRangeChanged(positionStart, mAdapter!!.itemCount)
                hideDataNull()
            } else {
                // 如果没数据这里在强制调用一下上拉加载更多，防止是因为某些条件过滤导致的假为0的情况
                onRecyclerViewPreloadMore()
            }
            if (result.size < PictureConfig.MIN_PAGE_SIZE) {
                // 当数据量过少时强制触发一下上拉加载更多，防止没有自动触发加载更多
                mRecycler!!.onScrolled(mRecycler!!.scrollX, mRecycler!!.scrollY)
            }
        }
    }

    private fun removePageCameraRepeatData(result: MutableList<LocalMedia>) {
        try {
            if (selectorConfig.isPageStrategy && isCameraCallback) {
                synchronized(LOCK) {
                    val iterator = result.iterator()
                    while (iterator.hasNext()) {
                        if (mAdapter!!.data.contains(iterator.next())) {
                            iterator.remove()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isCameraCallback = false
        }
    }

    override fun dispatchCameraMediaResult(media: LocalMedia) {
        val exitsTotalNum = albumListPopWindow!!.firstAlbumImageCount
        if (!isAddSameImp(exitsTotalNum)) {
            mAdapter!!.data.add(0, media)
            isCameraCallback = true
        }
        if (selectorConfig.selectionMode == SelectModeConfig.SINGLE && selectorConfig.isDirectReturnSingle) {
            selectorConfig.selectedResult.clear()
            val selectResultCode = confirmSelect(media, false)
            if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                dispatchTransformResult()
            }
        } else {
            confirmSelect(media, false)
        }
        mAdapter!!.notifyItemInserted(if (selectorConfig.isDisplayCamera) 1 else 0)
        mAdapter!!.notifyItemRangeChanged(
            if (selectorConfig.isDisplayCamera) 1 else 0,
            mAdapter!!.data.size
        )
        if (selectorConfig.isOnlySandboxDir) {
            var currentLocalMediaFolder = selectorConfig.currentLocalMediaFolder
            if (currentLocalMediaFolder == null) {
                currentLocalMediaFolder = LocalMediaFolder()
            }
            currentLocalMediaFolder.bucketId = ValueOf.toLong(media.parentFolderName.hashCode())
            currentLocalMediaFolder.folderName = media.parentFolderName
            currentLocalMediaFolder.firstMimeType = media.mimeType
            currentLocalMediaFolder.firstImagePath = media.path
            currentLocalMediaFolder.folderTotalNum = mAdapter!!.data.size
            currentLocalMediaFolder.currentDataPage = mPage
            currentLocalMediaFolder.isHasMore = false
            currentLocalMediaFolder.data = mAdapter!!.data
            mRecycler!!.isEnabledLoadMore = false
            selectorConfig.currentLocalMediaFolder = currentLocalMediaFolder
        } else {
            mergeFolder(media)
        }
        allFolderSize = 0
        if (mAdapter!!.data.size > 0 || selectorConfig.isDirectReturnSingle) {
            hideDataNull()
        } else {
            showDataNull()
        }
    }

    /**
     * 拍照出来的合并到相应的专辑目录中去
     *
     * @param media
     */
    private fun mergeFolder(media: LocalMedia) {
        val allFolder: LocalMediaFolder
        val albumList = albumListPopWindow!!.albumList
        if (albumListPopWindow!!.folderCount == 0) {
            // 1、没有相册时需要手动创建相机胶卷
            allFolder = LocalMediaFolder()
            val folderName: String
            folderName = if (TextUtils.isEmpty(selectorConfig.defaultAlbumName)) {
                if (selectorConfig.chooseMode == SelectMimeType.ofAudio()) getString(
                    R.string.ps_all_audio
                ) else getString(R.string.ps_camera_roll)
            } else {
                selectorConfig.defaultAlbumName
            }
            allFolder.folderName = folderName
            allFolder.firstImagePath = ""
            allFolder.bucketId = PictureConfig.ALL.toLong()
            albumList.add(0, allFolder)
        } else {
            // 2、有相册就找到对应的相册把数据加进去
            allFolder = albumListPopWindow!!.getFolder(0)
        }
        allFolder.firstImagePath = media.path
        allFolder.firstMimeType = media.mimeType
        allFolder.data = mAdapter!!.data
        allFolder.bucketId = PictureConfig.ALL.toLong()
        allFolder.folderTotalNum =
            if (isAddSameImp(allFolder.folderTotalNum)) allFolder.folderTotalNum else allFolder.folderTotalNum + 1
        val currentLocalMediaFolder = selectorConfig.currentLocalMediaFolder
        if (currentLocalMediaFolder == null || currentLocalMediaFolder.folderTotalNum == 0) {
            selectorConfig.currentLocalMediaFolder = allFolder
        }
        // 先查找Camera目录，没有找到则创建一个Camera目录
        var cameraFolder: LocalMediaFolder? = null
        for (i in albumList.indices) {
            val exitsFolder = albumList[i]
            if (TextUtils.equals(exitsFolder.folderName, media.parentFolderName)) {
                cameraFolder = exitsFolder
                break
            }
        }
        if (cameraFolder == null) {
            // 还没有这个目录，创建一个
            cameraFolder = LocalMediaFolder()
            albumList.add(cameraFolder)
        }
        cameraFolder.folderName = media.parentFolderName
        if (cameraFolder.bucketId == -1L || cameraFolder.bucketId == 0L) {
            cameraFolder.bucketId = media.bucketId
        }
        // 分页模式下，切换到Camera目录下时，会直接从MediaStore拉取
        if (selectorConfig.isPageStrategy) {
            cameraFolder.isHasMore = true
        } else {
            // 非分页模式数据都是存在目录的data下，所以直接添加进去就行
            if (!isAddSameImp(allFolder.folderTotalNum)
                || !TextUtils.isEmpty(selectorConfig.outPutCameraDir)
                || !TextUtils.isEmpty(selectorConfig.outPutAudioDir)
            ) {
                cameraFolder.data.add(0, media)
            }
        }
        cameraFolder.folderTotalNum =
            if (isAddSameImp(allFolder.folderTotalNum)) cameraFolder.folderTotalNum else cameraFolder.folderTotalNum + 1
        cameraFolder.firstImagePath = selectorConfig.cameraPath
        cameraFolder.firstMimeType = media.mimeType
        albumListPopWindow!!.bindAlbumData(albumList)
    }

    /**
     * 数量是否一致
     */
    private fun isAddSameImp(totalNum: Int): Boolean {
        return if (totalNum == 0) {
            false
        } else allFolderSize > 0 && allFolderSize < totalNum
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mDragSelectTouchListener != null) {
            mDragSelectTouchListener!!.stopAutoScroll()
        }
    }

    /**
     * 显示数据为空提示
     */
    private fun showDataNull() {
        if (selectorConfig.currentLocalMediaFolder == null
            || selectorConfig.currentLocalMediaFolder.bucketId == PictureConfig.ALL.toLong()
        ) {
            if (tvDataEmpty!!.visibility == View.GONE) {
                tvDataEmpty!!.visibility = View.VISIBLE
            }
            tvDataEmpty!!.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                R.drawable.ps_ic_no_data,
                0,
                0
            )
            val tips =
                if (selectorConfig.chooseMode == SelectMimeType.ofAudio()) getString(R.string.ps_audio_empty) else getString(
                    R.string.ps_empty
                )
            tvDataEmpty!!.text = tips
        }
    }

    /**
     * 隐藏数据为空提示
     */
    private fun hideDataNull() {
        if (tvDataEmpty!!.visibility == View.VISIBLE) {
            tvDataEmpty!!.visibility = View.GONE
        }
    }

    companion object {
        @JvmField
        val TAG = PictureSelectorFragment::class.java.simpleName
        private val LOCK = Any()

        /**
         * 这个时间对应的是R.anim.ps_anim_modal_in里面的
         */
        private var SELECT_ANIM_DURATION = 135
        @JvmStatic
        fun newInstance(): PictureSelectorFragment {
            val fragment = PictureSelectorFragment()
            fragment.arguments = Bundle()
            return fragment
        }
    }
}