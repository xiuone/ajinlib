package com.luck.picture.lib.basic

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
import androidx.fragment.app.FragmentManager
import com.luck.picture.lib.R
import com.luck.picture.lib.utils.ActivityCompatHelper

/**
 * @author：luck
 * @date：2021/12/6 1:28 下午
 * @describe：FragmentInjectManager
 */
object FragmentInjectManager {
    /**
     * inject fragment
     *
     * @param activity          root activity
     * @param targetFragmentTag fragment tag
     * @param targetFragment    target fragment
     */
    fun injectFragment(
        activity: FragmentActivity,
        targetFragmentTag: String?,
        targetFragment: Fragment?
    ) {
        if (ActivityCompatHelper.checkFragmentNonExits(activity, targetFragmentTag)) {
            activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, targetFragment!!, targetFragmentTag)
                .addToBackStack(targetFragmentTag)
                .commitAllowingStateLoss()
        }
    }

    /**
     * inject fragment
     *
     * @param fragmentManager   root activity FragmentManager
     * @param targetFragmentTag fragment tag
     * @param targetFragment    target fragment
     */
    fun injectSystemRoomFragment(
        fragmentManager: FragmentManager,
        targetFragmentTag: String?,
        targetFragment: Fragment?
    ) {
        fragmentManager.beginTransaction()
            .add(android.R.id.content, targetFragment!!, targetFragmentTag)
            .addToBackStack(targetFragmentTag)
            .commitAllowingStateLoss()
    }
}