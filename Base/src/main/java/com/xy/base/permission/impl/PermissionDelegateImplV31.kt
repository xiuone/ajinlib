package com.xy.base.permission.impl

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.Permission
import com.xy.base.permission.PermissionIntentManager.getApplicationDetailsIntent
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.permission.PermissionUtils.checkSelfPermission
import com.xy.base.permission.PermissionUtils.getPackageNameUri
import com.xy.base.permission.PermissionUtils.shouldShowRequestPermissionRationale
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 12 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_12)
internal open class PermissionDelegateImplV31 : PermissionDelegateImplV30() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        // 检测闹钟权限
        if (TextUtils.equals(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            return isGrantedAlarmPermission(context)
        }
        return if (TextUtils.equals(permission, Permission.BLUETOOTH_SCAN) ||
            TextUtils.equals(permission, Permission.BLUETOOTH_CONNECT) ||
            TextUtils.equals(permission, Permission.BLUETOOTH_ADVERTISE)
        ) {
            checkSelfPermission(context, permission)
        } else super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean {
        if (TextUtils.equals(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            return false
        }
        if (TextUtils.equals(permission, Permission.BLUETOOTH_SCAN) ||
            TextUtils.equals(permission, Permission.BLUETOOTH_CONNECT) ||
            TextUtils.equals(permission, Permission.BLUETOOTH_ADVERTISE)
        ) {
            return !checkSelfPermission(activity, permission) &&
                    !shouldShowRequestPermissionRationale(activity, permission)
        }
        return if (activity.applicationInfo.targetSdkVersion >= AndroidVersion.ANDROID_12 &&
            TextUtils.equals(permission, Permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            if (!checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION) &&
                !checkSelfPermission(activity, Permission.ACCESS_COARSE_LOCATION)
            ) {
                !shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION) &&
                        !shouldShowRequestPermissionRationale(activity,
                            Permission.ACCESS_COARSE_LOCATION)
            } else !checkSelfPermission(activity, permission) &&
                    !shouldShowRequestPermissionRationale(activity, permission)
        } else super.isPermissionPermanentDenied(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        return if (TextUtils.equals(permission, Permission.SCHEDULE_EXACT_ALARM)) {
            getAlarmPermissionIntent(context)
        } else super.getPermissionIntent(context, permission)
    }
    /**
     * 是否有闹钟权限
     */
    private fun isGrantedAlarmPermission(context: Context): Boolean =
        context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

    /**
     * 获取闹钟权限设置界面意图
     */
    private fun getAlarmPermissionIntent(context: Context): Intent? {
        var intent: Intent? = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        intent?.data = getPackageNameUri(context)
        if (!areActivityIntent(context, intent)) intent = getApplicationDetailsIntent(context)
        return intent
    }
}