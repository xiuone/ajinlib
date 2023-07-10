package com.yalantis.ucrop

import android.graphics.ColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2016-12-31 22:22
 * @describe：UCropGalleryAdapter
 */
class UCropGalleryAdapter constructor(private val list: List<String>?) :
    RecyclerView.Adapter<UCropGalleryAdapter.ViewHolder>() {
    var currentSelectPosition: Int = 0
    public override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.ucrop_gallery_adapter_item,
            parent, false
        )
        return ViewHolder(view)
    }

    public override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path: String = list!!.get(position)
        if (UCropDevelopConfig.imageEngine != null) {
            UCropDevelopConfig.imageEngine!!.loadImage(
                holder.itemView.getContext(),
                path,
                holder.mIvPhoto
            )
        }
        val colorFilter: ColorFilter?
        if (currentSelectPosition == position) {
            holder.mViewCurrentSelect.setVisibility(View.VISIBLE)
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.ucrop_color_80),
                BlendModeCompat.SRC_ATOP
            )
        } else {
            colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.ucrop_color_20),
                BlendModeCompat.SRC_ATOP
            )
            holder.mViewCurrentSelect.setVisibility(View.GONE)
        }
        holder.mIvPhoto.setColorFilter(colorFilter)
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if (listener != null) {
                    listener!!.onItemClick(holder.getAdapterPosition(), v)
                }
            }
        })
    }

    public override fun getItemCount(): Int {
        return if (list != null) list.size else 0
    }

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        var mIvPhoto: ImageView
        var mViewCurrentSelect: View

        init {
            mIvPhoto = view.findViewById(R.id.iv_photo)
            mViewCurrentSelect = view.findViewById(R.id.view_current_select)
        }
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    open interface OnItemClickListener {
        fun onItemClick(position: Int, view: View?)
    }
}