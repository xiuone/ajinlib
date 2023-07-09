package com.xy.base.fragment

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.xy.base.R
import com.xy.base.listener.ContextListener
import com.xy.base.utils.exp.setBar
import com.xy.base.utils.exp.setOnClick
import com.xy.base.utils.softkey.MoveKeyBoardController
import com.xy.base.utils.softkey.SoftKeyBoardDetector

/**
 * 加载状态
 */
abstract class FragmentBaseStatus : FragmentBase() , ContextListener {
    protected val rootView by lazy { LayoutInflater.from(context).inflate(onCreateRootRes(), null) }
    protected val titleFrameLayout: FrameLayout by lazy { rootView.findViewById(R.id.title_render_layout) }
    protected val contentFrameLayout: FrameLayout by lazy { rootView.findViewById(R.id.content_render_layout) }
    protected val statusLayout: FrameLayout by lazy {  rootView.findViewById(R.id.status_render_layout)}

    protected val titleView by lazy { titleFrameLayout.findViewById<TextView>(R.id.middle_title) }
    protected val backButton by lazy { titleFrameLayout.findViewById<View>(R.id.back_iv_button) }
    protected val rightLButton by lazy { titleFrameLayout.findViewById<View>(R.id.right_l_button) }
    protected val moreButton by lazy { titleFrameLayout.findViewById<ImageView>(R.id.more_button) }
    protected val rightButtonView by lazy { titleFrameLayout.findViewById<TextView>(R.id.toolbar_right_button) }
    protected val titleLine by lazy { titleFrameLayout.findViewById<View>(R.id.title_line) }

    protected val moveKeyBoardController: MoveKeyBoardController by lazy {
        MoveKeyBoardController(this)
    }

    protected fun getBackImg():ImageView?{
        val imageView = backButton
        if (imageView is ImageView){
            return imageView
        }
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState, view)
        setListener()
        val currentActivity = activity
        if (registerKeyBoard() && currentActivity != null){
            SoftKeyBoardDetector.register(currentActivity, moveKeyBoardController)
            moveKeyBoardController.keyBoardHide(currentActivity, 0)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootView
    }


    open fun initView(savedInstanceState: Bundle?, rootView: View?) {
        titleFrameLayout?.setBar()
        createView(titleLayoutRes(),titleFrameLayout)
        createView(contentLayoutRes(),contentFrameLayout)
        titleFrameLayout.visibility = View.GONE
        startInitView(savedInstanceState, rootView)
    }





    /**
     * 请求加载
     */
    private fun createView(@LayoutRes layoutRes: Int,viewGroup:ViewGroup?):View{
        var loadView: View = LayoutInflater.from(context).inflate(layoutRes, viewGroup,false)
        viewGroup?.isClickable = true
        viewGroup?.removeAllViews()
        viewGroup?.addView(loadView)
        return loadView
    }

    fun setBack(view: View?){
        view?.setOnClick {
            activity?.onBackPressed()
        }
    }


    protected fun setBackImageTintList(colorList: ColorStateList) = setImageTintList(getBackImg(),colorList)

    protected fun setImageTintList(imageView: ImageView?,colorList: ColorStateList){
        imageView?.run {
            ImageViewCompat.setImageTintList(this, colorList)
        }
    }

    protected fun setBackRes(res:Int){
        val imageView = backButton
        if (imageView is ImageView){
            imageView.setImageResource(res)
        }
    }

    open fun onCreateRootRes():Int = R.layout.layout_xiu_layer_view


    override fun getPageContext(): Context? = context
    override fun getCurrentAct(): Activity? = activity
    override fun getCurrentFragment(): Fragment? = this

    open fun startInitView(savedInstanceState: Bundle?, rootView: View?){}
    open fun registerKeyBoard():Boolean = false
    open fun setListener() {}
    @LayoutRes
    abstract fun contentLayoutRes(): Int
    open fun titleLayoutRes(): Int = R.layout.a_layout_toolbar_top_default

    override fun onDestroyView() {
        super.onDestroyView()
        val currentActivity = activity
        if (registerKeyBoard() && currentActivity != null)
            SoftKeyBoardDetector.removeListener(currentActivity,moveKeyBoardController)
    }
}