package xy.xy.base.assembly.common.url

import android.text.SpannableString
import android.text.method.LinkMovementMethod
import xy.xy.base.assembly.base.BaseAssemblyWithContext
import xy.xy.base.utils.Logger
import xy.xy.base.utils.exp.replaceContentColor
import xy.xy.base.utils.exp.setContentClicked
import xy.xy.base.utils.exp.setOnClick

class UrlRuleAssembly(view: UrlAssemblyView,private val select:()->Unit = {}) : BaseAssemblyWithContext<UrlAssemblyView>(view){
    private val aboutView by lazy { this.view?.onCreateButtonView() }
    private val selectView by lazy { this.view?.onCreateSelectView() }
    private val ruleTextView by lazy { this.view?.onCreateRuleTextView() }
    private val ruleHashMap by lazy { this.view?.onCreateRulKey()?:HashMap() }

    override fun onCreateInit() {
        super.onCreateInit()
        Logger.d("=========UrlRuleAssembly")
        aboutView?.setOnClick{
            setStatus(selectView?.isSelected != true)
        }
        ruleTextView?.setOnClick{
            setStatus(selectView?.isSelected != true)
        }
        val content = ruleTextView?.text?.toString()?:return
        val spannableString = SpannableString(content)
        for (entry in ruleHashMap.entries){
            spannableString.replaceContentColor(entry.value.color,entry.key).setContentClicked(entry.key){
                this.view?.onCreateWebActivityOpenListener()?.openUrlAct(getContext(),entry.value.url,entry.value.title)
            }
        }
        ruleTextView?.movementMethod = LinkMovementMethod.getInstance()
        ruleTextView?.text = spannableString
    }

    private fun setStatus(status:Boolean){
        selectView?.isSelected = status
        select()
    }

    fun isSelect() = selectView?.isSelected == true

}