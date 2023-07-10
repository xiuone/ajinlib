package xy.xy.base.listener

import android.view.View

interface LoadViewListener : LoadEmptyView, LoadErrorView {
    fun createContentView(): View?
    fun createLoadView():View?
    fun createUnNetView(): View?
    fun createErrorReLoadView(): View?
    fun createUnNetReLoadView(): View?
}