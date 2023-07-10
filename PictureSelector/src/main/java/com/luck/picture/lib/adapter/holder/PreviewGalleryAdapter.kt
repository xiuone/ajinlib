package com.luck.picture.lib.adapter.holder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.IntentUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.luck.picture.lib.config.InjectResourceSource
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.StyleUtils
import java.util.ArrayList

/**
 * @author：luck
 * @date：2019-11-30 20:50
 * @describe：preview gallery
 */
class PreviewGalleryAdapter(
    private val selectorConfig: SelectorConfig,
    private val isBottomPreview: Boolean
) : RecyclerView.Adapter<PreviewGalleryAdapter.ViewHolder>() {
    private val mData: MutableList<LocalMedia>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResourceId = InjectResourceSource.getLayoutResource(
            parent.context,
            InjectResourceSource.PREVIEW_GALLERY_ITEM_LAYOUT_RESOURCE, selectorConfig
        )
        val itemView = LayoutInflater.from(parent.context)
            .inflate(
                if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) layoutResourceId else R.layout.ps_preview_gallery_item,
                parent,
                false
            )
        return ViewHolder(itemView)
    }

    val data: List<LocalMedia>
        get() = mData

    fun clear() {
        mData.clear()
    }

    /**
     * 添加选中的至画廊效果里
     *
     * @param currentMedia
     */
    fun addGalleryData(currentMedia: LocalMedia) {
        val lastCheckPosition = lastCheckPosition
        if (lastCheckPosition != RecyclerView.NO_POSITION) {
            val lastSelectedMedia = mData[lastCheckPosition]
            lastSelectedMedia.isChecked = false
            notifyItemChanged(lastCheckPosition)
        }
        if (isBottomPreview && mData.contains(currentMedia)) {
            val currentPosition = getCurrentPosition(currentMedia)
            val media = mData[currentPosition]
            media.isGalleryEnabledMask = false
            media.isChecked = true
            notifyItemChanged(currentPosition)
        } else {
            currentMedia.isChecked = true
            mData.add(currentMedia)
            notifyItemChanged(mData.size - 1)
        }
    }

    /**
     * 移除画廊中未选中的结果
     *
     * @param currentMedia
     */
    fun removeGalleryData(currentMedia: LocalMedia) {
        val currentPosition = getCurrentPosition(currentMedia)
        if (currentPosition != RecyclerView.NO_POSITION) {
            if (isBottomPreview) {
                val media = mData[currentPosition]
                media.isGalleryEnabledMask = true
                notifyItemChanged(currentPosition)
            } else {
                mData.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
            }
        }
    }

    /**
     * 当前LocalMedia是否选中
     *
     * @param currentMedia
     */
    fun isSelectMedia(currentMedia: LocalMedia) {
        val lastCheckPosition = lastCheckPosition
        if (lastCheckPosition != RecyclerView.NO_POSITION) {
            val lastSelectedMedia = mData[lastCheckPosition]
            lastSelectedMedia.isChecked = false
            notifyItemChanged(lastCheckPosition)
        }
        val currentPosition = getCurrentPosition(currentMedia)
        if (currentPosition != RecyclerView.NO_POSITION) {
            val media = mData[currentPosition]
            media.isChecked = true
            notifyItemChanged(currentPosition)
        }
    }

    /**
     * 获取画廊上一次选中的位置
     *
     * @return
     */
    val lastCheckPosition: Int
        get() {
            for (i in mData.indices) {
                val media = mData[i]
                if (media.isChecked) {
                    return i
                }
            }
            return RecyclerView.NO_POSITION
        }

    /**
     * 获取当前画廊LocalMedia的位置
     *
     * @param currentMedia
     * @return
     */
    private fun getCurrentPosition(currentMedia: LocalMedia): Int {
        for (i in mData.indices) {
            val media = mData[i]
            if (TextUtils.equals(media.path, currentMedia.path)
                || media.id == currentMedia.id
            ) {
                return i
            }
        }
        return RecyclerView.NO_POSITION
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        val colorFilter = StyleUtils.getColorFilter(
            holder.itemView.context,
            if (item.isGalleryEnabledMask) R.color.ps_color_half_white else R.color.ps_color_transparent
        )
        if (item.isChecked && item.isGalleryEnabledMask) {
            holder.viewBorder.visibility = View.VISIBLE
        } else {
            holder.viewBorder.visibility = if (item.isChecked) View.VISIBLE else View.GONE
        }
        var path = item.path
        if (item.isEditorImage && !TextUtils.isEmpty(item.cutPath)) {
            path = item.cutPath
            holder.ivEditor.visibility = View.VISIBLE
        } else {
            holder.ivEditor.visibility = View.GONE
        }
        holder.ivImage.colorFilter = colorFilter
        if (selectorConfig.imageEngine != null) {
            selectorConfig.imageEngine.loadGridImage(holder.itemView.context, path, holder.ivImage)
        }
        holder.ivPlay.visibility =
            if (PictureMimeType.isHasVideo(item.mimeType)) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener { view ->
            if (listener != null) {
                listener!!.onItemClick(holder.adapterPosition, item, view)
            }
        }
        holder.itemView.setOnLongClickListener { v ->
            if (mItemLongClickListener != null) {
                val adapterPosition = holder.adapterPosition
                mItemLongClickListener!!.onItemLongClick(holder, adapterPosition, v)
            }
            true
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivImage: ImageView
        var ivPlay: ImageView
        var ivEditor: ImageView
        var viewBorder: View

        init {
            ivImage = itemView.findViewById(R.id.ivImage)
            ivPlay = itemView.findViewById(R.id.ivPlay)
            ivEditor = itemView.findViewById(R.id.ivEditor)
            viewBorder = itemView.findViewById(R.id.viewBorder)
            val selectMainStyle = selectorConfig.selectorStyle.selectMainStyle
            if (StyleUtils.checkStyleValidity(selectMainStyle.adapterImageEditorResources)) {
                ivEditor.setImageResource(selectMainStyle.adapterImageEditorResources)
            }
            if (StyleUtils.checkStyleValidity(selectMainStyle.adapterPreviewGalleryFrameResource)) {
                viewBorder.setBackgroundResource(selectMainStyle.adapterPreviewGalleryFrameResource)
            }
            val adapterPreviewGalleryItemSize = selectMainStyle.adapterPreviewGalleryItemSize
            if (StyleUtils.checkSizeValidity(adapterPreviewGalleryItemSize)) {
                val params = RelativeLayout.LayoutParams(
                    adapterPreviewGalleryItemSize,
                    adapterPreviewGalleryItemSize
                )
                itemView.layoutParams = params
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private var listener: OnItemClickListener? = null
    fun setItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, media: LocalMedia?, v: View?)
    }

    private var mItemLongClickListener: OnItemLongClickListener? = null
    fun setItemLongClickListener(listener: OnItemLongClickListener?) {
        mItemLongClickListener = listener
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(holder: RecyclerView.ViewHolder?, position: Int, v: View?)
    }

    init {
        mData = ArrayList(selectorConfig.selectedResult)
        for (i in mData.indices) {
            val media = mData[i]
            media.isGalleryEnabledMask = false
            media.isChecked = false
        }
    }
}