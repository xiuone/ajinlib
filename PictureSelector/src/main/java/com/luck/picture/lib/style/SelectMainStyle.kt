package com.luck.picture.lib.style

import androidx.annotation.AnimRes

/**
 * @author：luck
 * @date：2021/11/15 4:14 下午
 * @describe：SelectMainStyle
 */
class SelectMainStyle {
    /**
     * 状态栏背景色
     */
    var statusBarColor = 0

    /**
     * 导航栏背景色
     */
    var navigationBarColor = 0

    /**
     * 状态栏字体颜色，非黑即白
     */
    var isDarkStatusBarBlack = false

    /**
     * 完成按钮从底部放在右上角
     */
    var isCompleteSelectRelativeTop = false

    /**
     * 预览页选择按钮从顶部放在右下角
     */
    var isPreviewSelectRelativeBottom = false

    /**
     * 预览页是否显示选择画廊
     */
    var isPreviewDisplaySelectGallery = false

    /**
     * 预览页选择按钮MarginRight
     *
     *
     * unit dp
     *
     */
    var previewSelectMarginRight = 0

    /**
     * 预览背景色
     */
    var previewBackgroundColor = 0

    /**
     * 预览页选择按钮文本
     */
    var previewSelectText: String? = null

    /**
     * 预览页选择按钮文本
     */
    var previewSelectTextResId = 0
        private set

    /**
     * 预览页选择按钮字体大小
     */
    var previewSelectTextSize = 0

    /**
     * 预览页选择按钮字体颜色
     */
    var previewSelectTextColor = 0

    /**
     * 勾选样式
     */
    var selectBackground = 0

    /**
     * 预览样式勾选样式
     */
    var previewSelectBackground = 0

    /**
     * 勾选样式是否使用数量类型
     */
    var isSelectNumberStyle = false

    /**
     * 预览页勾选样式是否使用数量类型
     */
    var isPreviewSelectNumberStyle = false

    /**
     * 列表背景色
     */
    var mainListBackgroundColor = 0

    /**
     * 选择按钮默认文本
     */
    var selectNormalText: String? = null

    /**
     * 选择按钮默认文本
     */
    var selectNormalTextResId = 0
        private set

    /**
     * 选择按钮默认文本字体大小
     */
    var selectNormalTextSize = 0

    /**
     * 选择按钮默认文本字体色值
     */
    var selectNormalTextColor = 0

    /**
     * 选择按钮默认背景
     */
    var selectNormalBackgroundResources = 0

    /**
     * 选择按钮文本
     */
    var selectText: String? = null

    /**
     * 选择按钮文本
     */
    var selectTextResId = 0
        private set

    /**
     * 选择按钮文本字体大小
     */
    var selectTextSize = 0

    /**
     * 选择按钮文本字体色值
     */
    var selectTextColor = 0

    /**
     * 选择按钮选中背景
     */
    var selectBackgroundResources = 0

    /**
     * RecyclerView列表item间隙
     *
     *
     * use unit dp
     *
     */
    var adapterItemSpacingSize = 0

    /**
     * 是否显示左右间距
     */
    var isAdapterItemIncludeEdge = false

    /**
     * 勾选样式字体大小
     */
    var adapterSelectTextSize = 0

    /**
     * 勾选按钮点击区域
     *
     *
     * use unit dp
     *
     */
    var adapterSelectClickArea = 0

    /**
     * 勾选样式字体色值
     */
    var adapterSelectTextColor = 0

    /**
     * 勾选样式位置
     * []
     */
    var adapterSelectStyleGravity: IntArray

    /**
     * 资源类型标识
     */
    var adapterDurationDrawableLeft = 0

    /**
     * 时长文字字体大小
     */
    var adapterDurationTextSize = 0

    /**
     * 时长文字颜色
     */
    var adapterDurationTextColor = 0

    /**
     * 时长文字位置
     * []
     */
    var adapterDurationGravity: IntArray

    /**
     * 时长文字阴影背景
     */
    var adapterDurationBackgroundResources = 0

    /**
     * 拍照按钮背景色
     */
    var adapterCameraBackgroundColor = 0

    /**
     * 拍照按钮图标
     */
    var adapterCameraDrawableTop = 0

    /**
     * 拍照按钮文本
     */
    var adapterCameraText: String? = null

    /**
     * 拍照按钮文本
     */
    var adapterCameraTextResId = 0
        private set

    /**
     * 拍照按钮文本字体色值
     */
    var adapterCameraTextColor = 0

    /**
     * 拍照按钮文本字体大小
     */
    var adapterCameraTextSize = 0

    /**
     * 资源图标识的背景
     */
    var adapterTagBackgroundResources = 0

    /**
     * 资源标识的字体大小
     */
    var adapterTagTextSize = 0

    /**
     * 资源标识的字体色值
     */
    var adapterTagTextColor = 0

    /**
     * 资源标识的位置
     * []
     */
    var adapterTagGravity: IntArray

    /**
     * 图片被编辑标识
     */
    var adapterImageEditorResources = 0

    /**
     * 图片被编辑标识位置
     * []
     */
    var adapterImageEditorGravity: IntArray

    /**
     * 预览页画廊边框样式
     */
    var adapterPreviewGalleryFrameResource = 0

    /**
     * 预览页画廊背景色
     */
    var adapterPreviewGalleryBackgroundResource = 0

    /**
     * 预览页画廊item大小
     *
     *
     * use unit dp
     *
     */
    var adapterPreviewGalleryItemSize = 0
    fun setPreviewSelectText(resId: Int) {
        previewSelectTextResId = resId
    }

    fun setSelectNormalText(resId: Int) {
        selectNormalTextResId = resId
    }

    fun setSelectText(resId: Int) {
        selectTextResId = resId
    }

    fun setAdapterCameraText(resId: Int) {
        adapterCameraTextResId = resId
    }
}