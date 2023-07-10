package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2022/3/12 9:00 下午
 * @describe：OnSelectFilterListener
 */
interface OnSelectFilterListener {
    /**
     * You need to filter out the content that does not meet the selection criteria
     *
     * @param media current select [LocalMedia]
     * @return the boolean result
     */
    fun onSelectFilter(media: LocalMedia?): Boolean
}