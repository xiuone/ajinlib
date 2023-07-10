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

/**
 * @author：luck
 * @date：2021/11/18 8:35 下午
 * @describe：IPictureSelectorEvent
 */
interface IPictureSelectorEvent {
    /**
     * 获取相册目录
     */
    fun loadAllAlbumData()

    /**
     * 获取首页资源
     */
    fun loadFirstPageMediaData(firstBucketId: Long)

    /**
     * 加载应用沙盒内的资源
     */
    fun loadOnlyInAppDirectoryAllMediaData()

    /**
     * 加载更多
     */
    fun loadMoreMediaData()
}