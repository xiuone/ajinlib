package com.luck.picture.lib.interfaces

import androidx.fragment.app.Fragment
import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2021/12/1 8:48 下午
 * @describe：OnPermissionsInterceptListener
 */
interface OnPermissionsInterceptListener {
    /**
     * Custom Permissions management
     *
     * @param fragment
     * @param permissionArray Permissions array
     * @param call
     */
    fun requestPermission(
        fragment: Fragment?,
        permissionArray: Array<String?>?,
        call: OnRequestPermissionListener?
    )

    /**
     * Verify permission application status
     *
     * @param fragment
     * @param permissionArray
     * @return
     */
    fun hasPermissions(fragment: Fragment?, permissionArray: Array<String?>?): Boolean
}