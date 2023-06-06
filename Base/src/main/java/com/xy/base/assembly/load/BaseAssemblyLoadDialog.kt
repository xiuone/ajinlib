package com.xy.base.assembly.load

import androidx.lifecycle.LifecycleOwner
import com.xy.base.listener.EmptyErrorListener
import com.xy.base.assembly.base.BaseAssembly

abstract class BaseAssemblyLoadDialog<T : BaseAssemblyViewLoadDialog>(view: T?) : BaseAssembly<T>(view),
    EmptyErrorListener {
    /**
     * 提示框
     */
    private val loadDialog by lazy { onCreateLoadDialog() }
    private val progressTv = view?.loadProgressTvIdRes()
    private val progressString = view?.loadProgressString()

    fun showLoad(){
        loadDialog?.showDialog(progressTv,progressString?:"")
    }

    fun showLoad(progressString:String?){
        loadDialog?.showDialog(progressTv,progressString?:"")
    }

    fun dismiss(){
        if (loadDialog?.isShowing == true)
            loadDialog?.dismiss()
    }

    override fun onError() {
        loadDialog?.dismiss()
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        dismiss()
        super.onDestroyed(owner)
    }

    open fun onCreateLoadDialog() = this.view?.onCreateLoadDialog()
}