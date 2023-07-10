package com.luck.picture.lib.interfaces

import android.view.View

/**
 * @author：luck
 * @date：2022/6/26 2:51 下午
 * @describe：OnGridItemSelectAnimListener
 */
interface OnGridItemSelectAnimListener {
    /**
     * onSelectItemAnim
     *
     * @param view
     * @param isSelected
     */
    fun onSelectItemAnim(view: View?, isSelected: Boolean)
}