package xy.xy.base.assembly.common.url

import android.view.View
import android.widget.TextView
import xy.xy.base.assembly.base.BaseAssemblyViewWithContext
import xy.xy.base.web.WebActivityOpenListener

interface UrlAssemblyView :BaseAssemblyViewWithContext{
    fun onCreateWebActivityOpenListener(): WebActivityOpenListener?
    fun onCreateButtonView(): View?
    fun onCreateSelectView(): View?
    fun onCreateRuleTextView(): TextView?
    fun onCreateRulKey():HashMap<String,UrlMode>
    fun onCreateUrlMode():UrlMode?
}