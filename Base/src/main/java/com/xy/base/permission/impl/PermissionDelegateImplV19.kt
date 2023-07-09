package com.xy.base.permission.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.GetInstalledAppsPermissionCompat
import com.xy.base.permission.GetInstalledAppsPermissionCompat.isPermissionPermanentDenied
import com.xy.base.permission.NotificationPermissionCompat
import com.xy.base.permission.Permission
import com.xy.base.permission.WindowPermissionCompat
import com.xy.base.utils.AndroidVersion.isAndroid13

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/03/11
 * desc   : Android 4.4 权限委托实现
 */
open class PermissionDelegateImplV19 : PermissionDelegateImplV18() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        // 检测悬浮窗权限
        if (TextUtils.equals(permission, Permission.SYSTEM_ALERT_WINDOW)) return WindowPermissionCompat.isGrantedPermission(context)
        // 检查读取应用列表权限
        if (TextUtils.equals(permission, Permission.GET_INSTALLED_APPS)) return GetInstalledAppsPermissionCompat.isGrantedPermission(context)

        // 检测通知栏权限
        if (TextUtils.equals(permission, Permission.NOTIFICATION_SERVICE)) return NotificationPermissionCompat.isGrantedPermission(context)
        // 向下兼容 Android 13 新权限
        if (!isAndroid13() && TextUtils.equals(permission, Permission.POST_NOTIFICATIONS)) return NotificationPermissionCompat.isGrantedPermission(context)
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean {
        if (TextUtils.equals(permission, Permission.SYSTEM_ALERT_WINDOW)) return false
        if (TextUtils.equals(permission, Permission.GET_INSTALLED_APPS)) return isPermissionPermanentDenied(activity)
        if (TextUtils.equals(permission, Permission.NOTIFICATION_SERVICE)) return false
        // 向下兼容 Android 13 新权限
        if (!isAndroid13() && TextUtils.equals(permission, Permission.POST_NOTIFICATIONS)) return false
        return super.isPermissionPermanentDenied(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        if (TextUtils.equals(permission, Permission.SYSTEM_ALERT_WINDOW)) return WindowPermissionCompat.getPermissionIntent(context)
        if (TextUtils.equals(permission, Permission.GET_INSTALLED_APPS)) return GetInstalledAppsPermissionCompat.getPermissionIntent(context)
        if (TextUtils.equals(permission, Permission.NOTIFICATION_SERVICE)) return NotificationPermissionCompat.getPermissionIntent(context)
        // 向下兼容 Android 13 新权限
        if (!isAndroid13() && TextUtils.equals(permission, Permission.POST_NOTIFICATIONS)) return NotificationPermissionCompat.getPermissionIntent(context)
        return super.getPermissionIntent(context, permission)
    }
}