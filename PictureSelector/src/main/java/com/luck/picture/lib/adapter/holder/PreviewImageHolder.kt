package com.luck.picture.lib.adapter.holder

import android.view.View
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.IntentUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia

/**
 * @author：luck
 * @date：2021/12/15 5:11 下午
 * @describe：PreviewImageHolder
 */
class PreviewImageHolder(itemView: View) : BasePreviewHolder(itemView) {
    override fun findViews(itemView: View?) {}
    override fun loadImage(media: LocalMedia, maxWidth: Int, maxHeight: Int) {
        if (selectorConfig.imageEngine != null) {
            val availablePath = media.availablePath
            if (maxWidth == PictureConfig.UNSET && maxHeight == PictureConfig.UNSET) {
                selectorConfig.imageEngine.loadImage(
                    itemView.context,
                    availablePath,
                    coverImageView
                )
            } else {
                selectorConfig.imageEngine.loadImage(
                    itemView.context,
                    coverImageView,
                    availablePath,
                    maxWidth,
                    maxHeight
                )
            }
        }
    }

    override fun onClickBackPressed() {
        coverImageView.setOnViewTapListener { view, x, y ->
            if (mPreviewEventListener != null) {
                mPreviewEventListener!!.onBackPressed()
            }
        }
    }

    override fun onLongPressDownload(media: LocalMedia?) {
        coverImageView.setOnLongClickListener {
            if (mPreviewEventListener != null) {
                mPreviewEventListener!!.onLongPressDownload(media)
            }
            false
        }
    }
}