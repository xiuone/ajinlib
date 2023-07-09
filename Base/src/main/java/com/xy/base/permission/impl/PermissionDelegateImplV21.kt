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
import com.xy.base.permission.PermissionUtils.getPackageNameUri
import com.xy.base.utils.AndroidVersion
import com.xy.base.utils.AndroidVersion.isAndroid10

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 5.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_5)
open class PermissionDelegateImplV21 : PermissionDelegateImplV19() {
    // 检测获取使用统计权限
    override fun isGrantedPermission(context: Context, permission: String): Boolean =
         if (TextUtils.equals(permission, Permission.PACKAGE_USAGE_STATS)) isGrantedPackagePermission(context)
        else super.isGrantedPermission(context, permission)

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean =
        if (TextUtils.equals(permission, Permission.PACKAGE_USAGE_STATS)) false
        else super.isPermissionPermanentDenied(activity, permission)

    override fun getPermissionIntent(context: Context, permission: String): Intent? =
        if (TextUtils.equals(permission, Permission.PACKAGE_USAGE_STATS)) getPackagePermissionIntent(context)
        else super.getPermissionIntent(context, permission)

    /**
     * 是否有使用统计权限
     */
    private fun isGrantedPackagePermission(context: Context): Boolean =  checkOpNoThrow(context, AppOpsManager.OPSTR_GET_USAGE_STATS)

    /**
     * 获取使用统计权限设置界面意图
     */
    private fun getPackagePermissionIntent(context: Context): Intent? {
        var intent: Intent? = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        if (isAndroid10()) {
            // 经过测试，只有在 Android 10 及以上加包名才有效果
            // 如果在 Android 10 以下加包名会导致无法跳转
            intent!!.data = getPackageNameUri(context)
        }
        if (!areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context)
        }
        return intent
    }
}