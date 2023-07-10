package com.luck.picture.lib.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
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
import com.luck.picture.lib.adapter.holder.BaseRecyclerMediaHolder
import com.luck.picture.lib.config.InjectResourceSource
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.JvmOverloads
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMedia
import java.util.ArrayList

/**
 * @author：luck
 * @date：2016-12-30 12:02
 * @describe：PictureImageGridAdapter
 */
class PictureImageGridAdapter(private val mContext: Context, private val mConfig: SelectorConfig) :
    RecyclerView.Adapter<BaseRecyclerMediaHolder>() {
    var isDisplayCamera = false
    var data = ArrayList<LocalMedia>()
        private set

    fun notifyItemPositionChanged(position: Int) {
        this.notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataAndDataSetChanged(result: ArrayList<LocalMedia>?) {
        if (result != null) {
            data = result
            notifyDataSetChanged()
        }
    }

    val isDataEmpty: Boolean
        get() = data.size == 0

    override fun getItemViewType(position: Int): Int {
        return if (isDisplayCamera && position == 0) {
            ADAPTER_TYPE_CAMERA
        } else {
            val adapterPosition = if (isDisplayCamera) position - 1 else position
            val mimeType = data[adapterPosition].mimeType
            if (PictureMimeType.isHasVideo(mimeType)) {
                return ADAPTER_TYPE_VIDEO
            } else if (PictureMimeType.isHasAudio(mimeType)) {
                return ADAPTER_TYPE_AUDIO
            }
            ADAPTER_TYPE_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerMediaHolder {
        return generate(parent, viewType, getItemResourceId(viewType), mConfig)
    }

    /**
     * getItemResourceId
     *
     * @param viewType
     * @return
     */
    private fun getItemResourceId(viewType: Int): Int {
        val layoutResourceId: Int
        return when (viewType) {
            ADAPTER_TYPE_CAMERA -> R.layout.ps_item_grid_camera
            ADAPTER_TYPE_VIDEO -> {
                layoutResourceId = InjectResourceSource.getLayoutResource(
                    mContext,
                    InjectResourceSource.MAIN_ITEM_VIDEO_LAYOUT_RESOURCE,
                    mConfig
                )
                if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) layoutResourceId else R.layout.ps_item_grid_video
            }
            ADAPTER_TYPE_AUDIO -> {
                layoutResourceId = InjectResourceSource.getLayoutResource(
                    mContext,
                    InjectResourceSource.MAIN_ITEM_AUDIO_LAYOUT_RESOURCE,
                    mConfig
                )
                if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) layoutResourceId else R.layout.ps_item_grid_audio
            }
            else -> {
                layoutResourceId = InjectResourceSource.getLayoutResource(
                    mContext,
                    InjectResourceSource.MAIN_ITEM_IMAGE_LAYOUT_RESOURCE,
                    mConfig
                )
                if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) layoutResourceId else R.layout.ps_item_grid_image
            }
        }
    }

    override fun onBindViewHolder(holder: BaseRecyclerMediaHolder, position: Int) {
        if (getItemViewType(position) == ADAPTER_TYPE_CAMERA) {
            holder.itemView.setOnClickListener {
                if (listener != null) {
                    listener!!.openCameraClick()
                }
            }
        } else {
            val adapterPosition = if (isDisplayCamera) position - 1 else position
            val media = data[adapterPosition]
            holder.bindData(media, adapterPosition)
            holder.setOnItemClickListener(listener)
        }
    }

    override fun getItemCount(): Int {
        return if (isDisplayCamera) data.size + 1 else data.size
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    interface OnItemClickListener {
        /**
         * 拍照
         */
        fun openCameraClick()

        /**
         * 列表item点击事件
         *
         * @param selectedView 所产生点击事件的View
         * @param position     当前下标
         * @param media        当前LocalMedia对象
         */
        fun onItemClick(selectedView: View?, position: Int, media: LocalMedia?)

        /**
         * 列表item长按事件
         *
         * @param itemView
         * @param position
         */
        fun onItemLongClick(itemView: View?, position: Int)

        /**
         * 列表勾选点击事件
         *
         * @param selectedView 所产生点击事件的View
         * @param position     当前下标
         * @param media        当前LocalMedia对象
         */
        fun onSelected(selectedView: View?, position: Int, media: LocalMedia?): Int
    }

    companion object {
        /**
         * 拍照
         */
        const val ADAPTER_TYPE_CAMERA = 1

        /**
         * 图片
         */
        const val ADAPTER_TYPE_IMAGE = 2

        /**
         * 视频
         */
        const val ADAPTER_TYPE_VIDEO = 3

        /**
         * 音频
         */
        const val ADAPTER_TYPE_AUDIO = 4
    }
}