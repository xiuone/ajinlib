package com.xy.libad.lanuch

import com.qq.e.ads.splash.SplashAD
import com.qq.e.ads.splash.SplashADListener
import com.qq.e.comm.util.AdError
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.utils.Logger


class AdLauncherAssembly(view: AdLauncherAssemblyView) : BaseAssembly<AdLauncherAssemblyView>(view) , SplashADListener {
    private var splashAD: SplashAD? = null

    fun showLauncher(launchId:String?){
        val container = this.view?.onLauncherViewGroup()
        val activity = this.view?.getCurrentAct()
        if (container == null || activity == null){
            this.view?.adLauncherOver()
        }else{
            splashAD = SplashAD(activity, launchId, this, 0)
            splashAD?.fetchAndShowIn(container)
        }
    }

    override fun onADDismissed() {
        Logger.d("onADDismissed  广告消失")
        this.view?.adLauncherOver()
    }

    override fun onNoAD(p0: AdError?) {
        Logger.d("onNoAD  没有广告")
        this.view?.adLauncherOver()
    }

    override fun onADPresent() {
        Logger.d("onADPresent  广告")
    }

    override fun onADClicked() {
        Logger.d("onADPresent  广告被点击")
    }

    override fun onADTick(p0: Long) {
        Logger.d("onADPresent  广告被点击")
    }

    override fun onADExposure() {
        Logger.d("onADExposure  广告")
    }

    override fun onADLoaded(p0: Long) {
        Logger.d("onADLoaded  广告被加载")
    }
}