package com.xy.base.assembly.readly

import android.view.View
import com.xy.base.assembly.load.BaseAssemblyLoadDialog
import com.xy.base.assembly.load.BaseAssemblyViewLoadDialog
import com.xy.base.listener.LoadViewListener
import com.xy.base.utils.exp.setOnClick

abstract class BaseAssemblyLoadReadlyWithDialog<T: BaseAssemblyViewLoadDialog>(view: T, private var loadViewListener: LoadViewListener?):
    BaseAssemblyLoadDialog<T>(view){

    override fun onCreateInit() {
        super.onCreateInit()
        loadViewListener?.createUnNetReLoadView()?.setOnClick{
            loadData()
        }
        loadViewListener?.createErrorReLoadView()?.setOnClick{
            loadData()
        }
    }


    private fun resetView(){
        loadViewListener?.createLoadView()?.visibility = View.GONE
        loadViewListener?.createContentView()?.visibility = View.GONE
        loadViewListener?.createUnNetView()?.visibility = View.GONE
        loadViewListener?.createErrorView()?.visibility = View.GONE
        loadViewListener?.createEmptyView()?.visibility = View.GONE
    }

    protected fun showLoadIng(){
        resetView()
        loadViewListener?.createLoadView()?.visibility = View.VISIBLE
    }

    protected fun showError(){
        resetView()
        loadViewListener?.createErrorView()?.visibility = View.VISIBLE
    }

    protected fun showUnNet(){
        resetView()
        loadViewListener?.createUnNetView()?.visibility = View.VISIBLE
    }

    protected fun showContent(){
        resetView()
        loadViewListener?.createContentView()?.visibility = View.VISIBLE
    }

    protected fun showEmpty(){
        resetView()
        loadViewListener?.createEmptyView()?.visibility = View.VISIBLE
    }

    abstract fun loadData()
}