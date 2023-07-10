package com.luck.picture.lib.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.IntentUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.luck.picture.lib.R
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.StyleUtils

/**
 * @author：luck
 * @date：2021/11/20 3:59 下午
 * @describe：ImageViewHolder
 */
class ImageViewHolder(itemView: View, config: SelectorConfig) :
    BaseRecyclerMediaHolder(itemView, config) {
    private val ivEditor: ImageView
    private val tvMediaTag: TextView
    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        if (media.isEditorImage && media.isCut) {
            ivEditor.visibility = View.VISIBLE
        } else {
            ivEditor.visibility = View.GONE
        }
        tvMediaTag.visibility = View.VISIBLE
        if (PictureMimeType.isHasGif(media.mimeType)) {
            tvMediaTag.text = mContext!!.getString(R.string.ps_gif_tag)
        } else if (PictureMimeType.isHasWebp(media.mimeType)) {
            tvMediaTag.text = mContext!!.getString(R.string.ps_webp_tag)
        } else if (MediaUtils.isLongImage(media.width, media.height)) {
            tvMediaTag.text = mContext!!.getString(R.string.ps_long_chart)
        } else {
            tvMediaTag.visibility = View.GONE
        }
    }

    init {
        tvMediaTag = itemView.findViewById(R.id.tv_media_tag)
        ivEditor = itemView.findViewById(R.id.ivEditor)
        val adapterStyle = selectorConfig!!.selectorStyle.selectMainStyle
        val imageEditorRes = adapterStyle.adapterImageEditorResources
        if (StyleUtils.checkStyleValidity(imageEditorRes)) {
            ivEditor.setImageResource(imageEditorRes)
        }
        val editorGravity = adapterStyle.adapterImageEditorGravity
        if (StyleUtils.checkArrayValidity(editorGravity)) {
            if (ivEditor.layoutParams is RelativeLayout.LayoutParams) {
                (ivEditor.layoutParams as RelativeLayout.LayoutParams).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                for (i in editorGravity) {
                    (ivEditor.layoutParams as RelativeLayout.LayoutParams).addRule(i)
                }
            }
        }
        val tagGravity = adapterStyle.adapterTagGravity
        if (StyleUtils.checkArrayValidity(tagGravity)) {
            if (tvMediaTag.layoutParams is RelativeLayout.LayoutParams) {
                (tvMediaTag.layoutParams as RelativeLayout.LayoutParams).removeRule(RelativeLayout.ALIGN_PARENT_END)
                (tvMediaTag.layoutParams as RelativeLayout.LayoutParams).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                for (i in tagGravity) {
                    (tvMediaTag.layoutParams as RelativeLayout.LayoutParams).addRule(i)
                }
            }
        }
        val background = adapterStyle.adapterTagBackgroundResources
        if (StyleUtils.checkStyleValidity(background)) {
            tvMediaTag.setBackgroundResource(background)
        }
        val textSize = adapterStyle.adapterTagTextSize
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvMediaTag.textSize = textSize.toFloat()
        }
        val textColor = adapterStyle.adapterTagTextColor
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvMediaTag.setTextColor(textColor)
        }
    }
}