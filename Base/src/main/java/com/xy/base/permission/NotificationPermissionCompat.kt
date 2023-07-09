package com.xy.base.permission

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.permission.PermissionUtils.checkOpNoThrow
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/03/12
 * desc   : 通知栏权限兼容类
 */
object NotificationPermissionCompat {
    private const val OP_POST_NOTIFICATION_FIELD_NAME = "OP_POST_NOTIFICATION"
    private const val OP_POST_NOTIFICATION_DEFAULT_VALUE = 11
    fun isGrantedPermission(context: Context): Boolean {
        if (AndroidVersion.isAndroid7()) {
            return context.getSystemService(NotificationManager::class.java).areNotificationsEnabled()
        }
        return if (AndroidVersion.isAndroid4_4()) checkOpNoThrow(context, OP_POST_NOTIFICATION_FIELD_NAME, OP_POST_NOTIFICATION_DEFAULT_VALUE) else true
    }

    fun getPermissionIntent(context: Context): Intent? {
        var intent: Intent? = null
        if (AndroidVersion.isAndroid8()) {
            intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else if (AndroidVersion.isAndroid5()) {
            intent = Intent()
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        }
        if (!areActivityIntent(context, intent)) {
            intent = PermissionIntentManager.getApplicationDetailsIntent(context)
        }
        return intent
    }
}