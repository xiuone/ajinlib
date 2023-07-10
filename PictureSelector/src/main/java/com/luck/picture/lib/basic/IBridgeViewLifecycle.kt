package com.luck.picture.lib.basic

import android.os.Bundle
import android.view.View
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import com.luck.picture.lib.app.PictureAppMaster.appContext
import com.luck.picture.lib.app.PictureAppMaster.pictureSelectorEngine
import com.luck.picture.lib.PictureOnlyCameraFragment.Companion.newInstance
import com.luck.picture.lib.PictureOnlyCameraFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorPreviewFragment.setExternalPreviewData
import com.luck.picture.lib.PictureSelectorSystemFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorFragment.Companion.newInstance
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.FileDirMap
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * @author：luck
 * @date：2022/6/4 12:56 下午
 * @describe：IBridgeViewLifecycle
 */
interface IBridgeViewLifecycle {
    /**
     * onViewCreated
     *
     * @param fragment
     * @param view
     * @param savedInstanceState
     */
    fun onViewCreated(fragment: Fragment?, view: View?, savedInstanceState: Bundle?)

    /**
     * onDestroy
     *
     * @param fragment
     */
    fun onDestroy(fragment: Fragment?)
}