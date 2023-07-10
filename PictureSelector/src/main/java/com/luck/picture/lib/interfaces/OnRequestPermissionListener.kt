package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2020/4/24 11:48 AM
 * @describe：OnRequestPermissionListener
 */
interface OnRequestPermissionListener {
    /**
     * Permission request result
     *
     * @param permissionArray
     * @param isResult
     */
    fun onCall(permissionArray: Array<String?>?, isResult: Boolean)
}