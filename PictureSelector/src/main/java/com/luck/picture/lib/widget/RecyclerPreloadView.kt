package com.luck.picture.lib.widget

import android.content.Context
import android.util.AttributeSet
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollListener
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener
import java.lang.RuntimeException

/**
 * @author：luck
 * @date：2020-04-14 18:43
 * @describe：RecyclerPreloadView
 */
class RecyclerPreloadView : RecyclerView {
    private var isInTheBottom = false
    /**
     * Whether to load more
     */
    /**
     * Whether to load more
     *
     * @param isEnabledLoadMore
     */
    var isEnabledLoadMore = false

    /**
     * Gets the first visible position index
     *
     * @return
     */
    var firstVisiblePosition = 0
        private set

    /**
     * Gets the last visible position index
     *
     * @return
     */
    var lastVisiblePosition = 0

    /**
     * reachBottomRow = 1;(default)
     * mean : when the lastVisibleRow is lastRow , call the onReachBottom();
     * reachBottomRow = 2;
     * mean : when the lastVisibleRow is Penultimate Row , call the onReachBottom();
     * And so on
     */
    private var reachBottomRow = BOTTOM_DEFAULT

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    fun setReachBottomRow(reachBottomRow: Int) {
        var reachBottomRow = reachBottomRow
        if (reachBottomRow < 1) reachBottomRow = 1
        this.reachBottomRow = reachBottomRow
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        val layoutManager = layoutManager
            ?: throw RuntimeException("LayoutManager is null,Please check it!")
        setLayoutManagerPosition(layoutManager)
        if (onRecyclerViewPreloadListener != null) {
            if (isEnabledLoadMore) {
                val adapter = adapter ?: throw RuntimeException("Adapter is null,Please check it!")
                var isReachBottom = false
                if (layoutManager is GridLayoutManager) {
                    val gridLayoutManager = layoutManager
                    val rowCount = adapter.itemCount / gridLayoutManager.spanCount
                    val lastVisibleRowPosition =
                        gridLayoutManager.findLastVisibleItemPosition() / gridLayoutManager.spanCount
                    isReachBottom = lastVisibleRowPosition >= rowCount - reachBottomRow
                }
                if (!isReachBottom) {
                    isInTheBottom = false
                } else if (!isInTheBottom) {
                    onRecyclerViewPreloadListener!!.onRecyclerViewPreloadMore()
                    if (dy > 0) {
                        isInTheBottom = true
                    }
                } else {
                    // 属于首次进入屏幕未滑动且内容未超过一屏，用于确保分页数设置过小导致内容不足二次上拉加载...
                    if (dy == 0) {
                        isInTheBottom = false
                    }
                }
            }
        }
        if (onRecyclerViewScrollListener != null) {
            onRecyclerViewScrollListener!!.onScrolled(dx, dy)
        }
        if (onRecyclerViewScrollStateListener != null) {
            if (Math.abs(dy) < LIMIT) {
                onRecyclerViewScrollStateListener!!.onScrollSlow()
            } else {
                onRecyclerViewScrollStateListener!!.onScrollFast()
            }
        }
    }

    private fun setLayoutManagerPosition(layoutManager: LayoutManager?) {
        if (layoutManager is GridLayoutManager) {
            val gridLayoutManager = layoutManager
            firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition()
            lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is LinearLayoutManager) {
            val linearLayoutManager = layoutManager
            firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()
            lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE || state == SCROLL_STATE_DRAGGING) {
            setLayoutManagerPosition(layoutManager)
        }
        if (onRecyclerViewScrollListener != null) {
            onRecyclerViewScrollListener!!.onScrollStateChanged(state)
        }
        if (state == SCROLL_STATE_IDLE) {
            if (onRecyclerViewScrollStateListener != null) {
                onRecyclerViewScrollStateListener!!.onScrollSlow()
            }
        }
    }

    private var onRecyclerViewPreloadListener: OnRecyclerViewPreloadMoreListener? = null
    fun setOnRecyclerViewPreloadListener(listener: OnRecyclerViewPreloadMoreListener?) {
        onRecyclerViewPreloadListener = listener
    }

    private var onRecyclerViewScrollStateListener: OnRecyclerViewScrollStateListener? = null
    fun setOnRecyclerViewScrollStateListener(listener: OnRecyclerViewScrollStateListener?) {
        onRecyclerViewScrollStateListener = listener
    }

    private var onRecyclerViewScrollListener: OnRecyclerViewScrollListener? = null
    fun setOnRecyclerViewScrollListener(listener: OnRecyclerViewScrollListener?) {
        onRecyclerViewScrollListener = listener
    }

    companion object {
        private val TAG = RecyclerPreloadView::class.java.simpleName
        private const val BOTTOM_DEFAULT = 1
        const val BOTTOM_PRELOAD = 2
        private const val LIMIT = 150
    }
}