package com.luck.picture.lib.config

import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.Synchronized
import com.luck.picture.lib.utils.FileDirMap
import kotlin.jvm.Volatile
import com.luck.picture.lib.config.SelectorProviders

/**
 * @author：luck
 * @date：2021/12/1 6:49 下午
 * @describe：CustomIntentKey
 */
object CustomIntentKey {
    /**
     * 自定义数据
     */
    const val EXTRA_CUSTOM_EXTRA_DATA = "customExtraData"

    /**
     * 输出的路径
     */
    const val EXTRA_OUT_PUT_PATH = "outPutPath"

    /**
     * 图片宽度
     */
    const val EXTRA_IMAGE_WIDTH = "imageWidth"

    /**
     * 图片高度
     */
    const val EXTRA_IMAGE_HEIGHT = "imageHeight"

    /**
     * 图片X轴偏移量
     */
    const val EXTRA_OFFSET_X = "offsetX"

    /**
     * 图片Y轴偏移量
     */
    const val EXTRA_OFFSET_Y = "offsetY"

    /**
     * 图片旋转比例
     */
    const val EXTRA_ASPECT_RATIO = "aspectRatio"

    /**
     * uCrop的裁剪输出路径Key
     */
    const val EXTRA_OUTPUT_URI = "com.yalantis.ucrop.OutputUri"
}