package com.xy.base.act

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
import com.xy.base.R
import com.xy.base.listener.ContextListener
import com.xy.base.utils.exp.getResColor
import com.xy.base.utils.exp.setOnClick

/**
 * 加载状态
 */
abstract class ActivityBaseStatus : ActivityBase(), ContextListener {
    private val baseLayoutRes = -1
    protected val rootView by lazy { LayoutInflater.from(this).inflate(onCreateRootRes(), null) }
    protected val titleFrameLayout: FrameLayout by lazy { findViewById(R.id.title_render_layout) }
    protected val contentFrameLayout: FrameLayout by lazy { findViewById(R.id.content_render_layout) }
    protected val statusLayout: FrameLayout by lazy {  findViewById(R.id.status_render_layout)}

    protected val titleView by lazy { findViewById<TextView>(R.id.middle_title) }
    protected val backButton by lazy { findViewById<View>(R.id.back_iv_button) }
    protected val backTitle by lazy { findViewById<TextView>(R.id.back_title) }
    protected val rightLButton by lazy { findViewById<View>(R.id.right_l_button) }
    protected val rightButtonView by lazy { findViewById<TextView>(R.id.toolbar_right_button) }
    protected val moreButton by lazy { findViewById<ImageView>(R.id.more_button) }
    protected val titleLine by lazy { findViewById<View>(R.id.title_line) }

    protected fun getBackImg() :ImageView?=  backButton.run { if (this is ImageView) this else null }

    protected fun setBackRes(res:Int){
        val imageView = backButton
        if (imageView is ImageView){
            imageView.setImageResource(res)
        }
    }

    protected fun setBackImageTintList(colorList:ColorStateList) = setImageTintList(getBackImg(),colorList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView)
        initView(savedInstanceState)
        setListener()
    }

    open fun initView(savedInstanceState: Bundle?) {
        createView(titleLayoutRes(),titleFrameLayout)
        createView(contentLayoutRes(),contentFrameLayout)
        startInitView(savedInstanceState)
    }

    /**
     * 请求加载
     */
    private fun createView(@LayoutRes layoutRes: Int,viewGroup:ViewGroup?):View{
        var loadView: View = LayoutInflater.from(this).inflate(layoutRes, viewGroup,false)
        viewGroup?.isClickable = true
        viewGroup?.removeAllViews()
        viewGroup?.addView(loadView)
        return loadView
    }

    fun setBack(view: View?){
        view?.setOnClick {
            onBackPressed()
        }
    }

    open fun startInitView(savedInstanceState: Bundle?){}

    protected open fun onCreateRootRes():Int = R.layout.layout_xiu_layer_view

    override fun getPageContext(): Context = this
    override fun getCurrentAct(): Activity?  = this
    open fun setListener() {}
    @LayoutRes
    open fun contentLayoutRes(): Int = R.layout.layout_fragment
    open fun titleLayoutRes(): Int = R.layout.a_layout_toolbar_top_default
}