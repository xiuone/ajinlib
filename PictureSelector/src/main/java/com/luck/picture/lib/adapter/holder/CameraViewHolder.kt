package com.luck.picture.lib.adapter.holder

import android.view.View
import android.widget.TextView
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.IntentUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.luck.picture.lib.R
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.utils.StyleUtils

/**
 * @author：luck
 * @date：2021/11/20 3:54 下午
 * @describe：CameraViewHolder
 */
class CameraViewHolder(itemView: View) : BaseRecyclerMediaHolder(itemView) {
    init {
        val tvCamera = itemView.findViewById<TextView>(R.id.tvCamera)
        selectorConfig = SelectorProviders.instance.selectorConfig
        val adapterStyle = selectorConfig.selectorStyle.selectMainStyle
        val background = adapterStyle.adapterCameraBackgroundColor
        if (StyleUtils.checkStyleValidity(background)) {
            tvCamera.setBackgroundColor(background)
        }
        val drawableTop = adapterStyle.adapterCameraDrawableTop
        if (StyleUtils.checkStyleValidity(drawableTop)) {
            tvCamera.setCompoundDrawablesRelativeWithIntrinsicBounds(0, drawableTop, 0, 0)
        }
        val text =
            if (StyleUtils.checkStyleValidity(adapterStyle.adapterCameraTextResId)) itemView.context.getString(
                adapterStyle.adapterCameraTextResId
            ) else adapterStyle.adapterCameraText
        if (StyleUtils.checkTextValidity(text)) {
            tvCamera.text = text
        } else {
            if (selectorConfig.chooseMode == SelectMimeType.ofAudio()) {
                tvCamera.text = itemView.context.getString(R.string.ps_tape)
            }
        }
        val textSize = adapterStyle.adapterCameraTextSize
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvCamera.textSize = textSize.toFloat()
        }
        val textColor = adapterStyle.adapterCameraTextColor
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvCamera.setTextColor(textColor)
        }
    }
}