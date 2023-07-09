package com.xy.base.permission.impl

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.Permission
import com.xy.base.permission.PermissionIntentManager.getApplicationDetailsIntent
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.permission.PermissionUtils.checkOpNoThrow
import com.xy.base.permission.PermissionUtils.checkSelfPermission
import com.xy.base.permission.PermissionUtils.getPackageNameUri
import com.xy.base.permission.PermissionUtils.shouldShowRequestPermissionRationale
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 8.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_8)
internal open class PermissionDelegateImplV26 : PermissionDelegateImplV23() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (TextUtils.equals(permission, Permission.REQUEST_INSTALL_PACKAGES)) isGrantedInstallPermission(context)
        if (TextUtils.equals(permission, Permission.PICTURE_IN_PICTURE)) isGrantedPictureInPicturePermission(context)
        return if (TextUtils.equals(permission, Permission.READ_PHONE_NUMBERS) ||
            TextUtils.equals(permission, Permission.ANSWER_PHONE_CALLS)) {
            checkSelfPermission(context, permission)
        } else super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean {
        if (TextUtils.equals(permission, Permission.REQUEST_INSTALL_PACKAGES)) return false
        if (TextUtils.equals(permission, Permission.PICTURE_IN_PICTURE)) return false
        return if (TextUtils.equals(permission, Permission.READ_PHONE_NUMBERS) ||
            TextUtils.equals(permission, Permission.ANSWER_PHONE_CALLS)) {
            !checkSelfPermission(activity, permission) && !shouldShowRequestPermissionRationale(activity, permission)
        } else super.isPermissionPermanentDenied(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        if (TextUtils.equals(permission, Permission.REQUEST_INSTALL_PACKAGES)) return getInstallPermissionIntent(context)
        return if (TextUtils.equals(permission, Permission.PICTURE_IN_PICTURE)) {
            getPictureInPicturePermissionIntent(context)
        } else super.getPermissionIntent(context, permission)
    }
    /**
     * 是否有安装权限
     */
    private fun isGrantedInstallPermission(context: Context): Boolean =  context.packageManager.canRequestPackageInstalls()

    /**
     * 获取安装权限设置界面意图
     */
    private fun getInstallPermissionIntent(context: Context): Intent? {
        var intent: Intent? = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent!!.data = getPackageNameUri(context)
        if (!areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }

    /**
     * 是否有画中画权限
     */
    private fun isGrantedPictureInPicturePermission(context: Context): Boolean = checkOpNoThrow(context, AppOpsManager.OPSTR_PICTURE_IN_PICTURE)

    /**
     * 获取画中画权限设置界面意图
     */
    private fun getPictureInPicturePermissionIntent(context: Context): Intent? {
        // android.provider.Settings.ACTION_PICTURE_IN_PICTURE_SETTINGS
        var intent: Intent? = Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS")
        intent!!.data = getPackageNameUri(context)
        if (!areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }
}