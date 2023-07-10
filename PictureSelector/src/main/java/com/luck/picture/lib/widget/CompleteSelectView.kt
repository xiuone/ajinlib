package com.luck.picture.lib.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
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
 * @date：2021/11/21 11:28 下午
 * @describe：CompleteSelectView
 */
class CompleteSelectView : LinearLayout {
    private var tvSelectNum: TextView? = null
    private var tvComplete: TextView? = null
    private var numberChangeAnimation: Animation? = null
    private var config: SelectorConfig? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inflateLayout()
        orientation = HORIZONTAL
        tvSelectNum = findViewById(R.id.ps_tv_select_num)
        tvComplete = findViewById(R.id.ps_tv_complete)
        gravity = Gravity.CENTER_VERTICAL
        numberChangeAnimation = AnimationUtils.loadAnimation(context, R.anim.ps_anim_modal_in)
        config = instance!!.selectorConfig
    }

    protected fun inflateLayout() {
        LayoutInflater.from(context).inflate(R.layout.ps_complete_selected_layout, this)
    }

    /**
     * 完成选择按钮样式
     */
    fun setCompleteSelectViewStyle() {
        val selectorStyle = config!!.selectorStyle
        val selectMainStyle = selectorStyle!!.selectMainStyle
        if (checkStyleValidity(selectMainStyle!!.selectNormalBackgroundResources)) {
            setBackgroundResource(selectMainStyle.selectNormalBackgroundResources)
        }
        val selectNormalText = if (checkStyleValidity(
                selectMainStyle.selectNormalTextResId
            )
        ) context.getString(selectMainStyle.selectNormalTextResId) else selectMainStyle.selectNormalText!!
        if (checkTextValidity(selectNormalText)) {
            if (checkTextTwoFormatValidity(selectNormalText)) {
                tvComplete!!.text =
                    String.format(selectNormalText, config!!.selectCount, config!!.maxSelectNum)
            } else {
                tvComplete!!.text = selectNormalText
            }
        }
        val selectNormalTextSize = selectMainStyle.selectNormalTextSize
        if (checkSizeValidity(selectNormalTextSize)) {
            tvComplete!!.textSize = selectNormalTextSize.toFloat()
        }
        val selectNormalTextColor = selectMainStyle.selectNormalTextColor
        if (checkStyleValidity(selectNormalTextColor)) {
            tvComplete!!.setTextColor(selectNormalTextColor)
        }
        val bottomBarStyle = selectorStyle.bottomBarStyle
        if (bottomBarStyle!!.isCompleteCountTips) {
            val selectNumRes = bottomBarStyle.bottomSelectNumResources
            if (checkStyleValidity(selectNumRes)) {
                tvSelectNum!!.setBackgroundResource(selectNumRes)
            }
            val selectNumTextSize = bottomBarStyle.bottomSelectNumTextSize
            if (checkSizeValidity(selectNumTextSize)) {
                tvSelectNum!!.textSize = selectNumTextSize.toFloat()
            }
            val selectNumTextColor = bottomBarStyle.bottomSelectNumTextColor
            if (checkStyleValidity(selectNumTextColor)) {
                tvSelectNum!!.setTextColor(selectNumTextColor)
            }
        }
    }

    /**
     * 选择结果发生变化
     */
    fun setSelectedChange(isPreview: Boolean) {
        val selectorStyle = config!!.selectorStyle
        val selectMainStyle = selectorStyle!!.selectMainStyle
        if (config!!.selectCount > 0) {
            isEnabled = true
            val selectBackground = selectMainStyle!!.selectBackgroundResources
            if (checkStyleValidity(selectBackground)) {
                setBackgroundResource(selectBackground)
            } else {
                setBackgroundResource(R.drawable.ps_ic_trans_1px)
            }
            val selectText = if (checkStyleValidity(
                    selectMainStyle.selectTextResId
                )
            ) context.getString(selectMainStyle.selectTextResId) else selectMainStyle.selectText!!
            if (checkTextValidity(selectText)) {
                if (checkTextTwoFormatValidity(selectText)) {
                    tvComplete!!.text =
                        String.format(selectText, config!!.selectCount, config!!.maxSelectNum)
                } else {
                    tvComplete!!.text = selectText
                }
            } else {
                tvComplete!!.text = context.getString(R.string.ps_completed)
            }
            val selectTextSize = selectMainStyle.selectTextSize
            if (checkSizeValidity(selectTextSize)) {
                tvComplete!!.textSize = selectTextSize.toFloat()
            }
            val selectTextColor = selectMainStyle.selectTextColor
            if (checkStyleValidity(selectTextColor)) {
                tvComplete!!.setTextColor(selectTextColor)
            } else {
                tvComplete!!.setTextColor(ContextCompat.getColor(context, R.color.ps_color_fa632d))
            }
            if (selectorStyle.bottomBarStyle!!.isCompleteCountTips) {
                if (tvSelectNum!!.visibility == GONE || tvSelectNum!!.visibility == INVISIBLE) {
                    tvSelectNum!!.visibility = VISIBLE
                }
                if (TextUtils.equals(toString(config!!.selectCount), tvSelectNum!!.text)) {
                    // ignore
                } else {
                    tvSelectNum!!.text = toString(config!!.selectCount)
                    if (config!!.onSelectAnimListener != null) {
                        config!!.onSelectAnimListener!!.onSelectAnim(tvSelectNum)
                    } else {
                        tvSelectNum!!.startAnimation(numberChangeAnimation)
                    }
                }
            } else {
                tvSelectNum!!.visibility = GONE
            }
        } else {
            if (isPreview && selectMainStyle!!.isCompleteSelectRelativeTop) {
                isEnabled = true
                val selectBackground = selectMainStyle.selectBackgroundResources
                if (checkStyleValidity(selectBackground)) {
                    setBackgroundResource(selectBackground)
                } else {
                    setBackgroundResource(R.drawable.ps_ic_trans_1px)
                }
                val selectTextColor = selectMainStyle.selectTextColor
                if (checkStyleValidity(selectTextColor)) {
                    tvComplete!!.setTextColor(selectTextColor)
                } else {
                    tvComplete!!.setTextColor(ContextCompat.getColor(context, R.color.ps_color_9b))
                }
            } else {
                isEnabled = config!!.isEmptyResultReturn
                val normalBackground = selectMainStyle!!.selectNormalBackgroundResources
                if (checkStyleValidity(normalBackground)) {
                    setBackgroundResource(normalBackground)
                } else {
                    setBackgroundResource(R.drawable.ps_ic_trans_1px)
                }
                val normalTextColor = selectMainStyle.selectNormalTextColor
                if (checkStyleValidity(normalTextColor)) {
                    tvComplete!!.setTextColor(normalTextColor)
                } else {
                    tvComplete!!.setTextColor(ContextCompat.getColor(context, R.color.ps_color_9b))
                }
            }
            tvSelectNum!!.visibility = GONE
            val selectNormalText = if (checkStyleValidity(
                    selectMainStyle!!.selectNormalTextResId
                )
            ) context.getString(
                selectMainStyle.selectNormalTextResId
            ) else selectMainStyle.selectNormalText!!
            if (checkTextValidity(selectNormalText)) {
                if (checkTextTwoFormatValidity(selectNormalText)) {
                    tvComplete!!.text =
                        String.format(selectNormalText, config!!.selectCount, config!!.maxSelectNum)
                } else {
                    tvComplete!!.text = selectNormalText
                }
            } else {
                tvComplete!!.text = context.getString(R.string.ps_please_select)
            }
            val normalTextSize = selectMainStyle.selectNormalTextSize
            if (checkSizeValidity(normalTextSize)) {
                tvComplete!!.textSize = normalTextSize.toFloat()
            }
        }
    }
}