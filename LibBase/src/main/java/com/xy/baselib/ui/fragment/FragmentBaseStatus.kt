package com.xy.baselib.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.xy.baselib.R
import com.xy.baselib.exp.getResString
import com.xy.baselib.mvp.view.BaseView

/**
 * 加载状态
 */
abstract class FragmentBaseStatus : FragmentBase() ,BaseView{
    protected val rootView by lazy { LayoutInflater.from(context).inflate(R.layout.layout_base_view, null) }
    protected val titleFrameLayout: FrameLayout by lazy { rootView.findViewById(R.id.title_render_layout) }
    protected val contentFrameLayout: FrameLayout by lazy { rootView.findViewById(R.id.content_render_layout) }
    protected val loadingLayout: FrameLayout by lazy { rootView.findViewById(R.id.loading_render_layout) }
    protected val errorLayout: FrameLayout by lazy {  rootView.findViewById(R.id.error_render_layout)}
    protected val prLayout: FrameLayout by lazy { rootView.findViewById(R.id.pr_render_layout) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState, view)
        setListener()
        loadData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootView
    }


    open fun initView(savedInstanceState: Bundle?, rootView: View?) {
        createView(titleLayoutRes(),titleFrameLayout)
        createView(contentLayoutRes(),contentFrameLayout)
        titleFrameLayout?.visibility = View.GONE
    }

    fun setReLoadView(view: View?) {
        if (view == null) return
        view.setOnClickListener(View.OnClickListener { loadData() })
    }

    /**
     * 请求加载
     */
    private fun createView(@LayoutRes layoutRes: Int,viewGroup:ViewGroup?):View{
        var loadView: View = LayoutInflater.from(context).inflate(layoutRes, null)
        viewGroup?.isClickable = true
        viewGroup?.removeAllViews()
        viewGroup?.addView(loadView)
        return loadView
    }


    private fun resetShowStatus(){
        errorLayout?.visibility = View.GONE
        loadingLayout?.visibility = View.GONE
        contentFrameLayout?.visibility = View.GONE
        prLayout?.visibility = View.GONE
    }


    /**
     * 加载成功
     */
    override fun loadSuc() {
        resetShowStatus()
        contentFrameLayout?.visibility = View.VISIBLE
    }



    override fun showPre(): View {
        return showPre(prLayoutRes())
    }

    override fun showPre(layoutRes: Int): View {
        val preView = createView(layoutRes,prLayout)
        resetShowStatus()
        prLayout?.visibility = View.VISIBLE
        return preView
    }


    /**
     * 加载失败
     * @return
     */
    override fun showError(): View {
        return showError(errorLayoutRes())
    }

    override fun showError(layoutRes: Int): View {
        val errView = createView(layoutRes,errorLayout)
        resetShowStatus()
        errView?.visibility = View.VISIBLE
        return errView
    }


    override fun showLoading(@IdRes idRes: Int, @StringRes strRes:Int) :View{
        return showLoading(loadProgressLayoutRes(),idRes,strRes)
    }

    override fun showLoading(@LayoutRes layoutRes:Int, @IdRes idRes: Int, @StringRes strRes:Int):View {
        val loadView = createView(layoutRes,loadingLayout)
        loadView.findViewById<TextView>(idRes)?.text = context?.getResString(strRes)
        loadingLayout?.visibility = View.VISIBLE
        return loadView;
    }

    override fun disLoading() {
        loadingLayout?.removeAllViews()
        loadingLayout?.visibility = View.GONE
    }

    override fun getPageContext(): Context? = context

    open fun setListener() {}
    open fun loadData() {}
    @LayoutRes
    abstract fun contentLayoutRes(): Int
    open fun prLayoutRes(): Int = R.layout.a_page_load
    open fun titleLayoutRes(): Int = R.layout.a_layout_toolbar_top_default
    open fun loadProgressLayoutRes() = R.layout.dialog_common_load
    open fun errorLayoutRes(): Int = R.layout.a_page_err

}