package com.xy.base.permission.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.Permission
import com.xy.base.permission.PermissionIntentManager.getApplicationDetailsIntent
import com.xy.base.permission.PermissionUtils.areActivityIntent
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2022/06/11
 * desc   : Android 4.0 权限委托实现
 */
@RequiresApi(api = AndroidVersion.ANDROID_4_0)
open class PermissionDelegateImplV14 : PermissionDelegate {
    override fun isGrantedPermission(context: Context, permission: String): Boolean =
        if (TextUtils.equals(permission, Permission.BIND_VPN_SERVICE)) isGrantedVpnPermission(context) else true

    override fun isPermissionPermanentDenied(activity: FragmentActivity, permission: String): Boolean = false

    override fun getPermissionIntent(context: Context, permission: String): Intent? =
        if (TextUtils.equals(permission, Permission.BIND_VPN_SERVICE)) getVpnPermissionIntent(context)
        else getApplicationDetailsIntent(context)

    private fun isGrantedVpnPermission(context: Context): Boolean = VpnService.prepare(context) == null

    /**
     * 获取 VPN 权限设置界面意图
     */
    private fun getVpnPermissionIntent(context: Context): Intent? {
        var intent = VpnService.prepare(context)
        if (!areActivityIntent(context, intent)) intent = getApplicationDetailsIntent(context)
        return intent
    }

    protected fun TextUtils.equals(permission1:String, permission2:String) = TextUtils.equals(permission1,permission2)
}