package com.luck.picture.lib.basic


/**
 * @author：luck
 * @date：2022/1/12 9:32 上午
 * @describe：IBridgePictureBehavior
 */
interface IBridgePictureBehavior {
    /**
     * finish activity
     *
     * @param result data
     */
    fun onSelectFinish(result: PictureCommonFragment.SelectorResult?)
}