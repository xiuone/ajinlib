package com.luck.picture.lib.style

import androidx.annotation.AnimRes

/**
 * @author：luck
 * @date：2021/11/15 4:15 下午
 * @describe：titleBarStyle
 */
class TitleBarStyle {
    /**
     * 是否隐藏标题栏
     */
    var isHideTitleBar = false

    /**
     * 标题栏左边关闭样式
     */
    var titleLeftBackResource = 0

    /**
     * 预览标题栏左边关闭样式
     */
    var previewTitleLeftBackResource = 0

    /**
     * 标题栏默认文案
     */
    var titleDefaultText: String? = null

    /**
     * 标题栏默认文案
     */
    var titleDefaultTextResId = 0
        private set

    /**
     * 标题栏字体大小
     */
    var titleTextSize = 0

    /**
     * 标题栏字体色值
     */
    var titleTextColor = 0

    /**
     * 标题栏背景
     */
    var titleBackgroundColor = 0

    /**
     * 预览标题栏背景
     */
    var previewTitleBackgroundColor = 0

    /**
     * 标题栏高度
     *
     *
     * use  unit dp
     *
     */
    var titleBarHeight = 0

    /**
     * 标题栏专辑背景
     */
    var titleAlbumBackgroundResource = 0

    /**
     * 标题栏位置居左
     */
    var isAlbumTitleRelativeLeft = false

    /**
     * 标题栏右边向上图标
     */
    var titleDrawableRightResource = 0

    /**
     * 标题栏右边取消按钮背景
     */
    var titleCancelBackgroundResource = 0

    /**
     * 是否隐藏取消按钮
     */
    var isHideCancelButton = false

    /**
     * 外部预览删除
     */
    var previewDeleteBackgroundResource = 0

    /**
     * 标题栏右边默认文本
     */
    var titleCancelText: String? = null

    /**
     * 标题栏右边默认文本
     */
    var titleCancelTextResId = 0
        private set

    /**
     * 标题栏右边文本字体大小
     */
    var titleCancelTextSize = 0

    /**
     * 标题栏右边文本字体色值
     */
    var titleCancelTextColor = 0

    /**
     * 标题栏底部线条色值
     */
    var titleBarLineColor = 0

    /**
     * 是否显示标题栏底部线条
     */
    var isDisplayTitleBarLine = false
    fun setTitleDefaultText(resId: Int) {
        titleDefaultTextResId = resId
    }

    fun setTitleCancelText(resId: Int) {
        titleCancelTextResId = resId
    }
}