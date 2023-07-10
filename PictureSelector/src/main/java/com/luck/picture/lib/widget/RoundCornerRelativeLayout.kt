package com.luck.picture.lib.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig.selectCount
import com.luck.picture.lib.style.PictureSelectorStyle.bottomBarStyle
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalDrawableLeft
import com.luck.picture.lib.utils.StyleUtils.checkStyleValidity
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalTextResId
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalText
import com.luck.picture.lib.utils.StyleUtils.checkTextValidity
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalTextSize
import com.luck.picture.lib.utils.StyleUtils.checkSizeValidity
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomNarBarHeight
import com.luck.picture.lib.utils.DensityUtil.dip2px
import com.luck.picture.lib.style.BottomNavBarStyle.bottomNarBarBackgroundColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNormalTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNormalTextSize
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNormalTextResId
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNormalText
import com.luck.picture.lib.style.BottomNavBarStyle.bottomEditorTextResId
import com.luck.picture.lib.style.BottomNavBarStyle.bottomEditorText
import com.luck.picture.lib.style.BottomNavBarStyle.bottomEditorTextSize
import com.luck.picture.lib.style.BottomNavBarStyle.bottomEditorTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewSelectTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewSelectTextResId
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewSelectText
import com.luck.picture.lib.utils.StyleUtils.checkTextFormatValidity
import com.luck.picture.lib.config.SelectorConfig.selectedResult
import com.luck.picture.lib.utils.PictureFileUtils.formatAccurateUnitFileSize
import com.luck.picture.lib.style.PictureSelectorStyle.selectMainStyle
import com.luck.picture.lib.style.SelectMainStyle.selectNormalBackgroundResources
import com.luck.picture.lib.style.SelectMainStyle.selectNormalTextResId
import com.luck.picture.lib.style.SelectMainStyle.selectNormalText
import com.luck.picture.lib.utils.StyleUtils.checkTextTwoFormatValidity
import com.luck.picture.lib.style.SelectMainStyle.selectNormalTextSize
import com.luck.picture.lib.style.SelectMainStyle.selectNormalTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.isCompleteCountTips
import com.luck.picture.lib.style.BottomNavBarStyle.bottomSelectNumResources
import com.luck.picture.lib.style.BottomNavBarStyle.bottomSelectNumTextSize
import com.luck.picture.lib.style.BottomNavBarStyle.bottomSelectNumTextColor
import com.luck.picture.lib.style.SelectMainStyle.selectBackgroundResources
import com.luck.picture.lib.style.SelectMainStyle.selectTextResId
import com.luck.picture.lib.style.SelectMainStyle.selectText
import com.luck.picture.lib.style.SelectMainStyle.selectTextSize
import com.luck.picture.lib.style.SelectMainStyle.selectTextColor
import com.luck.picture.lib.utils.ValueOf.toString
import com.luck.picture.lib.interfaces.OnSelectAnimListener.onSelectAnim
import com.luck.picture.lib.style.SelectMainStyle.isCompleteSelectRelativeTop
import com.luck.picture.lib.config.PictureMimeType.isContent
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNarBarBackgroundColor
import com.luck.picture.lib.style.PictureSelectorStyle.titleBarStyle
import com.luck.picture.lib.style.TitleBarStyle.previewTitleBackgroundColor
import com.luck.picture.lib.style.TitleBarStyle.titleBackgroundColor
import com.luck.picture.lib.style.TitleBarStyle.previewTitleLeftBackResource
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener.onRecyclerViewPreloadMore
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollListener.onScrolled
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener.onScrollSlow
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener.onScrollFast
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollListener.onScrollStateChanged
import com.luck.picture.lib.config.SelectMimeType.ofAudio
import com.luck.picture.lib.utils.DensityUtil.getStatusBarHeight
import com.luck.picture.lib.style.TitleBarStyle.titleBarHeight
import com.luck.picture.lib.style.TitleBarStyle.isDisplayTitleBarLine
import com.luck.picture.lib.style.TitleBarStyle.titleBarLineColor
import com.luck.picture.lib.style.TitleBarStyle.titleLeftBackResource
import com.luck.picture.lib.style.TitleBarStyle.titleDefaultTextResId
import com.luck.picture.lib.style.TitleBarStyle.titleDefaultText
import com.luck.picture.lib.style.TitleBarStyle.titleTextSize
import com.luck.picture.lib.style.TitleBarStyle.titleTextColor
import com.luck.picture.lib.style.TitleBarStyle.titleDrawableRightResource
import com.luck.picture.lib.style.TitleBarStyle.titleAlbumBackgroundResource
import com.luck.picture.lib.style.TitleBarStyle.isHideCancelButton
import com.luck.picture.lib.style.TitleBarStyle.titleCancelBackgroundResource
import com.luck.picture.lib.style.TitleBarStyle.titleCancelTextResId
import com.luck.picture.lib.style.TitleBarStyle.titleCancelText
import com.luck.picture.lib.style.TitleBarStyle.titleCancelTextColor
import com.luck.picture.lib.style.TitleBarStyle.titleCancelTextSize
import com.luck.picture.lib.style.TitleBarStyle.previewDeleteBackgroundResource
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import androidx.core.content.ContextCompat
import kotlin.jvm.JvmOverloads
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import com.luck.picture.lib.R

/**
 * @author：luck
 * @date：2022/3/18 10:39 下午
 * @describe：RoundCornerRelativeLayout
 */
class RoundCornerRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private val path: Path
    private val cornerSize: Float
    private val isTopNormal: Boolean
    private val isBottomNormal: Boolean
    private val mRect = RectF()
    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        path.reset()
        mRect.right = w.toFloat()
        mRect.bottom = h.toFloat()
        var cornerRadii: FloatArray
        if (!isTopNormal && !isBottomNormal) {
            path.addRoundRect(mRect, cornerSize, cornerSize, Path.Direction.CW)
        } else {
            if (isTopNormal) {
                cornerRadii =
                    floatArrayOf(0f, 0f, 0f, 0f, cornerSize, cornerSize, cornerSize, cornerSize)
                path.addRoundRect(mRect, cornerRadii, Path.Direction.CW)
            }
            if (isBottomNormal) {
                cornerRadii =
                    floatArrayOf(cornerSize, cornerSize, cornerSize, cornerSize, 0f, 0f, 0f, 0f)
                path.addRoundRect(mRect, cornerRadii, Path.Direction.CW)
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    init {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PictureRoundCornerRelativeLayout,
            defStyleAttr,
            0
        )
        cornerSize = a.getDimension(R.styleable.PictureRoundCornerRelativeLayout_psCorners, 0f)
        isTopNormal = a.getBoolean(R.styleable.PictureRoundCornerRelativeLayout_psTopNormal, false)
        isBottomNormal =
            a.getBoolean(R.styleable.PictureRoundCornerRelativeLayout_psBottomNormal, false)
        a.recycle()
        path = Path()
    }
}