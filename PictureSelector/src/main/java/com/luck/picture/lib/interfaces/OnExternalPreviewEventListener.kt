package com.luck.picture.lib.interfaces

import android.content.Context
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2021/11/24 7:30 下午
 * @describe：OnExternalPreviewEventListener
 */
interface OnExternalPreviewEventListener {
    /**
     * 删除图片
     *
     * @param position 删除的下标
     */
    fun onPreviewDelete(position: Int)

    /**
     * 长按下载
     *
     * @param media 资源
     * @return false 自己实现下载逻辑；默认true
     */
    fun onLongPressDownload(context: Context?, media: LocalMedia?): Boolean
}