package com.luck.picture.lib

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.luck.picture.lib.basic.PictureCommonFragment
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.manager.SelectedManager
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.utils.SdkVersionUtils
import com.luck.picture.lib.utils.ToastUtils

/**
 * @author：luck
 * @date：2021/11/22 2:26 下午
 * @describe：PictureOnlyCameraFragment
 */
class PictureOnlyCameraFragment : PictureCommonFragment() {
    override fun getFragmentTag(): String {
        return TAG
    }

    override fun getResourceId(): Int {
        return R.layout.ps_empty
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 这里只有非内存回收状态下才走，否则当内存不足Fragment被回收后会重复执行
        if (savedInstanceState == null) {
            if (SdkVersionUtils.isQ()) {
                openSelectedCamera()
            } else {
                val writePermissionArray = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                PermissionChecker.getInstance().requestPermissions(
                    this,
                    writePermissionArray,
                    object : PermissionResultCallback() {
                        fun onGranted() {
                            openSelectedCamera()
                        }

                        fun onDenied() {
                            handlePermissionDenied(writePermissionArray)
                        }
                    })
            }
        }
    }

    override fun dispatchCameraMediaResult(media: LocalMedia) {
        val selectResultCode = confirmSelect(media, false)
        if (selectResultCode == SelectedManager.ADD_SUCCESS) {
            dispatchTransformResult()
        } else {
            onKeyBackFragmentFinish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            onKeyBackFragmentFinish()
        }
    }

    override fun handlePermissionSettingResult(permissions: Array<String>) {
        onPermissionExplainEvent(false, null)
        var isHasPermissions: Boolean
        if (selectorConfig.onPermissionsEventListener != null) {
            isHasPermissions = selectorConfig.onPermissionsEventListener
                .hasPermissions(this, permissions)
        } else {
            isHasPermissions = PermissionChecker.isCheckCamera(context)
            if (SdkVersionUtils.isQ()) {
            } else {
                isHasPermissions = PermissionChecker.isCheckWriteExternalStorage(context)
            }
        }
        if (isHasPermissions) {
            openSelectedCamera()
        } else {
            if (!PermissionChecker.isCheckCamera(context)) {
                ToastUtils.showToast(context, getString(R.string.ps_camera))
            } else {
                if (!PermissionChecker.isCheckWriteExternalStorage(context)) {
                    ToastUtils.showToast(context, getString(R.string.ps_jurisdiction))
                }
            }
            onKeyBackFragmentFinish()
        }
        PermissionConfig.CURRENT_REQUEST_PERMISSION = arrayOf<String>()
    }

    companion object {
        @JvmField
        val TAG = PictureOnlyCameraFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(): PictureOnlyCameraFragment {
            return PictureOnlyCameraFragment()
        }
    }
}