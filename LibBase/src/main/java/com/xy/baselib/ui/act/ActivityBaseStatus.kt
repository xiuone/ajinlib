package com.xy.baselib.ui.act

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
import com.xy.baselib.exp.setOnClick
import com.xy.baselib.softkey.SoftKeyBoardDetector

/**
 * 加载状态
 */
abstract class ActivityBaseStatus : ActivityBase(), BaseView {
    private val baseLayoutRes = -1
    protected val rootView by lazy { LayoutInflater.from(this).inflate(R.layout.layout_xiu_base_view, null) }
    protected val titleFrameLayout: FrameLayout by lazy { findViewById(R.id.title_render_layout) }
    protected val contentFrameLayout: FrameLayout by lazy { findViewById(R.id.content_render_layout) }
    protected val loadingLayout: FrameLayout by lazy { findViewById(R.id.loading_render_layout) }
    protected val errorLayout: FrameLayout by lazy {  findViewById(R.id.error_render_layout)}
    protected val prLayout: FrameLayout by lazy { findViewById(R.id.pr_render_layout) }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView)
        initView(savedInstanceState)
        setListener()
        loadData()
    }

    open fun initView(savedInstanceState: Bundle?) {
        createView(titleLayoutRes(),titleFrameLayout)
        createView(contentLayoutRes(),contentFrameLayout)
    }

    fun setReLoadView(view: View?) {
        if (view == null) return
        view.setOnClickListener(View.OnClickListener { loadData() })
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

    fun setBack(view: View?){
        view?.setOnClick {
            finish()
        }
    }


    override fun showLoading(@IdRes idRes: Int, @StringRes strRes:Int) :View{
        return showLoading(loadProgressLayoutRes(),idRes,strRes)
    }

    override fun showLoading(@IdRes idRes: Int, strRes:String) :View{
        return showLoading(loadProgressLayoutRes(),idRes,strRes)
    }

    override fun showLoading(@LayoutRes layoutRes:Int, @IdRes idRes: Int, @StringRes strRes:Int):View {
        return  return showLoading(loadProgressLayoutRes(),idRes,getResString(strRes));
    }

    override fun showLoading(@LayoutRes layoutRes:Int, @IdRes idRes: Int,  strRes:String?):View {
        val loadView = createView(layoutRes,loadingLayout)
        loadView.findViewById<TextView>(idRes)?.text = strRes
        loadingLayout.visibility = View.VISIBLE
        SoftKeyBoardDetector.closeKeyBord(titleFrameLayout)
        return loadView;
    }

    override fun disLoading() {
        loadingLayout.removeAllViews()
        loadingLayout.visibility = View.GONE
    }

    override fun getPageContext(): Context = this
    open fun setListener() {}
    open fun loadData() {}
    @LayoutRes
    abstract fun contentLayoutRes(): Int
    open fun prLayoutRes(): Int = R.layout.load_page_common_load
    open fun titleLayoutRes(): Int = R.layout.a_layout_toolbar_top_default
    open fun loadProgressLayoutRes() = R.layout.load_page_common_load
    open fun errorLayoutRes(): Int = R.layout.a_page_err
}