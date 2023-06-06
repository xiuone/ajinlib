package com.xy.base.assembly.common.url

import android.view.View
import android.widget.TextView
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.web.WebActivityOpenListener

interface UrlAssemblyView :BaseAssemblyView{
    fun onCreateWebActivityOpenListener(): WebActivityOpenListener?
    fun onCreateButtonView(): View?
    fun onCreateSelectView(): View?
    fun onCreateRuleTextView(): TextView?
    fun onCreateRulKey():HashMap<String,UrlMode>
    fun onCreateUrlMode():UrlMode?
}