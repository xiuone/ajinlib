package com.luck.picture.lib.interfaces

import android.view.View
import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2020-03-26 10:50
 * @describe：OnItemClickListener
 */
interface OnItemClickListener {
    /**
     * Item click event
     *
     * @param v
     * @param position
     */
    fun onItemClick(v: View?, position: Int)
}