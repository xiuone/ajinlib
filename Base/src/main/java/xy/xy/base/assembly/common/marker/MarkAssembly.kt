package xy.xy.base.assembly.common.marker

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import xy.xy.base.R
import xy.xy.base.assembly.base.BaseAssemblyWithContext
import xy.xy.base.utils.exp.*


/**
 * 跳转到市场
 */
class MarkAssembly(view: MarkerAssemblyView) : BaseAssemblyWithContext<MarkerAssemblyView>(view) {

    private val scoringButton by lazy { this.view?.scoringButtonView() }

    private val marker = "market://details?id=%s"

    /**
     * 创建的时候初始化
     */
    override fun onCreateInit() {
        super.onCreateInit()
        scoringButton?.setOnClick{
            val haveMarker = hasAnyMarketInstalled()
            if (haveMarker){
                getContext()?.startMark(getContext()?.packageName)
            }
            getContext()?.showToast(getContext()?.getResString(R.string.marker_un))
        }
    }

    /**
     * 查看市场是否存在
     */
    private fun hasAnyMarketInstalled(): Boolean {
        val intent = Intent()
        intent.data = Uri.parse(String.format(marker,"android.browser"))
        val list = getContext()?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return !list.isNullOrEmpty()
    }

}