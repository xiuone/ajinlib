package com.luck.picture.lib

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.luck.picture.lib.basic.PictureCommonFragment
import com.luck.picture.lib.config.PermissionEvent
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnRequestPermissionListener
import com.luck.picture.lib.manager.SelectedManager
import com.luck.picture.lib.utils.SdkVersionUtils
import com.luck.picture.lib.utils.ToastUtils
import java.util.ArrayList

/**
 * @author：luck
 * @date：2022/1/16 10:22 下午
 * @describe：PictureSelectorSystemFragment
 */
class PictureSelectorSystemFragment constructor() : PictureCommonFragment() {
    public override fun getFragmentTag(): String {
        return TAG
    }

    public override fun getResourceId(): Int {
        return R.layout.ps_empty
    }

    private var mDocMultipleLauncher: ActivityResultLauncher<String>? = null
    private var mDocSingleLauncher: ActivityResultLauncher<String>? = null
    private var mContentsLauncher: ActivityResultLauncher<String>? = null
    private var mContentLauncher: ActivityResultLauncher<String>? = null
    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createSystemContracts()
        if (PermissionChecker.isCheckReadStorage(selectorConfig.chooseMode, getContext())) {
            openSystemAlbum()
        } else {
            val readPermissionArray: Array<String> =
                PermissionConfig.getReadPermissionArray(appContext, selectorConfig.chooseMode)
            onPermissionExplainEvent(true, readPermissionArray)
            if (selectorConfig.onPermissionsEventListener != null) {
                onApplyPermissionsEvent(
                    PermissionEvent.EVENT_SYSTEM_SOURCE_DATA,
                    readPermissionArray
                )
            } else {
                PermissionChecker.getInstance().requestPermissions(
                    this,
                    readPermissionArray,
                    object : PermissionResultCallback() {
                        fun onGranted() {
                            openSystemAlbum()
                        }

                        fun onDenied() {
                            handlePermissionDenied(readPermissionArray)
                        }
                    })
            }
        }
    }

    public override fun onApplyPermissionsEvent(event: Int, permissionArray: Array<String>) {
        if (event == PermissionEvent.EVENT_SYSTEM_SOURCE_DATA) {
            selectorConfig.onPermissionsEventListener.requestPermission(this,
                PermissionConfig.getReadPermissionArray(appContext, selectorConfig.chooseMode),
                object : OnRequestPermissionListener {
                    public override fun onCall(permissionArray: Array<String>, isResult: Boolean) {
                        if (isResult) {
                            openSystemAlbum()
                        } else {
                            handlePermissionDenied(permissionArray)
                        }
                    }
                })
        }
    }

    /**
     * 打开系统相册
     */
    private fun openSystemAlbum() {
        onPermissionExplainEvent(false, null)
        if (selectorConfig.selectionMode == SelectModeConfig.SINGLE) {
            if (selectorConfig.chooseMode == SelectMimeType.ofAll()) {
                mDocSingleLauncher!!.launch(SelectMimeType.SYSTEM_ALL)
            } else {
                mContentLauncher!!.launch(input)
            }
        } else {
            if (selectorConfig.chooseMode == SelectMimeType.ofAll()) {
                mDocMultipleLauncher!!.launch(SelectMimeType.SYSTEM_ALL)
            } else {
                mContentsLauncher!!.launch(input)
            }
        }
    }

    /**
     * createSystemContracts
     */
    private fun createSystemContracts() {
        if (selectorConfig.selectionMode == SelectModeConfig.SINGLE) {
            if (selectorConfig.chooseMode == SelectMimeType.ofAll()) {
                createSingleDocuments()
            } else {
                createContent()
            }
        } else {
            if (selectorConfig.chooseMode == SelectMimeType.ofAll()) {
                createMultipleDocuments()
            } else {
                createMultipleContents()
            }
        }
    }

    /**
     * 同时获取图片或视频(多选)
     *
     * 部分机型可能不支持多选操作
     */
    private fun createMultipleDocuments() {
        mDocMultipleLauncher = registerForActivityResult<String, List<Uri>>(object :
            ActivityResultContract<String?, List<Uri?>>() {
            public override fun parseResult(resultCode: Int, intent: Intent?): List<Uri?> {
                val result: MutableList<Uri?> = ArrayList()
                if (intent == null) {
                    return result
                }
                if (intent.getClipData() != null) {
                    val clipData: ClipData? = intent.getClipData()
                    val itemCount: Int = clipData!!.getItemCount()
                    for (i in 0 until itemCount) {
                        val item: ClipData.Item = clipData.getItemAt(i)
                        val uri: Uri = item.getUri()
                        result.add(uri)
                    }
                } else if (intent.getData() != null) {
                    result.add(intent.getData())
                }
                return result
            }

            public override fun createIntent(context: Context, mimeTypes: String?): Intent {
                val intent: Intent = Intent(Intent.ACTION_PICK)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.setType(mimeTypes)
                return intent
            }
        }, object : ActivityResultCallback<List<Uri>?> {
            public override fun onActivityResult(result: List<Uri>?) {
                if (result == null || result.size == 0) {
                    onKeyBackFragmentFinish()
                } else {
                    for (i in result.indices) {
                        val media: LocalMedia = buildLocalMedia(result.get(i).toString())
                        media.setPath(if (SdkVersionUtils.isQ()) media.getPath() else media.getRealPath())
                        selectorConfig.addSelectResult(media)
                    }
                    dispatchTransformResult()
                }
            }
        })
    }

    /**
     * 同时获取图片或视频(单选)
     */
    private fun createSingleDocuments() {
        mDocSingleLauncher =
            registerForActivityResult(object : ActivityResultContract<String?, Uri?>() {
                public override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
                    if (intent == null) {
                        return null
                    }
                    return intent.getData()
                }

                public override fun createIntent(context: Context, mimeTypes: String?): Intent {
                    val intent: Intent = Intent(Intent.ACTION_PICK)
                    intent.setType(mimeTypes)
                    return intent
                }
            }, object : ActivityResultCallback<Uri?> {
                public override fun onActivityResult(result: Uri?) {
                    if (result == null) {
                        onKeyBackFragmentFinish()
                    } else {
                        val media: LocalMedia = buildLocalMedia(result.toString())
                        media.setPath(if (SdkVersionUtils.isQ()) media.getPath() else media.getRealPath())
                        val selectResultCode: Int = confirmSelect(media, false)
                        if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                            dispatchTransformResult()
                        } else {
                            onKeyBackFragmentFinish()
                        }
                    }
                }
            })
    }

    /**
     * 获取图片或视频
     *
     * 部分机型可能不支持多选操作
     */
    private fun createMultipleContents() {
        mContentsLauncher = registerForActivityResult<String, List<Uri>>(object :
            ActivityResultContract<String?, List<Uri?>>() {
            public override fun parseResult(resultCode: Int, intent: Intent?): List<Uri?> {
                val result: MutableList<Uri?> = ArrayList()
                if (intent == null) {
                    return result
                }
                if (intent.getClipData() != null) {
                    val clipData: ClipData? = intent.getClipData()
                    val itemCount: Int = clipData!!.getItemCount()
                    for (i in 0 until itemCount) {
                        val item: ClipData.Item = clipData.getItemAt(i)
                        val uri: Uri = item.getUri()
                        result.add(uri)
                    }
                } else if (intent.getData() != null) {
                    result.add(intent.getData())
                }
                return result
            }

            public override fun createIntent(context: Context, mimeType: String?): Intent {
                val intent: Intent
                if (TextUtils.equals(SelectMimeType.SYSTEM_VIDEO, mimeType)) {
                    intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                } else if (TextUtils.equals(SelectMimeType.SYSTEM_AUDIO, mimeType)) {
                    intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                } else {
                    intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                }
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                return intent
            }
        }, object : ActivityResultCallback<List<Uri>?> {
            public override fun onActivityResult(result: List<Uri>?) {
                if (result == null || result.size == 0) {
                    onKeyBackFragmentFinish()
                } else {
                    for (i in result.indices) {
                        val media: LocalMedia = buildLocalMedia(result.get(i).toString())
                        media.setPath(if (SdkVersionUtils.isQ()) media.getPath() else media.getRealPath())
                        selectorConfig.addSelectResult(media)
                    }
                    dispatchTransformResult()
                }
            }
        })
    }

    /**
     * 单选图片或视频
     */
    private fun createContent() {
        mContentLauncher =
            registerForActivityResult(object : ActivityResultContract<String?, Uri?>() {
                public override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
                    if (intent == null) {
                        return null
                    }
                    return intent.getData()
                }

                public override fun createIntent(context: Context, mimeType: String?): Intent {
                    val intent: Intent
                    if (TextUtils.equals(SelectMimeType.SYSTEM_VIDEO, mimeType)) {
                        intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    } else if (TextUtils.equals(SelectMimeType.SYSTEM_AUDIO, mimeType)) {
                        intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                    } else {
                        intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    }
                    return intent
                }
            }, object : ActivityResultCallback<Uri?> {
                public override fun onActivityResult(result: Uri?) {
                    if (result == null) {
                        onKeyBackFragmentFinish()
                    } else {
                        val media: LocalMedia = buildLocalMedia(result.toString())
                        media.setPath(if (SdkVersionUtils.isQ()) media.getPath() else media.getRealPath())
                        val selectResultCode: Int = confirmSelect(media, false)
                        if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                            dispatchTransformResult()
                        } else {
                            onKeyBackFragmentFinish()
                        }
                    }
                }
            })
    }

    /**
     * 获取选资源取类型
     *
     * @return
     */
    private val input: String
        private get() {
            if (selectorConfig.chooseMode == SelectMimeType.ofVideo()) {
                return SelectMimeType.SYSTEM_VIDEO
            } else if (selectorConfig.chooseMode == SelectMimeType.ofAudio()) {
                return SelectMimeType.SYSTEM_AUDIO
            } else {
                return SelectMimeType.SYSTEM_IMAGE
            }
        }

    public override fun handlePermissionSettingResult(permissions: Array<String>) {
        onPermissionExplainEvent(false, null)
        val isCheckReadStorage: Boolean
        if (selectorConfig.onPermissionsEventListener != null) {
            isCheckReadStorage = selectorConfig.onPermissionsEventListener
                .hasPermissions(this, permissions)
        } else {
            isCheckReadStorage =
                PermissionChecker.isCheckReadStorage(selectorConfig.chooseMode, getContext())
        }
        if (isCheckReadStorage) {
            openSystemAlbum()
        } else {
            ToastUtils.showToast(getContext(), getString(R.string.ps_jurisdiction))
            onKeyBackFragmentFinish()
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = arrayOf<String>()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            onKeyBackFragmentFinish()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mDocMultipleLauncher != null) {
            mDocMultipleLauncher!!.unregister()
        }
        if (mDocSingleLauncher != null) {
            mDocSingleLauncher!!.unregister()
        }
        if (mContentsLauncher != null) {
            mContentsLauncher!!.unregister()
        }
        if (mContentLauncher != null) {
            mContentLauncher!!.unregister()
        }
    }

    companion object {
        @JvmField
        val TAG: String = PictureSelectorSystemFragment::class.java.getSimpleName()
        @JvmStatic
        fun newInstance(): PictureSelectorSystemFragment {
            return PictureSelectorSystemFragment()
        }
    }
}