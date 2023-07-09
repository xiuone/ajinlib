package com.xy.base.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.xy.base.permission.PermissionApi.getDeniedPermissions
import com.xy.base.permission.PermissionApi.getGrantedPermissions
import com.xy.base.permission.PermissionApi.isGrantedPermission
import com.xy.base.permission.PermissionApi.isPermissionPermanentDenied
import com.xy.base.permission.PermissionApi.isSpecialPermission
import com.xy.base.permission.PermissionUtils.containsPermission
import com.xy.base.permission.PermissionUtils.getSmartPermissionIntent
import com.xy.base.permission.PermissionUtils.lockActivityOrientation
import com.xy.base.permission.PermissionUtils.optimizePermissionResults
import com.xy.base.permission.PermissionUtils.postActivityResult
import com.xy.base.permission.PermissionUtils.postDelayed
import com.xy.base.utils.AndroidVersion
import java.util.*
import kotlin.collections.ArrayList

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2018/06/15
 * desc   : 权限请求 Fragment
 */
class PermissionFragment : Fragment(), Runnable {
    /** 是否申请了特殊权限  */
    private var mSpecialRequest = false

    /** 是否申请了危险权限  */
    private var mDangerousRequest = false

    /** 权限申请标记  */
    private var mRequestFlag = false

    /** 权限回调对象  */
    @Nullable
    private var mCallBack: OnPermissionCallback? = null

    /** 权限请求拦截器  */
    @Nullable
    private var mInterceptor: IPermissionInterceptor? = null

    /** Activity 屏幕方向  */
    private var mScreenOrientation = 0

    /**
     * 绑定 Activity
     */
    fun attachActivity(activity: FragmentActivity?) {
        activity?.supportFragmentManager?.beginTransaction()?.add(this, this.toString())?.commitAllowingStateLoss()
    }

    /**
     * 解绑 Activity
     */
    fun detachActivity(activity: FragmentActivity?) {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
    }

    /**
     * 设置权限监听回调监听
     */
    fun setCallBack(@Nullable callback: OnPermissionCallback?) {
        mCallBack = callback
    }

    /**
     * 权限申请标记（防止系统杀死应用后重新触发请求的问题）
     */
    fun setRequestFlag(flag: Boolean) {
        mRequestFlag = flag
    }

    /**
     * 设置权限请求拦截器
     */
    fun setInterceptor(interceptor: IPermissionInterceptor?) {
        mInterceptor = interceptor
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = activity ?: return
        // 如果当前没有锁定屏幕方向就获取当前屏幕方向并进行锁定
        mScreenOrientation = activity.requestedOrientation
        if (mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return
        }

        // 锁定当前 Activity 方向
        lockActivityOrientation(activity)
    }

    override fun onDetach() {
        super.onDetach()
        val activity = activity
        if (activity == null || mScreenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED || activity.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            return
        }
        // 为什么这里不用跟上面一样 try catch ？因为这里是把 Activity 方向取消固定，只有设置横屏或竖屏的时候才可能触发 crash
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onDestroy() {
        super.onDestroy()
        // 取消引用监听器，避免内存泄漏
        mCallBack = null
    }

    override fun onResume() {
        super.onResume()

        // 如果当前 Fragment 是通过系统重启应用触发的，则不进行权限申请
        if (!mRequestFlag) {
            detachActivity(activity)
            return
        }

        // 如果在 Activity 不可见的状态下添加 Fragment 并且去申请权限会导致授权对话框显示不出来
        // 所以必须要在 Fragment 的 onResume 来申请权限，这样就可以保证应用回到前台的时候才去申请权限
        if (mSpecialRequest) {
            return
        }
        mSpecialRequest = true
        requestSpecialPermission()
    }

    /**
     * 申请特殊权限
     */
    fun requestSpecialPermission() {
        val arguments = arguments
        val activity = activity
        if (arguments == null || activity == null) {
            return
        }
        val allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS)?: ArrayList()

        // 是否需要申请特殊权限
        var requestSpecialPermission = false

        // 判断当前是否包含特殊权限
        for (permission in allPermissions) {
            if (!isSpecialPermission(permission)) {
                continue
            }
            if (isGrantedPermission(activity, permission)) {
                // 已经授予过了，可以跳过
                continue
            }
            if (!AndroidVersion.isAndroid11() && TextUtils.equals(permission, Permission.MANAGE_EXTERNAL_STORAGE)) {
                // 当前必须是 Android 11 及以上版本，因为在旧版本上是拿旧权限做的判断
                continue
            }
            // 跳转到特殊权限授权页面
            StartActivityManager.startActivityForResult(this, getSmartPermissionIntent(activity, permission), arguments.getInt(REQUEST_CODE))
            requestSpecialPermission = true
        }
        if (requestSpecialPermission) {
            return
        }
        // 如果没有跳转到特殊权限授权页面，就直接申请危险权限
        requestDangerousPermission()
    }

    /**
     * 申请危险权限
     */
    fun requestDangerousPermission() {
        val activity = activity
        val arguments = arguments
        if (activity == null || arguments == null) {
            return
        }
        val requestCode = arguments.getInt(REQUEST_CODE)
        val allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        if (allPermissions.isNullOrEmpty()) {
            return
        }
        if (!AndroidVersion.isAndroid6()) {
            // 如果是 Android 6.0 以下，没有危险权限的概念，则直接回调监听
            val grantResults = IntArray(allPermissions.size)
            for (i in grantResults.indices) {
                grantResults[i] = if (isGrantedPermission(activity,
                        allPermissions[i])
                ) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
            }
            onRequestPermissionsResult(requestCode, allPermissions.toTypedArray(), grantResults)
            return
        }

        // Android 13 传感器策略发生改变，申请后台传感器权限的前提是要有前台传感器权限
        if (AndroidVersion.isAndroid13() && allPermissions.size >= 2 &&
            containsPermission(Permission.BODY_SENSORS_BACKGROUND,*allPermissions.toTypedArray())) {
            val bodySensorsPermission = ArrayList(allPermissions)
            bodySensorsPermission.remove(Permission.BODY_SENSORS_BACKGROUND)

            // 在 Android 13 的机型上，需要先申请前台传感器权限，再申请后台传感器权限
            splitTwiceRequestPermission(activity,
                allPermissions,
                bodySensorsPermission,
                requestCode)
            return
        }

        // Android 10 定位策略发生改变，申请后台定位权限的前提是要有前台定位权限（授予了精确或者模糊任一权限）
        if (AndroidVersion.isAndroid10() && allPermissions.size >= 2 &&
            containsPermission(Permission.ACCESS_BACKGROUND_LOCATION,*allPermissions.toTypedArray())
        ) {
            val locationPermission = ArrayList(allPermissions)
            locationPermission.remove(Permission.ACCESS_BACKGROUND_LOCATION)

            // 在 Android 10 的机型上，需要先申请前台定位权限，再申请后台定位权限
            splitTwiceRequestPermission(activity, allPermissions, locationPermission, requestCode)
            return
        }

        // 必须要有文件读取权限才能申请获取媒体位置权限
        if (AndroidVersion.isAndroid10() &&
            containsPermission(Permission.ACCESS_MEDIA_LOCATION,*allPermissions.toTypedArray()) &&
            containsPermission(Permission.READ_EXTERNAL_STORAGE,*allPermissions.toTypedArray())
        ) {
            val storagePermission = ArrayList(allPermissions)
            storagePermission.remove(Permission.ACCESS_MEDIA_LOCATION)

            // 在 Android 10 的机型上，需要先申请存储权限，再申请获取媒体位置权限
            splitTwiceRequestPermission(activity, allPermissions, storagePermission, requestCode)
            return
        }
        requestPermissions(allPermissions.toTypedArray(), requestCode)
    }

    /**
     * 拆分两次请求权限（有些情况下，需要先申请 A 权限，才能再申请 B 权限）
     */
    fun splitTwiceRequestPermission(
        activity: FragmentActivity, allPermissions: ArrayList<String>,
        firstPermissions: ArrayList<String>, requestCode: Int,
    ) {
        val secondPermissions = ArrayList(allPermissions)
        for (permission in firstPermissions) {
            secondPermissions.remove(permission)
        }
        launch(activity,
            firstPermissions,
            object : IPermissionInterceptor {},
            object : OnPermissionCallback {
                override fun onGranted(permissions: List<String?>, allGranted: Boolean) {
                    if (!allGranted || !isAdded) {
                        return
                    }

                    // 经过测试，在 Android 13 设备上面，先申请前台权限，然后立马申请后台权限大概率会出现失败
                    // 这里为了避免这种情况出现，所以加了一点延迟，这样就没有什么问题了
                    // 为什么延迟时间是 150 毫秒？ 经过实践得出 100 还是有概率会出现失败，但是换成 150 试了很多次就都没有问题了
                    val delayMillis = if (AndroidVersion.isAndroid13()) 150 else 0.toLong()
                    postDelayed({
                        launch(activity, secondPermissions, object : IPermissionInterceptor {}, object : OnPermissionCallback {
                                override fun onGranted(permissions: List<String?>, allGranted: Boolean, ) {
                                    if (!allGranted || !isAdded) {
                                        return
                                    }

                                    // 所有的权限都授予了
                                    val grantResults = IntArray(allPermissions.size)
                                    Arrays.fill(grantResults,
                                        PackageManager.PERMISSION_GRANTED)
                                    onRequestPermissionsResult(requestCode,
                                        allPermissions.toTypedArray(),
                                        grantResults)
                                }

                                override fun onDenied(permissions: List<String?>, doNotAskAgain: Boolean, ) {
                                    if (!isAdded) {
                                        return
                                    }

                                    // 第二次申请的权限失败了，但是第一次申请的权限已经授予了
                                    val grantResults = IntArray(allPermissions.size)
                                    for (i in allPermissions.indices) {
                                        grantResults[i] = if (containsPermission( allPermissions[i],*secondPermissions.toTypedArray())) PackageManager.PERMISSION_DENIED else PackageManager.PERMISSION_GRANTED
                                    }
                                    onRequestPermissionsResult(requestCode,
                                        allPermissions.toTypedArray(),
                                        grantResults)
                                }
                            })
                    }, delayMillis)
                }

                override fun onDenied(permissions: List<String?>, doNotAskAgain: Boolean) {
                    if (!isAdded) {
                        return
                    }

                    // 第一次申请的权限失败了，没有必要进行第二次申请
                    val grantResults = IntArray(allPermissions.size)
                    Arrays.fill(grantResults, PackageManager.PERMISSION_DENIED)
                    onRequestPermissionsResult(requestCode,
                        allPermissions.toTypedArray(),
                        grantResults)
                }
            })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, ) {
        if (permissions.isEmpty() || grantResults.isEmpty()) {
            return
        }
        val arguments = arguments
        val activity = activity
        if (activity == null || arguments == null || mInterceptor == null || requestCode != arguments.getInt(REQUEST_CODE)) {
            return
        }
        val callback = mCallBack
        mCallBack = null
        val interceptor: IPermissionInterceptor? = mInterceptor
        mInterceptor = null

        // 优化权限回调结果
        optimizePermissionResults(activity, permissions, grantResults)

        // 将数组转换成 ArrayList
        val allPermissions = ArrayList(permissions.asList())

        // 释放对这个请求码的占用
        REQUEST_CODE_ARRAY.remove(requestCode)
        // 将 Fragment 从 Activity 移除
        detachActivity(activity)

        // 获取已授予的权限
        val grantedPermissions = getGrantedPermissions(allPermissions, grantResults)

        // 如果请求成功的权限集合大小和请求的数组一样大时证明权限已经全部授予
        if (grantedPermissions.size == allPermissions.size) {
            // 代表申请的所有的权限都授予了
            interceptor?.grantedPermissionRequest(activity,
                allPermissions,
                grantedPermissions,
                true,
                callback)
            // 权限申请结束
            interceptor?.finishPermissionRequest(activity, allPermissions, false, callback)
            return
        }

        // 获取被拒绝的权限
        val deniedPermissions: ArrayList<String> = getDeniedPermissions(allPermissions, grantResults)

        // 代表申请的权限中有不同意授予的，如果有某个权限被永久拒绝就返回 true 给开发人员，让开发者引导用户去设置界面开启权限
        interceptor?.deniedPermissionRequest(activity, allPermissions, deniedPermissions,
            isPermissionPermanentDenied(activity, *deniedPermissions.toTypedArray()), callback)

        // 证明还有一部分权限被成功授予，回调成功接口
        if (grantedPermissions.isNotEmpty()) {
            interceptor?.grantedPermissionRequest(activity, allPermissions, grantedPermissions, false, callback)
        }

        // 权限申请结束
        interceptor?.finishPermissionRequest(activity, allPermissions, false, callback)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        val activity = activity
        val arguments = arguments
        if (activity == null || arguments == null || mDangerousRequest || requestCode != arguments.getInt(REQUEST_CODE)) {
            return
        }
        val allPermissions = arguments.getStringArrayList(REQUEST_PERMISSIONS)
        if (allPermissions.isNullOrEmpty()) {
            return
        }
        mDangerousRequest = true
        postActivityResult(this,*allPermissions.toTypedArray())
    }

    override fun run() {
        // 如果用户离开太久，会导致 Activity 被回收掉
        // 所以这里要判断当前 Fragment 是否有被添加到 Activity
        // 可在开发者模式中开启不保留活动来复现这个 Bug
        if (!isAdded) {
            return
        }
        // 请求其他危险权限
        requestDangerousPermission()
    }

    companion object {
        /** 请求的权限组  */
        private const val REQUEST_PERMISSIONS = "request_permissions"

        /** 请求码（自动生成） */
        private const val REQUEST_CODE = "request_code"

        /** 权限请求码存放集合  */
        private val REQUEST_CODE_ARRAY: MutableList<Int> = ArrayList()

        /**
         * 开启权限申请
         */
        fun launch(activity: FragmentActivity?, permissions: ArrayList<String>,
            interceptor: IPermissionInterceptor, @Nullable callback: OnPermissionCallback?) {
            val fragment = PermissionFragment()
            val bundle = Bundle()
            var requestCode: Int
            // 请求码随机生成，避免随机产生之前的请求码，必须进行循环判断
            do {
                // 新版本的 Support 库限制请求码必须小于 65536
                // 旧版本的 Support 库限制请求码必须小于 256
                requestCode = Random().nextInt(Math.pow(2.0, 8.0).toInt())
            } while (REQUEST_CODE_ARRAY.contains(requestCode))
            // 标记这个请求码已经被占用
            REQUEST_CODE_ARRAY.add(requestCode)
            bundle.putInt(REQUEST_CODE, requestCode)
            bundle.putStringArrayList(REQUEST_PERMISSIONS, permissions)
            fragment.arguments = bundle
            // 设置保留实例，不会因为屏幕方向或配置变化而重新创建
            fragment.retainInstance = true
            // 设置权限申请标记
            fragment.setRequestFlag(true)
            // 设置权限回调监听
            fragment.setCallBack(callback)
            // 设置权限请求拦截器
            fragment.setInterceptor(interceptor)
            // 绑定到 Activity 上面
            fragment.attachActivity(activity)
        }
    }
}