package com.luck.picture.lib.basic


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