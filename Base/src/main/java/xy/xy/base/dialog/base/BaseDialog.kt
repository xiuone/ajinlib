package xy.xy.base.dialog.base

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import xy.xy.base.utils.exp.getScreenWidth
import xy.xy.base.dialog.listener.DialogActionListener
import xy.xy.base.utils.TagNumber
import xy.xy.base.utils.softkey.MoveKeyBoardController

abstract class BaseDialog(context: Context) : Dialog(context) ,
    DialogInterface.OnDismissListener, DialogActionListener,LifecycleObserver {
    protected val TAG by lazy { TagNumber.getTag(this::class.java.name) }
    protected var any:Any? = null
    protected var rootView:View?=null
    private val disListenerList by lazy { ArrayList<DialogInterface.OnDismissListener>() }
    protected var activity: Activity?=null
    protected val moveKeyBoardController: MoveKeyBoardController by lazy {
        MoveKeyBoardController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.setPadding(15, 0, 15, 0)
        val lp = window?.attributes
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        val width = WindowManager.LayoutParams.FILL_PARENT
        val proportion: Double = proportion()
        if (proportion != 0.0)
            lp?.width = ((context.getScreenWidth() * proportion).toInt())
        else
            lp?.width = width
        window?.attributes = lp
        window?.setBackgroundDrawableResource(R.color.transparent)
        window?.setGravity(gravity())
        setContent()
        initView()
    }

    open fun setContent(){
        val view = LayoutInflater.from(context).inflate(layoutRes(), null)
        setContentView(view)
        super.setOnDismissListener(this)
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        rootView = view
    }

    override fun showDialog()  = show()

    override fun showDialog(any: Any?) {
        if (any == null) return
        show()
        this.any = any
    }

    override fun showDialog(any: Any?,content: String?) {
        if (any == null) return
        show()
        this.any = any
    }

    fun getBindActivity() = activity



    override fun showDialog(idRes: Int?, content: String?) = showDialog()

    override fun showDialog(idRes: Int?, content: String?, any: Any?) = showDialog(any)

    override fun showDialogBindActivity(activity: Activity?) {
        if (activity == null)return
        this.activity = activity
        showDialog()
    }

    override fun showDialogBindActivity(activity: Activity?, any: Any?) {
        if (activity == null || any == null)return
        showDialogBindActivity(activity)
    }


    override fun showDialogBindActivity(activity: Activity?, idRes: Int?, content: String?) = showDialogBindActivity(activity)


    override fun showDialogBindActivity(activity: Activity?, idRes: Int?, content: String?, any: Any?) = showDialogBindActivity(activity,any)

    override fun show() {
        val isShow = isShowing
        super.show()
        if (!isShow)
            showAnimation(rootView)
    }

    override fun dialogIsShow(): Boolean = isShowing

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        synchronized(this){
            listener?.run {
                disListenerList.add(this)
            }
        }
    }

    open fun keyBoardEnable() = true

    /**
     * 显示动画
     */
    override fun onDismiss(p0: DialogInterface?) {
        synchronized(this) {
            for (listener in disListenerList) {
                listener.onDismiss(p0)
            }
        }
    }

    abstract fun showAnimation(view: View?)

    open fun initView(){}
    @LayoutRes
    open fun layoutRes(): Int = R.layout.select_dialog_item

    abstract fun proportion(): Double
    abstract fun gravity(): Int


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroyed(owner: LifecycleOwner) {}
}