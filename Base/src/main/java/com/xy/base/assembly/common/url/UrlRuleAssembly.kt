package com.xy.base.assembly.common.url

import android.text.SpannableString
import androidx.lifecycle.LifecycleOwner
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.utils.exp.replaceContentColor
import com.xy.base.utils.exp.setContentClicked
import com.xy.base.utils.exp.setOnClick

class UrlRuleAssembly(view: UrlAssemblyView) : BaseAssembly<UrlAssemblyView>(view){
    private val aboutView by lazy { this.view?.onCreateButtonView() }
    private val selectView by lazy { this.view?.onCreateSelectView() }
    private val ruleTextView by lazy { this.view?.onCreateRuleTextView() }
    private val ruleHashMap by lazy { this.view?.onCreateRulKey()?:HashMap() }

    override fun onCreate(owner: LifecycleOwner?) {
        super.onCreate(owner)
        aboutView?.setOnClick{
            selectView?.isSelected = selectView?.isSelected != true
        }
        loadRulContent()
    }

    private fun loadRulContent(){
        val content = ruleTextView?.text?.toString()?:return
        val spannableString = SpannableString(content)
        for (entry in ruleHashMap.entries){
            spannableString.replaceContentColor(entry.value.color,entry.key).setContentClicked(entry.key){
                this.view?.onCreateWebActivityOpenListener()?.openUrlAct(getContext(),entry.value.url,entry.value.title)
            }
        }
    }

    fun isSelect() = selectView?.isSelected == true

}