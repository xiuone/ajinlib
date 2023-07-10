package com.luck.picture.lib.widget

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
import java.util.HashSet

/**
 * @author：luck
 * @date：2022/1/23 10:40 上午
 * @describe：SlideSelectionProcessor
 */
class SlideSelectionHandler
/**
 * @param selectionHandler the handler that takes care to handle the selection events
 */(private val mSelectionHandler: ISelectionHandler) :
    SlideSelectTouchListener.OnAdvancedSlideSelectListener {
    private var mStartFinishedListener: ISelectionStartFinishedListener? =
        null
    private var mOriginalSelection: HashSet<Int>? = null

    /**
     * @param startFinishedListener a listener that get's notified when the drag selection is started or finished
     * @return this
     */
    fun withStartFinishedListener(startFinishedListener: ISelectionStartFinishedListener?): SlideSelectionHandler {
        mStartFinishedListener = startFinishedListener
        return this
    }

    override fun onSelectionStarted(start: Int) {
        mOriginalSelection = HashSet()
        val selected = mSelectionHandler.selection
        if (selected != null) mOriginalSelection!!.addAll(selected)
        val isFirstSelected = mOriginalSelection!!.contains(start)
        mSelectionHandler.changeSelection(start, start, !mOriginalSelection!!.contains(start), true)
        if (mStartFinishedListener != null) {
            mStartFinishedListener!!.onSelectionStarted(start, isFirstSelected)
        }
    }

    override fun onSelectionFinished(end: Int) {
        mOriginalSelection = null
        if (mStartFinishedListener != null) mStartFinishedListener!!.onSelectionFinished(end)
    }

    override fun onSelectChange(start: Int, end: Int, isSelected: Boolean) {
        for (i in start..end) {
            checkedChangeSelection(i, i, isSelected != mOriginalSelection!!.contains(i))
        }
    }

    private fun checkedChangeSelection(start: Int, end: Int, newSelectionState: Boolean) {
        mSelectionHandler.changeSelection(start, end, newSelectionState, false)
    }

    interface ISelectionHandler {
        /**
         * @return the currently selected items => can be ignored
         */
        val selection: Set<Int>?

        /**
         * update your adapter and select select/unselect the passed index range, you be get a single for all modes but [Mode.Simple] and [Mode.FirstItemDependent]
         *
         * @param start             the first item of the range who's selection state changed
         * @param end               the last item of the range who's selection state changed
         * @param isSelected        true, if the range should be selected, false otherwise
         * @param calledFromOnStart true, if it was called from the [SlideSelectionHandler.onSelectionStarted] event
         */
        fun changeSelection(start: Int, end: Int, isSelected: Boolean, calledFromOnStart: Boolean)
    }

    interface ISelectionStartFinishedListener {
        /**
         * @param start                  the item on which the drag selection was started at
         * @param originalSelectionState the original selection state
         */
        fun onSelectionStarted(start: Int, originalSelectionState: Boolean)

        /**
         * @param end the item on which the drag selection was finished at
         */
        fun onSelectionFinished(end: Int)
    }
}