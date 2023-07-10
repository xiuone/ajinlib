package com.luck.picture.lib.interfaces

import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2020-04-14 18:44
 * @describe：OnRecyclerViewScrollListener
 */
interface OnRecyclerViewScrollListener {
    /**
     * Called when the scroll position of this RecyclerView changes. Subclasses should use this method to respond to scrolling within the adapter's data set instead of an explicit listener.
     * This method will always be invoked before listeners. If a subclass needs to perform any additional upkeep or bookkeeping after scrolling but before listeners run, this is a good place to do so.
     * This differs from View.onScrollChanged(int, int, int, int) in that it receives the distance scrolled in either direction within the adapter's data set instead of absolute scroll coordinates. Since RecyclerView cannot compute the absolute scroll position from any arbitrary point in the data set, onScrollChanged will always receive the current View.getScrollX() and View.getScrollY() values which do not correspond to the data set scroll position. However, some subclasses may choose to use these fields as special offsets.
     * Params:
     * dx – horizontal distance scrolled in pixels
     * dy – vertical distance scrolled in pixels
     */
    fun onScrolled(dx: Int, dy: Int)

    /**
     * Called when the scroll state of this RecyclerView changes. Subclasses should use this method to respond to state changes instead of an explicit listener.
     * This method will always be invoked before listeners, but after the LayoutManager responds to the scroll state change.
     * Params:
     * state – the new scroll state, one of SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING or SCROLL_STATE_SETTLING
     */
    fun onScrollStateChanged(state: Int)
}