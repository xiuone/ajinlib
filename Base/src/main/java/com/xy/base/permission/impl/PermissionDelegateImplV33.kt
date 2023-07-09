package com.xy.base.permission.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.NotificationPermissionCompat.getPermissionIntent
import com.xy.base.permission.Permission
import com.xy.base.permission.PermissionUtils.checkSelfPermission
import com.xy.base.permission.PermissionUtils.shouldShowRequestPermissionRationale
import com.xy.base.utils.AndroidVersion
import com.xy.base.utils.AndroidVersion.getTargetSdkVersionCode

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/26
 * desc   : Android 13 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_13)
internal class PermissionDelegateImplV33 : PermissionDelegateImplV31() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (TextUtils.equals(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            // 有后台传感器权限的前提条件是要有前台的传感器权限
            return checkSelfPermission(context, Permission.BODY_SENSORS) &&
                    checkSelfPermission(context, Permission.BODY_SENSORS_BACKGROUND)
        }
        if (TextUtils.equals(permission, Permission.POST_NOTIFICATIONS) ||
            TextUtils.equals(permission, Permission.NEARBY_WIFI_DEVICES) ||
            TextUtils.equals(permission, Permission.READ_MEDIA_IMAGES) ||
            TextUtils.equals(permission, Permission.READ_MEDIA_VIDEO) ||
            TextUtils.equals(permission, Permission.READ_MEDIA_AUDIO)
        ) {
            return checkSelfPermission(context, permission)
        }
        if (getTargetSdkVersionCode(context) >= AndroidVersion.ANDROID_13) {
            // 亲测当这两个条件满足的时候，在 Android 13 不能申请 WRITE_EXTERNAL_STORAGE，会被系统直接拒绝
            // 不会弹出系统授权对话框，框架为了保证不同 Android 版本的回调结果一致性，这里直接返回 true 给到外层
            if (TextUtils.equals(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
                return true
            }
            if (TextUtils.equals(permission, Permission.READ_EXTERNAL_STORAGE)) {
                return checkSelfPermission(context, Permission.READ_MEDIA_IMAGES) &&
                        checkSelfPermission(context, Permission.READ_MEDIA_VIDEO) &&
                        checkSelfPermission(context, Permission.READ_MEDIA_AUDIO)
            }
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean {
        if (TextUtils.equals(permission, Permission.BODY_SENSORS_BACKGROUND)) {
            return if (!checkSelfPermission(activity, Permission.BODY_SENSORS)) {
                !shouldShowRequestPermissionRationale(activity, Permission.BODY_SENSORS)
            } else !checkSelfPermission(activity, permission) &&
                    !shouldShowRequestPermissionRationale(activity, permission)
        }
        if (TextUtils.equals(permission, Permission.POST_NOTIFICATIONS) ||
            TextUtils.equals(permission, Permission.NEARBY_WIFI_DEVICES) ||
            TextUtils.equals(permission, Permission.READ_MEDIA_IMAGES) ||
            TextUtils.equals(permission, Permission.READ_MEDIA_VIDEO) ||
            TextUtils.equals(permission, Permission.READ_MEDIA_AUDIO)
        ) {
            return !checkSelfPermission(activity, permission) &&
                    !shouldShowRequestPermissionRationale(activity, permission)
        }
        if (getTargetSdkVersionCode(activity) >= AndroidVersion.ANDROID_13) {
            if (TextUtils.equals(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
                return false
            }
            if (TextUtils.equals(permission, Permission.READ_EXTERNAL_STORAGE)) {
                return !checkSelfPermission(activity, Permission.READ_MEDIA_IMAGES) &&
                        !shouldShowRequestPermissionRationale(activity,
                            Permission.READ_MEDIA_IMAGES) &&
                        !checkSelfPermission(activity, Permission.READ_MEDIA_VIDEO) &&
                        !shouldShowRequestPermissionRationale(activity,
                            Permission.READ_MEDIA_VIDEO) &&
                        !checkSelfPermission(activity, Permission.READ_MEDIA_AUDIO) &&
                        !shouldShowRequestPermissionRationale(activity, Permission.READ_MEDIA_AUDIO)
            }
        }
        return super.isPermissionPermanentDenied(activity, permission)
    }

    override fun getPermissionIntent(context: Context, permission: String): Intent? {
        // Github issue 地址：https://github.com/getActivity/XXPermissions/issues/208
        // POST_NOTIFICATIONS 要跳转到权限设置页和 NOTIFICATION_SERVICE 权限是一样的
        return if (TextUtils.equals(permission, Permission.POST_NOTIFICATIONS)) {
            getPermissionIntent(context)
        } else super.getPermissionIntent(context, permission)
    }
}