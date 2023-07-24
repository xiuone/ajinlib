package xy.xy.base.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import xy.xy.base.assembly.base.BaseAssemblyWithContext
import xy.xy.base.assembly.base.BaseAssemblyViewWithContext
import xy.xy.base.utils.config.ConfigController
import xy.xy.base.act.ActivityBaseStatusBar
import xy.xy.base.act.ActivityBase
import xy.xy.base.assembly.BaseAssemblyImpl
import xy.xy.base.listener.OpenPageListener
import xy.xy.base.utils.TagNumber

abstract class FragmentBase : Fragment(), ActivityResultCallback<ActivityResult>,OpenPageListener{
    protected val TAG by lazy { TagNumber.getTag(this::class.java.name) }
    private val FRAGMENT_BASE_LAUNCH:String = "FRAGMENT:BASE:LAUNCH:"
    private val activityResultLauncherList: HashMap<String,ActivityResultLauncher<Intent>> by lazy { HashMap() }
    private val configController by lazy { ConfigController(::needReCreate) }

    private val assemblyList by lazy { ArrayList<BaseAssemblyWithContext<*>>() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindLifecycleObserve()
        registerLaunch(FRAGMENT_BASE_LAUNCH,this)
    }

    fun getActivityBase(): ActivityBase? {
        if (activity is ActivityBase)
            return activity as ActivityBase
        return null
    }

    fun getActivityBarBase(): ActivityBaseStatusBar? {
        if (activity is ActivityBaseStatusBar)
            return activity as ActivityBaseStatusBar
        return null
    }

    override fun onResume() {
        super.onResume()
        context?.run {
            configController.onResume(this)
        }
    }

    protected open fun needReCreate(){}


    protected open fun addAssembly(vararg assemblyList: BaseAssemblyWithContext<*>?) {
        if (assemblyList.isNullOrEmpty()) return
        synchronized(this){
            for (assembly in assemblyList){
                if (assembly != null && !this.assemblyList.contains(assembly)) {
                    lifecycle.addObserver(assembly)
                    assembly.onCreateInit()
                    this.assemblyList.add(assembly)
                }
            }
        }
    }


    protected fun addLifecycleObserver(vararg lifeList: BaseAssemblyImpl){
        for (lift in lifeList){
            lifecycle.addObserver(lift)
            lift.onCreate()
        }
    }

    protected open fun <T: BaseAssemblyViewWithContext> addAssembly(savedInstanceState : Bundle?, vararg assemblyList: BaseAssemblyWithContext<*>) {
        if (assemblyList.isNullOrEmpty()) return
        synchronized(this){
            for (assembly in assemblyList){
                lifecycle.addObserver(assembly)
                assembly.onCreateInit(savedInstanceState)
                this.assemblyList.add(assembly)
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        for (assembly in assemblyList){
            assembly.onSaveInstanceState(outState)
        }
    }

    /**
     * 注册跳转
     */
    override fun registerLaunch(tag: String,launcher:ActivityResultCallback<ActivityResult>){
        activityResultLauncherList[tag] = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), launcher)
    }

    /**
     * 进入历史界面
     */
    override fun startActivityForResult(intent: Intent) = startActivityForResult(FRAGMENT_BASE_LAUNCH,intent)
    /**
     * 进入历史界面
     */
    override fun startActivityForResult(tag:String,intent: Intent) {
        activityResultLauncherList[tag]?.launch(intent)
    }



    override fun onActivityResult(result: ActivityResult?) {}


    open fun bindLifecycleObserve() {}


}