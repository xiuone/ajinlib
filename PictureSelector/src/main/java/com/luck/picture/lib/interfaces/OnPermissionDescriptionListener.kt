package com.luck.picture.lib.interfaces

import androidx.fragment.app.Fragment
import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2021/12/1 8:48 下午
 * @describe：OnPermissionDescriptionListener
 */
interface OnPermissionDescriptionListener {
    /**
     * Permission description
     *
     * @param fragment
     * @param permissionArray
     */
    fun onPermissionDescription(fragment: Fragment?, permissionArray: Array<String?>?)

    /**
     * onDismiss
     */
    fun onDismiss(fragment: Fragment?)
}