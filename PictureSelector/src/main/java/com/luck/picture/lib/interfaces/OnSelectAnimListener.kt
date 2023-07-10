package com.luck.picture.lib.interfaces

import android.view.View
import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2022/6/26 2:51 下午
 * @describe：OnSelectAnimListener
 */
interface OnSelectAnimListener {
    /**
     * onSelectAnim
     *
     * @param view
     * @return anim duration
     */
    fun onSelectAnim(view: View?): Long
}