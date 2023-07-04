package com.xy.chat

import android.content.Context
import android.util.AttributeSet
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
import com.xy.base.utils.emo.EmoManager
import com.xy.base.utils.exp.addNewParent
import com.xy.base.utils.exp.getSpString
import com.xy.base.utils.exp.setSpString
import com.xy.base.utils.record.record.utils.Logger
import com.xy.base.widget.recycler.adapter.RecyclerSingleAdapter
import com.xy.base.widget.recycler.adapter.RecyclerSingleExpAdapter
import com.xy.base.widget.recycler.holder.BaseViewHolder
import com.xy.base.widget.recycler.listener.OnItemClickListener
import com.xy.base.widget.recycler.listener.RecyclerExpListener

class CommonEmoView@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :FrameLayout(context,attrs),RecyclerExpListener<EmoManager.EmoEntryMode>,OnItemClickListener<EmoManager.EmoEntryMode>{
    private val TAG by lazy { "CommonEmoView" }
    private var cowSize:Int = -1
    private var mHeadRootView :View?=null
    private var itemRes:Int = R.layout.a_page_load

    private var recentlyListener: CommonEmoRecentlyListener?=null

    private val recyclerView by lazy { RecyclerView(context) }
    private val delView by lazy { ImageView(context) }

    private var adapter:RecyclerSingleAdapter<EmoManager.EmoEntryMode> ?= null
    var listener: CommonEmoListener?=null

    fun init(cow:Int,res:Int): CommonEmoView {
        this.itemRes = res
        this.cowSize = cow
        return this
    }

    /**
     * 绑定删除事件
     */
    fun bindDelView(width:Int,height:Int,marginR:Int,marginB:Int,paddingH:Int,paddingV:Int,res:Int): CommonEmoView {
        delView.addNewParent(this)
        val params = LayoutParams(width, height)
        params.gravity = Gravity.BOTTOM and Gravity.RIGHT
        params.rightMargin = marginR
        params.bottomMargin = marginB
        delView.setPadding(paddingH,paddingV,paddingH,paddingV)
        delView.setImageResource(res)
        delView.layoutParams = params
        delView.setOnClickListener{
            listener?.onEmoDelClicked()
        }
        return this
    }

    /**
     * 绑定正文
     */
    fun bindExpItem(data:MutableList<EmoManager.EmoEntryMode>): CommonEmoView {
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
        adapter?.addHeadView(listener.onCommonEmoRecentlyRes())
        bindResetData(listener.onCommonContentView(mHeadRootView),getRecentlyData())
    }

    /**
     * 重新设置状态
     */
    private fun bindResetData(contentLinearLayout: LinearLayout?,data: MutableList<EmoManager.EmoEntryMode>){
        val contentLayout = contentLinearLayout?:return
        contentLayout.removeAllViews()
        for (index in 0 until cowSize){
            val itemView:View
            if (index < data.size){
                itemView = LayoutInflater.from(context).inflate(itemRes,null)
                val itemData = data[index]
                listener?.onBindItemView(itemView,itemData)
                itemView.setOnClickListener{
                    listener?.onEmoClicked(itemData)
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
    private fun getRecentlyData():MutableList<EmoManager.EmoEntryMode>{
        if (TAG.isNullOrEmpty())return ArrayList()
        try {
            val dataStr = context.getSpString(TAG,"[]")
            val data = JSON.parseArray(dataStr, EmoManager.EmoEntryMode::class.java)
            return data
        }catch (e:Exception){
            Logger.e(TAG,e.message+"====")
        }
        return ArrayList()
    }

    /**
     * 添加最近记录
     */
    private fun setRecentlyItem(item: EmoManager.EmoEntryMode){
        if (cowSize<=0 || TAG.isNullOrEmpty())return
        val oldData = getRecentlyData()
        for (oldItem in oldData){
            if (oldItem.text == item.text){
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
        context.setSpString(TAG,JSON.toJSONString(oldData))
        bindResetData(recentlyListener?.onCommonContentView(mHeadRootView),oldData)
    }


    override fun onBindViewHolder(holder: BaseViewHolder, data: EmoManager.EmoEntryMode, position: Int) {
        listener?.onBindItemView(holder.itemView,data)
    }

    override fun onItemClick(view: View, data: EmoManager.EmoEntryMode, holder: BaseViewHolder?) {
        listener?.onEmoClicked(data)
        setRecentlyItem(data)
    }


    interface CommonEmoListener{
        fun onEmoClicked(item: EmoManager.EmoEntryMode)
        fun onEmoDelClicked()
        fun onBindItemView(rootView:View?,data: EmoManager.EmoEntryMode)
    }

    interface CommonEmoRecentlyListener{
        fun onCommonEmoRecentlyRes():Int
        fun onCommonContentView(view: View?): LinearLayout?
    }





    init {
        addView(recyclerView)
    }
}