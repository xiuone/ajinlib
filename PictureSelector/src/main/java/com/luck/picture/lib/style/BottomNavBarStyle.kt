package com.luck.picture.lib.style

import androidx.annotation.AnimRes

/**
 * @author：luck
 * @date：2021/11/15 4:15 下午
 * @describe：NavBarbottomStyle
 */
class BottomNavBarStyle {
    /**
     * 底部导航栏背景色
     */
    var bottomNarBarBackgroundColor = 0

    /**
     * 底部预览页NarBar背景色
     */
    var bottomPreviewNarBarBackgroundColor = 0

    /**
     * 底部导航栏高度
     *
     *
     * use unit dp
     *
     */
    var bottomNarBarHeight = 0

    /**
     * 底部预览文本
     */
    var bottomPreviewNormalText: String? = null

    /**
     * 底部预览文本
     */
    var bottomPreviewNormalTextResId = 0
        private set

    /**
     * 底部预览文本字体大小
     */
    var bottomPreviewNormalTextSize = 0

    /**
     * 底部预览文本正常字体色值
     */
    var bottomPreviewNormalTextColor = 0

    /**
     * 底部选中预览文本
     */
    var bottomPreviewSelectText: String? = null

    /**
     * 底部选中预览文本
     */
    var bottomPreviewSelectTextResId = 0
        private set

    /**
     * 底部预览文本选中字体色值
     */
    var bottomPreviewSelectTextColor = 0

    /**
     * 底部编辑文字
     */
    var bottomEditorText: String? = null

    /**
     * 底部编辑文字
     */
    var bottomEditorTextResId = 0
        private set

    /**
     * 底部编辑文字大小
     */
    var bottomEditorTextSize = 0

    /**
     * 底部编辑文字色值
     */
    var bottomEditorTextColor = 0

    /**
     * 底部原图文字DrawableLeft
     */
    var bottomOriginalDrawableLeft = 0

    /**
     * 底部原图文字
     */
    var bottomOriginalText: String? = null

    /**
     * 底部原图文字
     */
    var bottomOriginalTextResId = 0
        private set

    /**
     * 底部原图文字大小
     */
    var bottomOriginalTextSize = 0

    /**
     * 底部原图文字色值
     */
    var bottomOriginalTextColor = 0

    /**
     * 已选数量背景样式
     */
    var bottomSelectNumResources = 0

    /**
     * 已选数量文字大小
     */
    var bottomSelectNumTextSize = 0

    /**
     * 已选数量文字颜色
     */
    var bottomSelectNumTextColor = 0

    /**
     * 是否显示已选数量圆点提醒
     */
    var isCompleteCountTips = true
    fun setBottomPreviewNormalText(resId: Int) {
        bottomPreviewNormalTextResId = resId
    }

    fun setBottomPreviewSelectText(resId: Int) {
        bottomPreviewSelectTextResId = resId
    }

    fun setBottomEditorText(resId: Int) {
        bottomEditorTextResId = resId
    }

    fun setBottomOriginalText(resId: Int) {
        bottomOriginalTextResId = resId
    }
}