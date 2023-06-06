package com.xy.chat.chat.input.emo

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.xy.base.R
import com.xy.base.utils.exp.addNewParent
import com.xy.base.utils.exp.getSpString
import com.xy.base.utils.exp.setSpString
import com.xy.base.utils.record.record.utils.Logger
import com.xy.base.widget.recycler.adapter.RecyclerSingleAdapter
import com.xy.base.widget.recycler.adapter.RecyclerSingleExpAdapter
import com.xy.base.widget.recycler.holder.BaseViewHolder
import com.xy.base.widget.recycler.listener.OnItemClickListener
import com.xy.base.widget.recycler.listener.RecyclerExpListener

class CommonEmoView(context: Context) :FrameLayout(context),RecyclerExpListener<CommonEmoBean>,OnItemClickListener<CommonEmoBean>{
    private val TAG by lazy { "CommonEmoView" }
    private var cowSize:Int = -1;
    private var recentlyTag:String?= null
    private var mHeadRootView :View?=null
    private var itemRes:Int = R.layout.a_page_load

    private var recentlyListener: CommonEmoRecentlyListener?=null

    private val recyclerView by lazy { RecyclerView(context) }
    private val delView by lazy { ImageView(context) }

    private var adapter:RecyclerSingleAdapter<CommonEmoBean> ?= null
    var listener:CommonEmoListener?=null

    fun init(tag:String?,cow:Int,res:Int):CommonEmoView{
        this.itemRes = res
        recentlyTag = "CommonEmoView--$tag"
        this.cowSize = cow
        return this
    }

    /**
     * 绑定删除事件
     */
    fun bindDelView(width:Int,height:Int,marginR:Int,marginB:Int,paddingH:Int,paddingV:Int,res:Int):CommonEmoView{
        delView.addNewParent(this)
        val params = LayoutParams(width, height)
        params.gravity = Gravity.BOTTOM and Gravity.RIGHT
        params.rightMargin = marginR
        params.bottomMargin = marginB
        delView.setPadding(paddingH,paddingV,paddingH,paddingV)
        delView.setImageResource(res)
        delView.layoutParams = params
        delView.setOnClickListener{
            listener?.onDelClicked()
        }
        return this
    }

    /**
     * 绑定正文
     */
    fun bindExpItem(data:MutableList<CommonEmoBean>):CommonEmoView{
        if (cowSize > 0) {
            this.adapter = RecyclerSingleExpAdapter(itemRes,this)
            recyclerView.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            recyclerView.layoutManager = GridLayoutManager(context, cowSize)
            recyclerView.adapter = this.adapter
            this.adapter?.setNewData(data)
        }
        return this
    }

    /**
     * 绑定最近选中的view
     */
    fun bindRecentlyView(listener: CommonEmoRecentlyListener){
        recentlyListener = listener
        mHeadRootView = LayoutInflater.from(context).inflate(listener.onCommonEmoRecentlyRes(),null)
        adapter?.addHeadView(mHeadRootView)
        bindResetData(listener.onCommonContentView(),getRecentlyData())
    }

    /**
     * 重新设置状态
     */
    private fun bindResetData(contentLinearLayout: LinearLayout?,data: MutableList<CommonEmoBean>){
        val contentLayout = contentLinearLayout?:return
        contentLayout.removeAllViews()
        for (index in 0 until cowSize){
            val itemView:View
            if (index < data.size){
                itemView = LayoutInflater.from(context).inflate(itemRes,null)
                val itemData = data.get(index)
                listener?.onBindItemView(itemView,delView,itemData)
                itemView.setOnClickListener{
                    listener?.onClicked(itemData)
                }
            }else{
                itemView = Space(context)
            }
            itemView.layoutParams = LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1F)
            contentLayout.addView(itemView)
        }
        if (contentLayout.childCount>0) {
            mHeadRootView?.visibility = View.VISIBLE
        }else{
            mHeadRootView?.visibility = View.GONE
        }
    }

    /**
     * 获取最近记录
     */
    private fun getRecentlyData():MutableList<CommonEmoBean>{
        if (recentlyTag.isNullOrEmpty())return ArrayList()
        try {
            val dataStr = context.getSpString(recentlyTag,"[]")
            val data = JSON.parseArray(dataStr,CommonEmoBean::class.java)
            return data
        }catch (e:Exception){
            Logger.e(TAG,e.message+"====")
        }
        return ArrayList()
    }

    /**
     * 添加最近记录
     */
    private fun setRecentlyItem(item: CommonEmoBean){
        if (cowSize<=0 || recentlyTag.isNullOrEmpty())return
        val oldData = getRecentlyData()
        for (oldItem in oldData){
            if (oldItem.tag == item.tag){
                oldData.remove(oldItem)
                break
            }
        }
        if (oldData.size > 0){
            oldData.add(0,item)
        }else{
            oldData.add(item)
        }
        if (oldData.size > cowSize){
            oldData.removeAt(cowSize)
        }
        context.setSpString(recentlyTag,JSON.toJSONString(oldData))
        bindResetData(recentlyListener?.onCommonContentView(),oldData)
    }


    override fun onBindViewHolder(holder: BaseViewHolder, data: CommonEmoBean, position: Int) {
        listener?.onBindItemView(holder.itemView,delView,data)
    }

    override fun onItemClick(view: View, data: CommonEmoBean, holder: BaseViewHolder?) {
        listener?.onClicked(data)
        setRecentlyItem(data)
    }


    interface CommonEmoListener{
        fun onClicked(item:CommonEmoBean)
        fun onDelClicked()
        fun onBindItemView(rootView:View?,delView:View?,data:CommonEmoBean)
    }

    interface CommonEmoRecentlyListener{
        fun onCommonEmoRecentlyRes():Int
        fun onCommonContentView(): LinearLayout?
    }





    init {
        addView(recyclerView)
    }
}