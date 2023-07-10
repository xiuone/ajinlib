package com.luck.picture.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RelativeLayout
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
 * @date：2021/11/17 10:46 上午
 * @describe：BottomNavBar
 */
open class BottomNavBar : RelativeLayout, View.OnClickListener {
    protected var tvPreview: TextView? = null
    protected var tvImageEditor: TextView? = null
    private var originalCheckbox: CheckBox? = null
    protected var config: SelectorConfig? = null

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

    protected fun init() {
        inflateLayout()
        isClickable = true
        isFocusable = true
        config = instance!!.selectorConfig
        tvPreview = findViewById(R.id.ps_tv_preview)
        tvImageEditor = findViewById(R.id.ps_tv_editor)
        originalCheckbox = findViewById(R.id.cb_original)
        tvPreview.setOnClickListener(this)
        tvImageEditor.setVisibility(GONE)
        setBackgroundColor(ContextCompat.getColor(context, R.color.ps_color_grey))
        originalCheckbox.setChecked(config!!.isCheckOriginalImage)
        originalCheckbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            config!!.isCheckOriginalImage = isChecked
            originalCheckbox.setChecked(config!!.isCheckOriginalImage)
            if (bottomNavBarListener != null) {
                bottomNavBarListener!!.onCheckOriginalChange()
                if (isChecked && config!!.selectCount == 0) {
                    bottomNavBarListener!!.onFirstCheckOriginalSelectedChange()
                }
            }
        })
        handleLayoutUI()
    }

    protected fun inflateLayout() {
        inflate(context, R.layout.ps_bottom_nav_bar, this)
    }

    protected open fun handleLayoutUI() {}
    open fun setBottomNavBarStyle() {
        if (config!!.isDirectReturnSingle) {
            visibility = GONE
            return
        }
        val selectorStyle = config!!.selectorStyle
        val bottomBarStyle = selectorStyle!!.bottomBarStyle
        if (config!!.isOriginalControl) {
            originalCheckbox!!.visibility = VISIBLE
            val originalDrawableLeft = bottomBarStyle!!.bottomOriginalDrawableLeft
            if (checkStyleValidity(originalDrawableLeft)) {
                originalCheckbox!!.setButtonDrawable(originalDrawableLeft)
            }
            val bottomOriginalText = if (checkStyleValidity(
                    bottomBarStyle.bottomOriginalTextResId
                )
            ) context.getString(
                bottomBarStyle.bottomOriginalTextResId
            ) else bottomBarStyle.bottomOriginalText!!
            if (checkTextValidity(bottomOriginalText)) {
                originalCheckbox!!.text = bottomOriginalText
            }
            val originalTextSize = bottomBarStyle.bottomOriginalTextSize
            if (checkSizeValidity(originalTextSize)) {
                originalCheckbox!!.textSize = originalTextSize.toFloat()
            }
            val originalTextColor = bottomBarStyle.bottomOriginalTextColor
            if (checkStyleValidity(originalTextColor)) {
                originalCheckbox!!.setTextColor(originalTextColor)
            }
        }
        val narBarHeight = bottomBarStyle!!.bottomNarBarHeight
        if (checkSizeValidity(narBarHeight)) {
            layoutParams.height = narBarHeight
        } else {
            layoutParams.height = dip2px(context, 46f)
        }
        val backgroundColor = bottomBarStyle.bottomNarBarBackgroundColor
        if (checkStyleValidity(backgroundColor)) {
            setBackgroundColor(backgroundColor)
        }
        val previewNormalTextColor = bottomBarStyle.bottomPreviewNormalTextColor
        if (checkStyleValidity(previewNormalTextColor)) {
            tvPreview!!.setTextColor(previewNormalTextColor)
        }
        val previewTextSize = bottomBarStyle.bottomPreviewNormalTextSize
        if (checkSizeValidity(previewTextSize)) {
            tvPreview!!.textSize = previewTextSize.toFloat()
        }
        val bottomPreviewText = if (checkStyleValidity(
                bottomBarStyle.bottomPreviewNormalTextResId
            )
        ) context.getString(
            bottomBarStyle.bottomPreviewNormalTextResId
        ) else bottomBarStyle.bottomPreviewNormalText!!
        if (checkTextValidity(bottomPreviewText)) {
            tvPreview!!.text = bottomPreviewText
        }
        val editorText = if (checkStyleValidity(
                bottomBarStyle.bottomEditorTextResId
            )
        ) context.getString(bottomBarStyle.bottomEditorTextResId) else bottomBarStyle.bottomEditorText!!
        if (checkTextValidity(editorText)) {
            tvImageEditor!!.text = editorText
        }
        val editorTextSize = bottomBarStyle.bottomEditorTextSize
        if (checkSizeValidity(editorTextSize)) {
            tvImageEditor!!.textSize = editorTextSize.toFloat()
        }
        val editorTextColor = bottomBarStyle.bottomEditorTextColor
        if (checkStyleValidity(editorTextColor)) {
            tvImageEditor!!.setTextColor(editorTextColor)
        }
        val originalDrawableLeft = bottomBarStyle.bottomOriginalDrawableLeft
        if (checkStyleValidity(originalDrawableLeft)) {
            originalCheckbox!!.setButtonDrawable(originalDrawableLeft)
        }
        val originalText = if (checkStyleValidity(
                bottomBarStyle.bottomOriginalTextResId
            )
        ) context.getString(bottomBarStyle.bottomOriginalTextResId) else bottomBarStyle.bottomOriginalText!!
        if (checkTextValidity(originalText)) {
            originalCheckbox!!.text = originalText
        }
        val originalTextSize = bottomBarStyle.bottomOriginalTextSize
        if (checkSizeValidity(originalTextSize)) {
            originalCheckbox!!.textSize = originalTextSize.toFloat()
        }
        val originalTextColor = bottomBarStyle.bottomOriginalTextColor
        if (checkStyleValidity(originalTextColor)) {
            originalCheckbox!!.setTextColor(originalTextColor)
        }
    }

    /**
     * 原图选项发生变化
     */
    fun setOriginalCheck() {
        originalCheckbox!!.isChecked = config!!.isCheckOriginalImage
    }

    /**
     * 选择结果发生变化
     */
    fun setSelectedChange() {
        calculateFileTotalSize()
        val selectorStyle = config!!.selectorStyle
        val bottomBarStyle = selectorStyle!!.bottomBarStyle
        if (config!!.selectCount > 0) {
            tvPreview!!.isEnabled = true
            val previewSelectTextColor = bottomBarStyle!!.bottomPreviewSelectTextColor
            if (checkStyleValidity(previewSelectTextColor)) {
                tvPreview!!.setTextColor(previewSelectTextColor)
            } else {
                tvPreview!!.setTextColor(ContextCompat.getColor(context, R.color.ps_color_fa632d))
            }
            val previewSelectText = if (checkStyleValidity(
                    bottomBarStyle.bottomPreviewSelectTextResId
                )
            ) context.getString(
                bottomBarStyle.bottomPreviewSelectTextResId
            ) else bottomBarStyle.bottomPreviewSelectText!!
            if (checkTextValidity(previewSelectText)) {
                if (checkTextFormatValidity(previewSelectText)) {
                    tvPreview!!.text = String.format(previewSelectText, config!!.selectCount)
                } else {
                    tvPreview!!.text = previewSelectText
                }
            } else {
                tvPreview!!.text = context.getString(R.string.ps_preview_num, config!!.selectCount)
            }
        } else {
            tvPreview!!.isEnabled = false
            val previewNormalTextColor = bottomBarStyle!!.bottomPreviewNormalTextColor
            if (checkStyleValidity(previewNormalTextColor)) {
                tvPreview!!.setTextColor(previewNormalTextColor)
            } else {
                tvPreview!!.setTextColor(ContextCompat.getColor(context, R.color.ps_color_9b))
            }
            val previewText = if (checkStyleValidity(
                    bottomBarStyle.bottomPreviewNormalTextResId
                )
            ) context.getString(
                bottomBarStyle.bottomPreviewNormalTextResId
            ) else bottomBarStyle.bottomPreviewNormalText!!
            if (checkTextValidity(previewText)) {
                tvPreview!!.text = previewText
            } else {
                tvPreview!!.text = context.getString(R.string.ps_preview)
            }
        }
    }

    /**
     * 计算原图大小
     */
    private fun calculateFileTotalSize() {
        if (config!!.isOriginalControl) {
            var totalSize: Long = 0
            for (i in 0 until config!!.selectCount) {
                val media = config!!.selectedResult[i]
                totalSize += media.size
            }
            if (totalSize > 0) {
                val fileSize = formatAccurateUnitFileSize(totalSize)
                originalCheckbox!!.text = context.getString(R.string.ps_original_image, fileSize)
            } else {
                originalCheckbox!!.text = context.getString(R.string.ps_default_original_image)
            }
        } else {
            originalCheckbox!!.text =
                context.getString(R.string.ps_default_original_image)
        }
    }

    override fun onClick(view: View) {
        if (bottomNavBarListener == null) {
            return
        }
        val id = view.id
        if (id == R.id.ps_tv_preview) {
            bottomNavBarListener!!.onPreview()
        }
    }

    protected var bottomNavBarListener: OnBottomNavBarListener? = null

    /**
     * 预览NarBar的功能事件回调
     *
     * @param listener
     */
    fun setOnBottomNavBarListener(listener: OnBottomNavBarListener?) {
        bottomNavBarListener = listener
    }

    open class OnBottomNavBarListener {
        /**
         * 预览
         */
        open fun onPreview() {}

        /**
         * 编辑图片
         */
        open fun onEditImage() {}

        /**
         * 原图发生变化
         */
        open fun onCheckOriginalChange() {}

        /**
         * 首次选择原图并加入选择结果
         */
        open fun onFirstCheckOriginalSelectedChange() {}
    }
}