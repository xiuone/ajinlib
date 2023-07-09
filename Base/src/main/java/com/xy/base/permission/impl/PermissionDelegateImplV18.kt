package com.xy.base.permission.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.NotificationListenerPermissionCompat.getPermissionIntent
import com.xy.base.permission.NotificationListenerPermissionCompat.isGrantedPermission
import com.xy.base.permission.Permission
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/03/11
 * desc   : Android 4.3 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_4_3)
open class PermissionDelegateImplV18 : PermissionDelegateImplV14() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean =
        if (TextUtils.equals(permission, Permission.BIND_NOTIFICATION_LISTENER_SERVICE)) isGrantedPermission(context)
        else super.isGrantedPermission(context, permission)

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean =
        if (TextUtils.equals(permission, Permission.BIND_NOTIFICATION_LISTENER_SERVICE)) false
        else super.isPermissionPermanentDenied(activity, permission)

    override fun getPermissionIntent(context: Context, permission: String): Intent? =
        if (TextUtils.equals(permission, Permission.BIND_NOTIFICATION_LISTENER_SERVICE)) getPermissionIntent(context)
        else super.getPermissionIntent(context, permission)
}