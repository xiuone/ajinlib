package xy.xy.base

import android.content.Context
import androidx.multidex.MultiDexApplication
import xy.xy.base.utils.ContextHolder
import xy.xy.base.utils.Logger
import xy.xy.base.utils.lift.ActivityController
import xy.xy.base.utils.config.font.ConfigChangeListener
import xy.xy.base.utils.config.ConfigController
import me.jessyan.autosize.AutoSize


abstract class BaseApp : MultiDexApplication() , ConfigChangeListener {
    private val configController by lazy { ConfigController() }

    override fun onCreate() {
        super.onCreate()
        AutoSize.initCompatMultiProcess(this)
        registerActivityLifecycleCallbacks(ActivityController.instance)
        ContextHolder.setContext(this)
        Logger.debug = BuildConfig.DEBUG
    }

    override fun attachBaseContext(newBase: Context) {
        val config = configController.changeConfig(newBase)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onChangeConfig() {
        configController.checkChangeConfig(this)
    }

}