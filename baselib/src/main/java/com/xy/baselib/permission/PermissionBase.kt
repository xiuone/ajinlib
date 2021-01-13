package com.xy.baselib.permission

import android.app.Activity
import androidx.fragment.app.Fragment
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import com.yanzhenjie.permission.Rationale
import com.yanzhenjie.permission.Request

abstract class PermissionBase {
    var view: Any? = null
    var fragment: Fragment? = null
    var permissionListener: PermissionListener

    constructor(
        view: Any?, permissionListener: PermissionListener,
        permissions: Array<String?>,
        permissCode: Int) {
        this.view = view
        this.permissionListener = permissionListener
        requestPermission(permissCode, *permissions)
    }
    /**
     * 申请SD卡权限，单个的。
     */
    private fun requestPermission(requestCode: Int, vararg permissions: String?) {
        view?.run {
            var request: Request? = null
            if (this is Activity)
                request = AndPermission.with(this )
            if (this is Fragment)
                request = AndPermission.with(this )
            request?.run {
                requestCode(requestCode)
                    .permission(*permissions)
                    .callback(permissionListener)
                    .rationale { _: Int, rationale: Rationale ->
                        // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                        rationale.resume()
                    }.start()
            }
        }
    }

}
