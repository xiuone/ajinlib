package com.luck.picture.lib.interfaces

import androidx.fragment.app.Fragment
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2021/11/27 5:44 下午
 * @describe：OnMediaEditInterceptListener
 */
interface OnMediaEditInterceptListener {
    /**
     * Custom crop image engine
     *
     *
     * Users can implement this interface, and then access their own crop framework to plug
     * the crop path into the [LocalMedia] object;
     *
     *
     *
     *
     *
     * 1、LocalMedia media = new LocalMedia();
     * media.setEditorImage(true);
     * media.setCut(true);
     * media.setCutPath("Your edit path"); or media.setCustomData("Your edit path");
     * or
     * media.setCustomData("Your custom data");
     *
     *
     *
     * If you implement your own Editing function function, you need to assign the following values in
     * Intent.putExtra() [CustomIntentKey.EXTRA_OUT_PUT_PATH]
     * Intent.putExtra() [CustomIntentKey.EXTRA_IMAGE_WIDTH]
     * Intent.putExtra() [CustomIntentKey.EXTRA_IMAGE_HEIGHT]
     * ... more [CustomIntentKey]
     *
     *
     * If you have customized additional data, please put it in Intent.putExtra()
     * [CustomIntentKey.EXTRA_CUSTOM_EXTRA_DATA]
     *
     *
     * @param fragment
     * @param currentLocalMedia current edit LocalMedia
     * @param requestCode       Activity or fragment result code
     */
    fun onStartMediaEdit(fragment: Fragment?, currentLocalMedia: LocalMedia?, requestCode: Int)
}