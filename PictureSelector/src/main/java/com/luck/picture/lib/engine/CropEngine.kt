package com.luck.picture.lib.engine

import androidx.fragment.app.Fragment
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMedia
import java.util.ArrayList

/**
 * @author：luck
 * @date：2021/11/23 8:13 下午
 * Please Use [CropFileEngine]
 * @describe：CropEngine
 */
interface CropEngine {
    /**
     * Custom crop image engine
     *
     *
     * Users can implement this interface, and then access their own crop framework to plug
     * the crop path into the [LocalMedia] object;
     *
     *
     * 1、If Activity start crop use context;
     * activity.startActivityForResult([Crop.REQUEST_CROP])
     *
     *
     * 2、If Fragment start crop use fragment;
     * fragment.startActivityForResult([Crop.REQUEST_CROP])
     *
     *
     * 3、If you implement your own clipping function, you need to assign the following values in
     * Intent.putExtra [CustomIntentKey]
     *
     *
     *
     * @param fragment          Fragment
     * @param currentLocalMedia current crop data
     * @param dataSource        crop data
     * @param requestCode       Activity result code or fragment result code
     */
    fun onStartCrop(
        fragment: Fragment?,
        currentLocalMedia: LocalMedia?,
        dataSource: ArrayList<LocalMedia?>?,
        requestCode: Int
    )
}