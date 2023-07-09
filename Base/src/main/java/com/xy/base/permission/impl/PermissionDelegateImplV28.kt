package com.xy.base.permission.impl

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.Permission
import com.xy.base.permission.PermissionUtils.checkSelfPermission
import com.xy.base.permission.PermissionUtils.shouldShowRequestPermissionRationale
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/07/03
 * desc   : Android 9.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_9)
internal open class PermissionDelegateImplV28 : PermissionDelegateImplV26() {
    override fun isGrantedPermission(context: Context, permission: String): Boolean {
        return if (TextUtils.equals(permission, Permission.ACCEPT_HANDOVER)) {
            checkSelfPermission(context, permission)
        } else super.isGrantedPermission(context, permission)
    }

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean {
        return if (TextUtils.equals(permission, Permission.ACCEPT_HANDOVER)) {
            !checkSelfPermission(activity, permission) &&
                    !shouldShowRequestPermissionRationale(activity, permission)
        } else super.isPermissionPermanentDenied(activity, permission)
    }
}