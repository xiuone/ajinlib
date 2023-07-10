package com.luck.picture.lib.config

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.Synchronized
import com.luck.picture.lib.utils.FileDirMap
import kotlin.jvm.Volatile
import com.luck.picture.lib.config.SelectorProviders

/**
 * @author：luck
 * @date：2021/11/28 3:47 下午
 * @describe：Crop
 */
object Crop {
    /**
     *
     *
     * 这里都是对应的UCrop库的UCrop类的Key
     * (https://github.com/Yalantis/uCrop/blob/develop/ucrop/src/main/java/com/yalantis/ucrop/UCrop.java)
     * 如果你使用的是PictureSelector自带的裁剪库，此处不要乱改 ！！！
     *
     */
    const val REQUEST_EDIT_CROP = 696
    const val REQUEST_CROP = 69
    const val RESULT_CROP_ERROR = 96
    private const val EXTRA_PREFIX = "com.yalantis.ucrop"
    const val EXTRA_OUTPUT_CROP_ASPECT_RATIO = EXTRA_PREFIX + ".CropAspectRatio"
    const val EXTRA_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".ImageWidth"
    const val EXTRA_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".ImageHeight"
    const val EXTRA_OUTPUT_OFFSET_X = EXTRA_PREFIX + ".OffsetX"
    const val EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY"
    const val EXTRA_ERROR = EXTRA_PREFIX + ".Error"

    /**
     * Retrieve cropped image Uri from the result Intent
     *
     * @param intent crop result intent
     */
    fun getOutput(intent: Intent): Uri? {
        var outputUri = intent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
        if (outputUri == null) {
            outputUri = intent.getParcelableExtra(CustomIntentKey.EXTRA_OUTPUT_URI)
        }
        return outputUri
    }

    /**
     * custom extra data
     *
     * @param intent crop result intent
     */
    fun getOutputCustomExtraData(intent: Intent): String? {
        return intent.getStringExtra(CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA)
    }

    /**
     * Retrieve the width of the cropped image
     *
     * @param intent crop result intent
     */
    fun getOutputImageWidth(intent: Intent): Int {
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_WIDTH, -1)
    }

    /**
     * Retrieve the height of the cropped image
     *
     * @param intent crop result intent
     */
    fun getOutputImageHeight(intent: Intent): Int {
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_HEIGHT, -1)
    }

    /**
     * Retrieve cropped image aspect ratio from the result Intent
     *
     * @param intent crop result intent
     * @return aspect ratio as a floating point value (x:y) - so it will be 1 for 1:1 or 4/3 for 4:3
     */
    fun getOutputCropAspectRatio(intent: Intent): Float {
        return intent.getFloatExtra(EXTRA_OUTPUT_CROP_ASPECT_RATIO, 0f)
    }

    /**
     * Retrieve the x of the cropped offset x
     *
     * @param intent crop result intent
     */
    fun getOutputImageOffsetX(intent: Intent): Int {
        return intent.getIntExtra(EXTRA_OUTPUT_OFFSET_X, 0)
    }

    /**
     * Retrieve the y of the cropped offset y
     *
     * @param intent crop result intent
     */
    fun getOutputImageOffsetY(intent: Intent): Int {
        return intent.getIntExtra(EXTRA_OUTPUT_OFFSET_Y, 0)
    }

    /**
     * Method retrieves error from the result intent.
     *
     * @param result crop result Intent
     * @return Throwable that could happen while image processing
     */
    fun getError(result: Intent): Throwable? {
        return result.getSerializableExtra(EXTRA_ERROR) as Throwable?
    }
}