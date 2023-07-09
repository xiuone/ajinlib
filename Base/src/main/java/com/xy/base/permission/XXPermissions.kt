package com.xy.base.permission

import android.app.Activity
import android.content.Context
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.PermissionApi.containsSpecialPermission
import com.xy.base.permission.PermissionApi.getDeniedPermissions
import com.xy.base.permission.PermissionApi.isGrantedPermissions
import com.xy.base.permission.PermissionApi.isPermissionPermanentDenied
import com.xy.base.permission.PermissionIntentManager.getApplicationDetailsIntent
import com.xy.base.permission.StartActivityManager.startActivity
import com.xy.base.utils.AndroidVersion

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : Android 危险权限请求类
 */
object XXPermissions{

    /** 权限设置页跳转请求码  */
    const val REQUEST_CODE = 1024 + 1


    /**
     * 设置请求的对象
     *
     * @param context          当前 Activity，可以传入栈顶的 Activity
     */
    fun with(context: Context,permissionInterceptor: IPermissionInterceptor): XXPermissionsBuild = XXPermissionsBuild(context,permissionInterceptor)

    fun isGranted(context: Context,vararg permissions: String): Boolean = isGrantedPermissions(context, *permissions)

    fun getDenied(context: Context, vararg permissions: String): ArrayList<String> = getDeniedPermissions(context,*permissions)

    fun getDenied(context: Context, permissions: ArrayList<String>): ArrayList<String> = getDeniedPermissions(context,*permissions.toTypedArray())


    /**
     * 判断权限列表中是否包含特殊权限
     */
    fun containsSpecial(vararg permissions: String): Boolean = containsSpecialPermission(*permissions)

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     */
    fun isPermanentDenied(activity: FragmentActivity, vararg permissions: String): Boolean=
        isPermissionPermanentDenied(activity, *permissions)


    fun startPermissionActivity(activity: FragmentActivity ?,@Nullable callback: OnPermissionPageCallback?,vararg permissions: String) {
        if (activity == null || activity.isFinishing) return
        if (AndroidVersion.isAndroid4_2() && activity.isDestroyed) return
        if (permissions.isEmpty()) {
            startActivity(activity, getApplicationDetailsIntent(activity))
            return
        }
        val permissionsList = ArrayList<String>()
        for (item in permissions) permissionsList.add(item)
        PermissionPageFragment.beginRequest(activity, permissionsList, callback)
    }


    fun startPermissionActivity(fragment: Fragment? ,@Nullable callback: OnPermissionPageCallback?,vararg permissions: String) {
        val activity = fragment?.activity
        if (activity == null || activity.isFinishing) return
        if (AndroidVersion.isAndroid4_2() && activity.isDestroyed) return
        if (permissions.isEmpty()) {
            startActivity(fragment, getApplicationDetailsIntent(activity))
            return
        }
        val permissionsList = ArrayList<String>()
        for (item in permissions) permissionsList.add(item)
        PermissionPageFragment.beginRequest(activity, permissionsList, callback)
    }
}