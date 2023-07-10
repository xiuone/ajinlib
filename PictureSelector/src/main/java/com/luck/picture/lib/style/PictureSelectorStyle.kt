package com.luck.picture.lib.style

import androidx.annotation.AnimRes

/**
 * @author：luck
 * @date：2021/11/15 4:12 下午
 * @describe：PictureSelectorUIStyle
 */
class PictureSelectorStyle {
    /**
     * Album Window Style
     */
    var albumWindowStyle: AlbumWindowStyle? = null
        get() = if (field == null) AlbumWindowStyle() else field

    /**
     * TitleBar UI Style
     */
    var titleBarStyle: TitleBarStyle? = null
        get() = if (field == null) TitleBarStyle() else field

    /**
     * SelectMainStyle
     */
    var selectMainStyle: SelectMainStyle? = null
        get() = if (field == null) SelectMainStyle() else field

    /**
     * BottomBar UI Style
     */
    var bottomBarStyle: BottomNavBarStyle? = null
        get() = if (field == null) BottomNavBarStyle() else field

    /**
     * PictureSelector Window AnimationStyle
     */
    var windowAnimationStyle: PictureWindowAnimationStyle? = null
        get() {
            if (field == null) {
                field = PictureWindowAnimationStyle.Companion.ofDefaultWindowAnimationStyle()
            }
            return field
        }
}