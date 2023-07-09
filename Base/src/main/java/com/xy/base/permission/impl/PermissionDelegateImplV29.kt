package com.xy.base.permission.impl

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.Permission
import com.xy.base.permission.PermissionUtils.checkSelfPermission
import com.xy.base.permission.PermissionUtils.shouldShowRequestPermissionRationale
import com.xy.base.utils.AndroidVersion
import com.xy.base.utils.AndroidVersion.getTargetSdkVersionCode
import com.xy.base.utils.AndroidVersion.isAndroid11
import com.xy.base.utils.AndroidVersion.isAndroid13

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 10 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_10)
internal open class PermissionDelegateImplV29 : PermissionDelegateImplV28() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        if (TextUtils.equals(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            return hasReadStoragePermission(context) &&
                    checkSelfPermission(context, Permission.ACCESS_MEDIA_LOCATION)
        }
        if (TextUtils.equals(permission, Permission.ACCESS_BACKGROUND_LOCATION) ||
            TextUtils.equals(permission, Permission.ACTIVITY_RECOGNITION)
        ) {
            return checkSelfPermission(context, permission)
        }

        // 向下兼容 Android 11 新权限
        if (!isAndroid11()) {
            if (TextUtils.equals(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
                // 这个是 Android 10 上面的历史遗留问题，假设申请的是 MANAGE_EXTERNAL_STORAGE 权限
                // 必须要在 AndroidManifest.xml 中注册 android:requestLegacyExternalStorage="true"
                if (!isUseDeprecationExternalStorage()) {
                    return false
                }
            }
        }
        return super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean {
        if (TextUtils.equals(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            return if (!checkSelfPermission(activity, Permission.ACCESS_FINE_LOCATION)) {
                !shouldShowRequestPermissionRationale(activity, Permission.ACCESS_FINE_LOCATION)
            } else !checkSelfPermission(activity, permission) &&
                    !shouldShowRequestPermissionRationale(activity, permission)
        }
        if (TextUtils.equals(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            return hasReadStoragePermission(activity) &&
                    !checkSelfPermission(activity, permission) &&
                    !shouldShowRequestPermissionRationale(activity, permission)
        }
        if (TextUtils.equals(permission, Permission.ACTIVITY_RECOGNITION)) {
            return !checkSelfPermission(activity, permission) &&
                    !shouldShowRequestPermissionRationale(activity, permission)
        }

        // 向下兼容 Android 11 新权限
        if (!isAndroid11()) {
            if (TextUtils.equals(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
                // 处理 Android 10 上面的历史遗留问题
                if (!isUseDeprecationExternalStorage()) {
                    return true
                }
            }
        }
        return super.isPermissionPermanentDenied(activity, permission)
    }

    /**
     * 是否有读取文件的权限
     */
    private fun hasReadStoragePermission(context: Context): Boolean {
        if (isAndroid13() && getTargetSdkVersionCode(context) >= AndroidVersion.ANDROID_13) {
            return checkSelfPermission(context, Permission.READ_MEDIA_IMAGES) ||
                    isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE)
        }
        return if (isAndroid11() && getTargetSdkVersionCode(context) >= AndroidVersion.ANDROID_11) {
            checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE) ||
                    isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE)
        } else checkSelfPermission(context, Permission.READ_EXTERNAL_STORAGE)
    }

    private fun isUseDeprecationExternalStorage(): Boolean  = Environment.isExternalStorageLegacy()
}