package xy.xy.base.web

import android.content.Context

interface WebActivityOpenListener{
    fun openUrlAct(context: Context?,url:String?,title:String?)
    fun openStrAct(context: Context?,loadStr:String?,title:String?)
}