package xy.xy.base.assembly.common.lauch

import android.content.Context
import android.view.View
import xy.xy.base.assembly.base.BaseAssemblyWithContext
import xy.xy.base.utils.exp.*
import xy.xy.base.utils.lift.ActivityController

class CommonLaunchAssembly(view: CommonLaunchAssemblyView) : BaseAssemblyWithContext<CommonLaunchAssemblyView>(view) {


    private val agreeButtonView by lazy { this.view?.agreeButtonView() }
    private val refuseButtonView by lazy { this.view?.refuseButtonView() }
    private val privacyContentView by lazy { this.view?.privacyContentView() }

    override fun onCreateInit() {
        super.onCreateInit()
        val context = getContext()
        if (context == null){
            privacyContentView?.visibility = View.GONE
            this.view?.agreeLaunchPrivacy()
            return
        }
    }

    fun checkVersionPermission(){
        val context = getContext()
        if (context == null){
            privacyContentView?.visibility = View.GONE
            this.view?.agreeLaunchPrivacy()
            return
        }
        val currentVersion = context.getVersionCode()
        if (isPrivacyReady(context)){
            privacyContentView?.visibility = View.GONE
            this.view?.agreeLaunchPrivacy()
            return
        }

        privacyContentView?.visibility = View.VISIBLE
        agreeButtonView?.setOnClick{
            context.setSpLong(launchPrivacyKey,currentVersion.toLong())
            privacyContentView?.visibility = View.GONE
            this.view?.agreeLaunchPrivacy()
        }
        refuseButtonView?.setOnClick{
            privacyContentView?.visibility = View.GONE
            ActivityController.instance.closeAllAct()
        }
    }

    companion object{
        const val launchPrivacyKey = "LaunchPrivacyKey"

        fun isPrivacyReady(context:Context):Boolean{
            val oldVersion = context.getSpLong(launchPrivacyKey,0)
            val currentVersion = context.getVersionCode()
            return currentVersion > oldVersion
        }
    }
}