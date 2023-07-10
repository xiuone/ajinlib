package com.luck.picture.lib.engine

import android.net.Uri
import androidx.fragment.app.Fragment
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import java.util.ArrayList

/**
 * @author：luck
 * @date：2021/11/23 8:13 下午
 * @describe：CropFileEngine
 */
interface CropFileEngine {
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
     * @param fragment       Fragment
     * @param srcUri         current src Uri
     * @param destinationUri current output src Uri
     * @param dataSource     crop data
     * @param requestCode    Activity result code or fragment result code
     */
    fun onStartCrop(
        fragment: Fragment?,
        srcUri: Uri?,
        destinationUri: Uri?,
        dataSource: ArrayList<String?>?,
        requestCode: Int
    )
}