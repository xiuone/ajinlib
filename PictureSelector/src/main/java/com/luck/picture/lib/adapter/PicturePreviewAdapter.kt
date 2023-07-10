package com.luck.picture.lib.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.holder.BasePreviewHolder
import com.luck.picture.lib.adapter.holder.BaseRecyclerMediaHolder.Companion.generate
import com.luck.picture.lib.adapter.holder.BaseRecyclerMediaHolder.bindData
import com.luck.picture.lib.adapter.holder.BaseRecyclerMediaHolder.setOnItemClickListener
import com.luck.picture.lib.adapter.holder.BasePreviewHolder.Companion.generate
import com.luck.picture.lib.adapter.holder.BasePreviewHolder.setOnPreviewEventListener
import com.luck.picture.lib.adapter.holder.BasePreviewHolder.bindData
import com.luck.picture.lib.adapter.holder.BasePreviewHolder.onViewAttachedToWindow
import com.luck.picture.lib.adapter.holder.BasePreviewHolder.onViewDetachedFromWindow
import com.luck.picture.lib.adapter.holder.PreviewVideoHolder.isPlaying
import com.luck.picture.lib.adapter.holder.PreviewVideoHolder.startPlay
import com.luck.picture.lib.adapter.holder.BasePreviewHolder.isPlaying
import com.luck.picture.lib.adapter.holder.BasePreviewHolder.release
import com.luck.picture.lib.adapter.holder.PreviewVideoHolder
import com.luck.picture.lib.config.InjectResourceSource
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.JvmOverloads
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMedia
import java.util.LinkedHashMap

/**
 * @author：luck
 * @date：2021/11/23 1:11 下午
 * @describe：PicturePreviewAdapter2
 */
class PicturePreviewAdapter @JvmOverloads constructor(private val selectorConfig: SelectorConfig = SelectorProviders.instance.selectorConfig) :
    RecyclerView.Adapter<BasePreviewHolder>() {
    private var mData: List<LocalMedia>? = null
    private var onPreviewEventListener: BasePreviewHolder.OnPreviewEventListener? = null
    private val mHolderCache = LinkedHashMap<Int, BasePreviewHolder>()
    fun getCurrentHolder(position: Int): BasePreviewHolder? {
        return mHolderCache[position]
    }

    fun setData(list: List<LocalMedia>?) {
        mData = list
    }

    fun setOnPreviewEventListener(listener: BasePreviewHolder.OnPreviewEventListener?) {
        onPreviewEventListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePreviewHolder {
        val layoutResourceId: Int
        return if (viewType == BasePreviewHolder.ADAPTER_TYPE_VIDEO) {
            layoutResourceId = InjectResourceSource.getLayoutResource(
                parent.context,
                InjectResourceSource.PREVIEW_ITEM_VIDEO_LAYOUT_RESOURCE,
                selectorConfig
            )
            generate(
                parent,
                viewType,
                if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) layoutResourceId else R.layout.ps_preview_video
            )
        } else if (viewType == BasePreviewHolder.ADAPTER_TYPE_AUDIO) {
            layoutResourceId = InjectResourceSource.getLayoutResource(
                parent.context,
                InjectResourceSource.PREVIEW_ITEM_AUDIO_LAYOUT_RESOURCE,
                selectorConfig
            )
            generate(
                parent,
                viewType,
                if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) layoutResourceId else R.layout.ps_preview_audio
            )
        } else {
            layoutResourceId = InjectResourceSource.getLayoutResource(
                parent.context,
                InjectResourceSource.PREVIEW_ITEM_IMAGE_LAYOUT_RESOURCE,
                selectorConfig
            )
            generate(
                parent,
                viewType,
                if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) layoutResourceId else R.layout.ps_preview_image
            )
        }
    }

    override fun onBindViewHolder(holder: BasePreviewHolder, position: Int) {
        holder.setOnPreviewEventListener(onPreviewEventListener)
        val media = getItem(position)
        mHolderCache[position] = holder
        holder.bindData(media!!, position)
    }

    fun getItem(position: Int): LocalMedia? {
        return if (position > mData!!.size) {
            null
        } else mData!![position]
    }

    override fun getItemViewType(position: Int): Int {
        return if (PictureMimeType.isHasVideo(mData!![position].mimeType)) {
            BasePreviewHolder.ADAPTER_TYPE_VIDEO
        } else if (PictureMimeType.isHasAudio(mData!![position].mimeType)) {
            BasePreviewHolder.ADAPTER_TYPE_AUDIO
        } else {
            BasePreviewHolder.ADAPTER_TYPE_IMAGE
        }
    }

    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }

    override fun onViewAttachedToWindow(holder: BasePreviewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onViewAttachedToWindow()
    }

    override fun onViewDetachedFromWindow(holder: BasePreviewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
    }

    /**
     * 设置封面的缩放方式
     *
     * @param position
     */
    fun setCoverScaleType(position: Int) {
        val currentHolder = getCurrentHolder(position)
        if (currentHolder != null) {
            val media = getItem(position)
            if (media!!.width == 0 && media.height == 0) {
                currentHolder.coverImageView.scaleType = ImageView.ScaleType.FIT_CENTER
            } else {
                currentHolder.coverImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    /**
     * 设置播放按钮状态
     *
     * @param position
     */
    fun setVideoPlayButtonUI(position: Int) {
        val currentHolder = getCurrentHolder(position)
        if (currentHolder is PreviewVideoHolder) {
            val videoHolder = currentHolder
            if (!videoHolder.isPlaying) {
                videoHolder.ivPlayButton.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 设置自动播放视频
     *
     * @param position
     */
    fun startAutoVideoPlay(position: Int) {
        val currentHolder = getCurrentHolder(position)
        if (currentHolder is PreviewVideoHolder) {
            currentHolder.startPlay()
        }
    }

    /**
     * isPlaying
     *
     * @param position
     * @return
     */
    fun isPlaying(position: Int): Boolean {
        val currentHolder = getCurrentHolder(position)
        return currentHolder != null && currentHolder.isPlaying
    }

    /**
     * 释放当前视频相关
     */
    fun destroy() {
        for (key in mHolderCache.keys) {
            val holder = mHolderCache[key]
            holder?.release()
        }
    }
}