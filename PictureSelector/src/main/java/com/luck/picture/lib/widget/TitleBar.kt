package com.luck.picture.lib.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
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
 * @date：2021/11/17 10:45 上午
 * @describe：TitleBar
 */
open class TitleBar : RelativeLayout, View.OnClickListener {
    protected var rlAlbumBg: RelativeLayout? = null
    protected var ivLeftBack: ImageView? = null
    var imageArrow: ImageView? = null
        protected set
    var imageDelete: ImageView? = null
        protected set
    protected var tvTitle: MarqueeTextView? = null
    var titleCancelView: TextView? = null
        protected set

    /**
     * title bar line
     *
     * @return
     */
    var titleBarLine: View? = null
        protected set
    protected var viewAlbumClickArea: View? = null
    protected var config: SelectorConfig? = null
    protected var viewTopStatusBar: View? = null
    protected var titleBarLayout: RelativeLayout? = null

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
        viewTopStatusBar = findViewById(R.id.top_status_bar)
        titleBarLayout = findViewById(R.id.rl_title_bar)
        ivLeftBack = findViewById(R.id.ps_iv_left_back)
        rlAlbumBg = findViewById(R.id.ps_rl_album_bg)
        imageDelete = findViewById(R.id.ps_iv_delete)
        viewAlbumClickArea = findViewById(R.id.ps_rl_album_click)
        tvTitle = findViewById(R.id.ps_tv_title)
        imageArrow = findViewById(R.id.ps_iv_arrow)
        titleCancelView = findViewById(R.id.ps_tv_cancel)
        titleBarLine = findViewById(R.id.title_bar_line)
        ivLeftBack.setOnClickListener(this)
        titleCancelView.setOnClickListener(this)
        rlAlbumBg.setOnClickListener(this)
        titleBarLayout.setOnClickListener(this)
        viewAlbumClickArea.setOnClickListener(this)
        setBackgroundColor(ContextCompat.getColor(context, R.color.ps_color_grey))
        handleLayoutUI()
        if (TextUtils.isEmpty(config!!.defaultAlbumName)) {
            setTitle(
                if (config!!.chooseMode == ofAudio()) context.getString(R.string.ps_all_audio) else context.getString(
                    R.string.ps_camera_roll
                )
            )
        } else {
            setTitle(config!!.defaultAlbumName)
        }
    }

    protected fun inflateLayout() {
        LayoutInflater.from(context).inflate(R.layout.ps_title_bar, this)
    }

    protected fun handleLayoutUI() {}

    /**
     * Set title
     *
     * @param title
     */
    fun setTitle(title: String?) {
        tvTitle!!.text = title
    }

    /**
     * Get title text
     */
    val titleText: String
        get() = tvTitle!!.text.toString()

    open fun setTitleBarStyle() {
        if (config!!.isPreviewFullScreenMode) {
            val layoutParams = viewTopStatusBar!!.layoutParams
            layoutParams.height = getStatusBarHeight(context)
        }
        val selectorStyle = config!!.selectorStyle
        val titleBarStyle = selectorStyle!!.titleBarStyle
        val titleBarHeight = titleBarStyle!!.titleBarHeight
        if (checkSizeValidity(titleBarHeight)) {
            titleBarLayout!!.layoutParams.height = titleBarHeight
        } else {
            titleBarLayout!!.layoutParams.height = dip2px(
                context, 48f
            )
        }
        if (titleBarLine != null) {
            if (titleBarStyle.isDisplayTitleBarLine) {
                titleBarLine!!.visibility = VISIBLE
                if (checkStyleValidity(titleBarStyle.titleBarLineColor)) {
                    titleBarLine!!.setBackgroundColor(titleBarStyle.titleBarLineColor)
                }
            } else {
                titleBarLine!!.visibility = GONE
            }
        }
        val backgroundColor = titleBarStyle.titleBackgroundColor
        if (checkStyleValidity(backgroundColor)) {
            setBackgroundColor(backgroundColor)
        }
        val backResId = titleBarStyle.titleLeftBackResource
        if (checkStyleValidity(backResId)) {
            ivLeftBack!!.setImageResource(backResId)
        }
        val titleDefaultText = if (checkStyleValidity(
                titleBarStyle.titleDefaultTextResId
            )
        ) context.getString(titleBarStyle.titleDefaultTextResId) else titleBarStyle.titleDefaultText!!
        if (checkTextValidity(titleDefaultText)) {
            tvTitle!!.text = titleDefaultText
        }
        val titleTextSize = titleBarStyle.titleTextSize
        if (checkSizeValidity(titleTextSize)) {
            tvTitle!!.textSize = titleTextSize.toFloat()
        }
        val titleTextColor = titleBarStyle.titleTextColor
        if (checkStyleValidity(titleTextColor)) {
            tvTitle!!.setTextColor(titleTextColor)
        }
        if (config!!.isOnlySandboxDir) {
            imageArrow!!.setImageResource(R.drawable.ps_ic_trans_1px)
        } else {
            val arrowResId = titleBarStyle.titleDrawableRightResource
            if (checkStyleValidity(arrowResId)) {
                imageArrow!!.setImageResource(arrowResId)
            }
        }
        val albumBackgroundRes = titleBarStyle.titleAlbumBackgroundResource
        if (checkStyleValidity(albumBackgroundRes)) {
            rlAlbumBg!!.setBackgroundResource(albumBackgroundRes)
        }
        if (titleBarStyle.isHideCancelButton) {
            titleCancelView!!.visibility = GONE
        } else {
            titleCancelView!!.visibility = VISIBLE
            val titleCancelBackgroundResource = titleBarStyle.titleCancelBackgroundResource
            if (checkStyleValidity(titleCancelBackgroundResource)) {
                titleCancelView!!.setBackgroundResource(titleCancelBackgroundResource)
            }
            val titleCancelText = if (checkStyleValidity(
                    titleBarStyle.titleCancelTextResId
                )
            ) context.getString(titleBarStyle.titleCancelTextResId) else titleBarStyle.titleCancelText!!
            if (checkTextValidity(titleCancelText)) {
                titleCancelView!!.text = titleCancelText
            }
            val titleCancelTextColor = titleBarStyle.titleCancelTextColor
            if (checkStyleValidity(titleCancelTextColor)) {
                titleCancelView!!.setTextColor(titleCancelTextColor)
            }
            val titleCancelTextSize = titleBarStyle.titleCancelTextSize
            if (checkSizeValidity(titleCancelTextSize)) {
                titleCancelView!!.textSize = titleCancelTextSize.toFloat()
            }
        }
        val deleteBackgroundResource = titleBarStyle.previewDeleteBackgroundResource
        if (checkStyleValidity(deleteBackgroundResource)) {
            imageDelete!!.setBackgroundResource(deleteBackgroundResource)
        } else {
            imageDelete!!.setBackgroundResource(R.drawable.ps_ic_delete)
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.ps_iv_left_back || id == R.id.ps_tv_cancel) {
            if (titleBarListener != null) {
                titleBarListener!!.onBackPressed()
            }
        } else if (id == R.id.ps_rl_album_bg || id == R.id.ps_rl_album_click) {
            if (titleBarListener != null) {
                titleBarListener!!.onShowAlbumPopWindow(this)
            }
        } else if (id == R.id.rl_title_bar) {
            if (titleBarListener != null) {
                titleBarListener!!.onTitleDoubleClick()
            }
        }
    }

    protected var titleBarListener: OnTitleBarListener? = null

    /**
     * TitleBar的功能事件回调
     *
     * @param listener
     */
    fun setOnTitleBarListener(listener: OnTitleBarListener?) {
        titleBarListener = listener
    }

    open class OnTitleBarListener {
        /**
         * 双击标题栏
         */
        open fun onTitleDoubleClick() {}

        /**
         * 关闭页面
         */
        open fun onBackPressed() {}

        /**
         * 显示专辑列表
         */
        open fun onShowAlbumPopWindow(anchor: View?) {}
    }
}