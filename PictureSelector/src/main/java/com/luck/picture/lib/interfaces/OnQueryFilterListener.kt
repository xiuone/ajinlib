package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2022/3/12 10:09 上午
 * @describe：OnQueryFilterListener
 */
interface OnQueryFilterListener {
    /**
     * You need to filter out what doesn't meet the standards
     *
     * @return the boolean result
     */
    fun onFilter(media: LocalMedia?): Boolean
}