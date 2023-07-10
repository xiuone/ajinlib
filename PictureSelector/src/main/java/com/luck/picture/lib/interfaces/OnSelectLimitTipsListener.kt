package com.luck.picture.lib.interfaces

import android.content.Context
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2022/1/8 2:12 下午
 * @describe：OnSelectLimitTipsListener
 */
interface OnSelectLimitTipsListener {
    /**
     * Custom limit tips
     *
     * @param media Current Selection [LocalMedia]
     * @param config    PictureSelectionConfig
     * @param limitType Use [SelectLimitType]
     * @return If true is returned, the user needs to customize the implementation prompt content，
     * Otherwise, use the system default prompt
     */
    fun onSelectLimitTips(
        context: Context?,
        media: LocalMedia?,
        config: SelectorConfig?,
        limitType: Int
    ): Boolean
}