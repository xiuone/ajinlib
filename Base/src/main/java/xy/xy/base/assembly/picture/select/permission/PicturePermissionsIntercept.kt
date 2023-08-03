package xy.xy.base.assembly.picture.select.permission

import androidx.fragment.app.Fragment
import com.hjq.permissions.IPermissionInterceptor
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.interfaces.OnPermissionsInterceptListener
import com.luck.picture.lib.interfaces.OnRequestPermissionListener

class PicturePermissionsIntercept(private val interceptor: IPermissionInterceptor):
    OnPermissionsInterceptListener {

    override fun requestPermission(fragment: Fragment?, permissionArray: Array<out String>?, call: OnRequestPermissionListener?, ) {
        if (fragment == null)return
        XXPermissions.with(fragment).permission(permissionArray).interceptor(interceptor).request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                call?.onCall(permissionArray,true)
            }

            override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                super.onDenied(permissions, doNotAskAgain)
                call?.onCall(permissionArray,false)
            }
        })
    }

    override fun hasPermissions(fragment: Fragment?, permissionArray: Array<out String>?): Boolean {
        val context = fragment?.context?:return false
        return XXPermissions.isGranted(context,permissionArray)
    }
}