package com.xy.base.assembly.common.url

import androidx.lifecycle.LifecycleOwner
import com.xy.base.assembly.base.BaseAssemblyWithContext
import com.xy.base.utils.exp.setOnClick

class UrlAssembly(view: UrlAssemblyView) : BaseAssemblyWithContext<UrlAssemblyView>(view){
    private val aboutView by lazy { this.view?.onCreateButtonView() }
    private val urlMode by lazy { this.view?.onCreateUrlMode() }

    override fun onCreate(owner: LifecycleOwner?) {
        super.onCreate(owner)
        aboutView?.setOnClick{
            val url = urlMode?.url?:return@setOnClick
            this.view?.onCreateWebActivityOpenListener()?.openUrlAct(getContext(),url,this.urlMode?.title)
        }
    }
}