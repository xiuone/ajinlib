package com.xy.base.assembly.common.lauch

import android.view.View
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.utils.exp.*
import com.xy.base.utils.lift.ActivityController

class CommonLaunchAssembly(view: CommonLaunchAssemblyView) : BaseAssembly<CommonLaunchAssemblyView>(view) {
    private val launchPrivacyKey = "LaunchPrivacyKey"

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
        val oldVersion = context.getSpLong(launchPrivacyKey,0)
        val currentVersion = context.getVersionCode()
        if (currentVersion <= oldVersion){
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
}