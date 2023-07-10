package com.luck.picture.lib.style

import androidx.annotation.AnimRes
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2021/11/16 6:41 下午
 * @describe：PictureWindowAnimationStyle
 */
class PictureWindowAnimationStyle {
    /**
     * 相册启动动画
     */
    @AnimRes
    var activityEnterAnimation = 0

    /**
     * 相册退出动画
     */
    @AnimRes
    var activityExitAnimation = 0

    /**
     * 预览界面启动动画
     */
    @AnimRes
    var activityPreviewEnterAnimation = 0

    /**
     * 预览界面退出动画
     */
    @AnimRes
    var activityPreviewExitAnimation = 0

    constructor() {}
    constructor(
        @AnimRes activityEnterAnimation: Int,
        @AnimRes activityExitAnimation: Int
    ) {
        this.activityEnterAnimation = activityEnterAnimation
        this.activityExitAnimation = activityExitAnimation
        activityPreviewEnterAnimation = activityEnterAnimation
        activityPreviewExitAnimation = activityExitAnimation
    }

    companion object {
        /**
         * 默认WindowAnimationStyle
         *
         * @return this
         */
        fun ofDefaultWindowAnimationStyle(): PictureWindowAnimationStyle {
            return PictureWindowAnimationStyle(R.anim.ps_anim_enter, R.anim.ps_anim_exit)
        }
    }
}