package com.xy.base.permission.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.Permission
import com.xy.base.permission.PermissionIntentManager
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.permission.PermissionUtils.getPackageNameUri
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 11 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_11)
internal open class PermissionDelegateImplV30 : PermissionDelegateImplV29() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        return if (TextUtils.equals(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            isGrantedManageStoragePermission()
        } else super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean {
        return if (TextUtils.equals(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            false
        } else super.isPermissionPermanentDenied(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        return if (TextUtils.equals(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
            getManageStoragePermissionIntent(context)
        } else super.getPermissionIntent(context, permission)
    }

    /**
     * 是否有所有文件的管理权限
     */
    private fun isGrantedManageStoragePermission(): Boolean =  Environment.isExternalStorageManager()

    /**
     * 获取所有文件的管理权限设置界面意图
     */
    private fun getManageStoragePermissionIntent(context: Context): Intent? {
        var intent :Intent?= Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent?.data = getPackageNameUri(context)
        if (!areActivityIntent(context, intent)) intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        if (!areActivityIntent(context, intent)) intent = PermissionIntentManager.getApplicationDetailsIntent(context)
        return intent
    }
}