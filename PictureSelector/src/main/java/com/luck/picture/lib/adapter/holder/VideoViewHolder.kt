package com.luck.picture.lib.adapter.holder

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.IntentUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.luck.picture.lib.R
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils
import com.luck.picture.lib.utils.StyleUtils

/**
 * @author：luck
 * @date：2021/11/20 3:59 下午
 * @describe：VideoViewHolder
 */
class VideoViewHolder(itemView: View, config: SelectorConfig) :
    BaseRecyclerMediaHolder(itemView, config) {
    private val tvDuration: TextView
    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        tvDuration.text = DateUtils.formatDurationTime(media.duration)
    }

    init {
        tvDuration = itemView.findViewById(R.id.tv_duration)
        val adapterStyle = selectorConfig!!.selectorStyle.selectMainStyle
        val drawableLeft = adapterStyle.adapterDurationDrawableLeft
        if (StyleUtils.checkStyleValidity(drawableLeft)) {
            tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableLeft, 0, 0, 0)
        }
        val textSize = adapterStyle.adapterDurationTextSize
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvDuration.textSize = textSize.toFloat()
        }
        val textColor = adapterStyle.adapterDurationTextColor
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvDuration.setTextColor(textColor)
        }
        val shadowBackground = adapterStyle.adapterDurationBackgroundResources
        if (StyleUtils.checkStyleValidity(shadowBackground)) {
            tvDuration.setBackgroundResource(shadowBackground)
        }
        val durationGravity = adapterStyle.adapterDurationGravity
        if (StyleUtils.checkArrayValidity(durationGravity)) {
            if (tvDuration.layoutParams is RelativeLayout.LayoutParams) {
                (tvDuration.layoutParams as RelativeLayout.LayoutParams).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                for (i in durationGravity) {
                    (tvDuration.layoutParams as RelativeLayout.LayoutParams).addRule(i)
                }
            }
        }
    }
}