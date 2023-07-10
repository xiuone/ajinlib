package xy.xy.base.utils.lift

import android.app.Activity
import android.os.Bundle
import java.util.*


class ActivityController : BaseActivityLifecycleCallbacks() {
    val activities: MutableList<Activity> = ArrayList()

    fun closeAct(mClass: Class<*>) {
        synchronized(this){
            for (activity in activities) {
                if (activity.javaClass == mClass) {
                    if (!activity.isFinishing) {
                        activity.finish()
                        activities.remove(activity)
                        return
                    }
                }
            }
        }

    }

    fun closeAllAct() = closeAllActWidthOut(null)

    fun closeAllActWidthOut(withOutClass:Class<*>? = null){
        synchronized(this){
            for (activity in activities) {
                if (!activity.isFinishing && (withOutClass == null || activity.javaClass != withOutClass)) {
                    activity.finish()
                    activities.remove(activity)
                }
            }
        }
    }



    fun closeActWithOut(vararg mClassList: Class<*>) {
        synchronized(this){
            for (activity in activities) {
                for (liveAct in mClassList){
                    if (activity.javaClass == liveAct){
                        break
                    }
                }
                activity.finish()
            }
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        synchronized(this){
            if (activity == null) return
            for (act in activities) {
                if (activity.javaClass == act.javaClass) {
                    return
                }
            }
            activities.add(activity)
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        synchronized(this){
            activities.remove(activity)
        }
    }


    companion object{
        val instance = ActivityController()
    }
}
