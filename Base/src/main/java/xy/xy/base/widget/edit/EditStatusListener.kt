package xy.xy.base.widget.edit

import xy.xy.base.listener.AppTextWatcher

interface EditStatusListener:AppTextWatcher {
    fun onEditorAction(actionId :Int,content:String):Boolean = false
}