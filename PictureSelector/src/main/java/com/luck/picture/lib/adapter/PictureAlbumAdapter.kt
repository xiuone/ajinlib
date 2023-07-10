package com.luck.picture.lib.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.luck.picture.lib.config.InjectResourceSource
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectorConfig
import kotlin.jvm.JvmOverloads
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMediaFolder
import com.luck.picture.lib.interfaces.OnAlbumItemClickListener
import java.util.ArrayList

/**
 * @author：luck
 * @date：2016-12-11 17:02
 * @describe：PictureAlbumDirectoryAdapter
 */
class PictureAlbumAdapter(private val selectorConfig: SelectorConfig) :
    RecyclerView.Adapter<PictureAlbumAdapter.ViewHolder>() {
    private var albumList: List<LocalMediaFolder>? = null
    fun bindAlbumData(albumList: List<LocalMediaFolder>?) {
        this.albumList = ArrayList(albumList)
    }

    fun getAlbumList(): List<LocalMediaFolder> {
        return if (albumList != null) albumList!! else ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutResourceId = InjectResourceSource.getLayoutResource(
            parent.context,
            InjectResourceSource.ALBUM_ITEM_LAYOUT_RESOURCE,
            selectorConfig
        )
        val itemView = LayoutInflater.from(parent.context)
            .inflate(
                if (layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE) layoutResourceId else R.layout.ps_album_folder_item,
                parent,
                false
            )
        return ViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = albumList!![position]
        val name = folder.folderName
        val imageNum = folder.folderTotalNum
        val imagePath = folder.firstImagePath
        holder.tvSelectTag.visibility = if (folder.isSelectTag) View.VISIBLE else View.INVISIBLE
        val currentLocalMediaFolder = selectorConfig.currentLocalMediaFolder
        holder.itemView.isSelected = (currentLocalMediaFolder != null
                && folder.bucketId == currentLocalMediaFolder.bucketId)
        val firstMimeType = folder.firstMimeType
        if (PictureMimeType.isHasAudio(firstMimeType)) {
            holder.ivFirstImage.setImageResource(R.drawable.ps_audio_placeholder)
        } else {
            if (selectorConfig.imageEngine != null) {
                selectorConfig.imageEngine.loadAlbumCover(
                    holder.itemView.context,
                    imagePath, holder.ivFirstImage
                )
            }
        }
        val context = holder.itemView.context
        holder.tvFolderName.text = context.getString(R.string.ps_camera_roll_num, name, imageNum)
        holder.itemView.setOnClickListener(View.OnClickListener {
            if (onAlbumItemClickListener == null) {
                return@OnClickListener
            }
            onAlbumItemClickListener!!.onItemClick(position, folder)
        })
    }

    override fun getItemCount(): Int {
        return albumList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFirstImage: ImageView
        var tvFolderName: TextView
        var tvSelectTag: TextView

        init {
            ivFirstImage = itemView.findViewById(R.id.first_image)
            tvFolderName = itemView.findViewById(R.id.tv_folder_name)
            tvSelectTag = itemView.findViewById(R.id.tv_select_tag)
            val selectorStyle = selectorConfig.selectorStyle
            val albumWindowStyle = selectorStyle.albumWindowStyle
            val itemBackground = albumWindowStyle.albumAdapterItemBackground
            if (itemBackground != 0) {
                itemView.setBackgroundResource(itemBackground)
            }
            val itemSelectStyle = albumWindowStyle.albumAdapterItemSelectStyle
            if (itemSelectStyle != 0) {
                tvSelectTag.setBackgroundResource(itemSelectStyle)
            }
            val titleColor = albumWindowStyle.albumAdapterItemTitleColor
            if (titleColor != 0) {
                tvFolderName.setTextColor(titleColor)
            }
            val titleSize = albumWindowStyle.albumAdapterItemTitleSize
            if (titleSize > 0) {
                tvFolderName.textSize = titleSize.toFloat()
            }
        }
    }

    private var onAlbumItemClickListener: OnAlbumItemClickListener? = null

    /**
     * 专辑列表桥接类
     *
     * @param listener
     */
    fun setOnIBridgeAlbumWidget(listener: OnAlbumItemClickListener?) {
        onAlbumItemClickListener = listener
    }
}