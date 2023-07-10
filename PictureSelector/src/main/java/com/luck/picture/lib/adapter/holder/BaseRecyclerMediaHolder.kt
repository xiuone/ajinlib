package com.luck.picture.lib.adapter.holder

import android.content.Context
import android.graphics.ColorFilter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.IntentUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.PictureImageGridAdapter
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.manager.SelectedManager
import com.luck.picture.lib.utils.AnimUtils
import com.luck.picture.lib.utils.StyleUtils
import com.luck.picture.lib.utils.ValueOf

/**
 * @author：luck
 * @date：2021/11/20 3:17 下午
 * @describe：BaseRecyclerMediaHolder
 */
open class BaseRecyclerMediaHolder : RecyclerView.ViewHolder {
    var ivPicture: ImageView? = null
    var tvCheck: TextView? = null
    var btnCheck: View? = null
    var mContext: Context? = null
    var selectorConfig: SelectorConfig? = null
    var isSelectNumberStyle = false
    var isHandleMask = false
    private var defaultColorFilter: ColorFilter? = null
    private var selectColorFilter: ColorFilter? = null
    private var maskWhiteColorFilter: ColorFilter? = null

    constructor(itemView: View) : super(itemView) {}
    constructor(itemView: View, config: SelectorConfig) : super(itemView) {
        selectorConfig = config
        mContext = itemView.context
        defaultColorFilter = StyleUtils.getColorFilter(mContext, R.color.ps_color_20)
        selectColorFilter = StyleUtils.getColorFilter(mContext, R.color.ps_color_80)
        maskWhiteColorFilter = StyleUtils.getColorFilter(mContext, R.color.ps_color_half_white)
        val selectMainStyle = selectorConfig!!.selectorStyle.selectMainStyle
        isSelectNumberStyle = selectMainStyle.isSelectNumberStyle
        ivPicture = itemView.findViewById(R.id.ivPicture)
        tvCheck = itemView.findViewById(R.id.tvCheck)
        btnCheck = itemView.findViewById(R.id.btnCheck)
        if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
            tvCheck.setVisibility(View.GONE)
            btnCheck.setVisibility(View.GONE)
        } else {
            tvCheck.setVisibility(View.VISIBLE)
            btnCheck.setVisibility(View.VISIBLE)
        }
        isHandleMask = (!config.isDirectReturnSingle
                && (config.selectionMode == SelectModeConfig.SINGLE || config.selectionMode == SelectModeConfig.MULTIPLE))
        val textSize = selectMainStyle.adapterSelectTextSize
        if (StyleUtils.checkSizeValidity(textSize)) {
            tvCheck.setTextSize(textSize.toFloat())
        }
        val textColor = selectMainStyle.adapterSelectTextColor
        if (StyleUtils.checkStyleValidity(textColor)) {
            tvCheck.setTextColor(textColor)
        }
        val adapterSelectBackground = selectMainStyle.selectBackground
        if (StyleUtils.checkStyleValidity(adapterSelectBackground)) {
            tvCheck.setBackgroundResource(adapterSelectBackground)
        }
        val selectStyleGravity = selectMainStyle.adapterSelectStyleGravity
        if (StyleUtils.checkArrayValidity(selectStyleGravity)) {
            if (tvCheck.getLayoutParams() is RelativeLayout.LayoutParams) {
                (tvCheck.getLayoutParams() as RelativeLayout.LayoutParams).removeRule(RelativeLayout.ALIGN_PARENT_END)
                for (i in selectStyleGravity) {
                    (tvCheck.getLayoutParams() as RelativeLayout.LayoutParams).addRule(i)
                }
            }
            if (btnCheck.getLayoutParams() is RelativeLayout.LayoutParams) {
                (btnCheck.getLayoutParams() as RelativeLayout.LayoutParams).removeRule(
                    RelativeLayout.ALIGN_PARENT_END
                )
                for (i in selectStyleGravity) {
                    (btnCheck.getLayoutParams() as RelativeLayout.LayoutParams).addRule(i)
                }
            }
            val clickArea = selectMainStyle.adapterSelectClickArea
            if (StyleUtils.checkSizeValidity(clickArea)) {
                val clickAreaParams = btnCheck.getLayoutParams()
                clickAreaParams.width = clickArea
                clickAreaParams.height = clickArea
            }
        }
    }

    /**
     * bind Data
     *
     * @param media
     * @param position
     */
    open fun bindData(media: LocalMedia, position: Int) {
        media.position = adapterPosition
        selectedMedia(isSelected(media))
        if (isSelectNumberStyle) {
            notifySelectNumberStyle(media)
        }
        if (isHandleMask && selectorConfig!!.isMaxSelectEnabledMask) {
            dispatchHandleMask(media)
        }
        var path = media.path
        if (media.isEditorImage) {
            path = media.cutPath
        }
        loadCover(path)
        tvCheck!!.setOnClickListener { btnCheck!!.performClick() }
        btnCheck!!.setOnClickListener(View.OnClickListener {
            if (media.isMaxSelectEnabledMask || listener == null) {
                return@OnClickListener
            }
            val resultCode = listener!!.onSelected(tvCheck, position, media)
            if (resultCode == SelectedManager.INVALID) {
                return@OnClickListener
            }
            if (resultCode == SelectedManager.ADD_SUCCESS) {
                if (selectorConfig!!.isSelectZoomAnim) {
                    if (selectorConfig!!.onItemSelectAnimListener != null) {
                        selectorConfig!!.onItemSelectAnimListener.onSelectItemAnim(ivPicture, true)
                    } else {
                        AnimUtils.selectZoom(ivPicture)
                    }
                }
            } else if (resultCode == SelectedManager.REMOVE) {
                if (selectorConfig!!.isSelectZoomAnim) {
                    if (selectorConfig!!.onItemSelectAnimListener != null) {
                        selectorConfig!!.onItemSelectAnimListener.onSelectItemAnim(ivPicture, false)
                    }
                }
            }
            selectedMedia(isSelected(media))
        })
        itemView.setOnLongClickListener { v ->
            if (listener != null) {
                listener!!.onItemLongClick(v, position)
            }
            false
        }
        itemView.setOnClickListener(View.OnClickListener {
            if (media.isMaxSelectEnabledMask || listener == null) {
                return@OnClickListener
            }
            val isPreview =
                (PictureMimeType.isHasImage(media.mimeType) && selectorConfig!!.isEnablePreviewImage || selectorConfig!!.isDirectReturnSingle
                        || PictureMimeType.isHasVideo(media.mimeType) && (selectorConfig!!.isEnablePreviewVideo
                        || selectorConfig!!.selectionMode == SelectModeConfig.SINGLE) || PictureMimeType.isHasAudio(
                    media.mimeType
                ) && (selectorConfig!!.isEnablePreviewAudio
                        || selectorConfig!!.selectionMode == SelectModeConfig.SINGLE))
            if (isPreview) {
                listener!!.onItemClick(tvCheck, position, media)
            } else {
                btnCheck!!.performClick()
            }
        })
    }

    /**
     * 加载资源封面
     */
    protected open fun loadCover(path: String?) {
        if (selectorConfig!!.imageEngine != null) {
            selectorConfig!!.imageEngine.loadGridImage(ivPicture!!.context, path, ivPicture)
        }
    }

    /**
     * 处理到达选择条件后的蒙层效果
     */
    private fun dispatchHandleMask(media: LocalMedia) {
        var isEnabledMask = false
        if (selectorConfig!!.selectCount > 0 && !selectorConfig!!.selectedResult
                .contains(media)
        ) {
            if (selectorConfig!!.isWithVideoImage) {
                isEnabledMask =
                    if (selectorConfig!!.selectionMode == SelectModeConfig.SINGLE) {
                        selectorConfig!!.selectCount == Int.MAX_VALUE
                    } else {
                        selectorConfig!!.selectCount == selectorConfig!!.maxSelectNum
                    }
            } else {
                if (PictureMimeType.isHasVideo(selectorConfig!!.resultFirstMimeType)) {
                    val maxSelectNum: Int
                    maxSelectNum =
                        if (selectorConfig!!.selectionMode == SelectModeConfig.SINGLE) {
                            Int.MAX_VALUE
                        } else {
                            if (selectorConfig!!.maxVideoSelectNum > 0) selectorConfig!!.maxVideoSelectNum else selectorConfig!!.maxSelectNum
                        }
                    isEnabledMask = (selectorConfig!!.selectCount == maxSelectNum
                            || PictureMimeType.isHasImage(media.mimeType))
                } else {
                    val maxSelectNum: Int
                    maxSelectNum =
                        if (selectorConfig!!.selectionMode == SelectModeConfig.SINGLE) {
                            Int.MAX_VALUE
                        } else {
                            selectorConfig!!.maxSelectNum
                        }
                    isEnabledMask = (selectorConfig!!.selectCount == maxSelectNum
                            || PictureMimeType.isHasVideo(media.mimeType))
                }
            }
        }
        if (isEnabledMask) {
            ivPicture!!.colorFilter = maskWhiteColorFilter
            media.isMaxSelectEnabledMask = true
        } else {
            media.isMaxSelectEnabledMask = false
        }
    }

    /**
     * 设置选中缩放动画
     *
     * @param isChecked
     */
    private fun selectedMedia(isChecked: Boolean) {
        if (tvCheck!!.isSelected != isChecked) {
            tvCheck!!.isSelected = isChecked
        }
        if (selectorConfig!!.isDirectReturnSingle) {
            ivPicture!!.colorFilter = defaultColorFilter
        } else {
            ivPicture!!.colorFilter = if (isChecked) selectColorFilter else defaultColorFilter
        }
    }

    /**
     * 检查LocalMedia是否被选中
     *
     * @param currentMedia
     * @return
     */
    private fun isSelected(currentMedia: LocalMedia): Boolean {
        val selectedResult: List<LocalMedia> = selectorConfig!!.selectedResult
        val isSelected = selectedResult.contains(currentMedia)
        if (isSelected) {
            val compare = currentMedia.compareLocalMedia
            if (compare != null && compare.isEditorImage) {
                currentMedia.cutPath = compare.cutPath
                currentMedia.isCut = !TextUtils.isEmpty(compare.cutPath)
                currentMedia.isEditorImage = compare.isEditorImage
            }
        }
        return isSelected
    }

    /**
     * 对选择数量进行编号排序
     */
    private fun notifySelectNumberStyle(currentMedia: LocalMedia) {
        tvCheck!!.text = ""
        for (i in 0 until selectorConfig!!.selectCount) {
            val media = selectorConfig!!.selectedResult[i]
            if (TextUtils.equals(media.path, currentMedia.path)
                || media.id == currentMedia.id
            ) {
                currentMedia.num = media.num
                media.setPosition(currentMedia.getPosition())
                tvCheck!!.text = ValueOf.toString(currentMedia.num)
            }
        }
    }

    private var listener: PictureImageGridAdapter.OnItemClickListener? = null
    fun setOnItemClickListener(listener: PictureImageGridAdapter.OnItemClickListener?) {
        this.listener = listener
    }

    companion object {
        @JvmStatic
        fun generate(
            parent: ViewGroup,
            viewType: Int,
            resource: Int,
            config: SelectorConfig
        ): BaseRecyclerMediaHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(resource, parent, false)
            return when (viewType) {
                PictureImageGridAdapter.ADAPTER_TYPE_CAMERA -> CameraViewHolder(
                    itemView
                )
                PictureImageGridAdapter.ADAPTER_TYPE_VIDEO -> VideoViewHolder(
                    itemView,
                    config
                )
                PictureImageGridAdapter.ADAPTER_TYPE_AUDIO -> AudioViewHolder(
                    itemView,
                    config
                )
                else -> ImageViewHolder(itemView, config)
            }
        }
    }
}