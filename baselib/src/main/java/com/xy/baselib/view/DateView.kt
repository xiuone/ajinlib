package com.xy.baselib.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.xy.baselib.R
import com.xy.baselib.adapter.DateAdapter
import com.xy.baselib.mvp.mode.DateMode
import com.xy.baselib.utils.AppUtil
import com.xy.baselib.utils.NestScrollGridLayoutManager
import com.xy.baselib.utils.setOnClick
import com.xiuone.adapter.adapter.RecyclerBaseAdapter
import com.xiuone.adapter.listener.OnItemClickListener
import kotlinx.android.synthetic.main.date_view.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * 年月日筛选
 */
class DateView(context: Context, attrs: AttributeSet?=null):FrameLayout(context,attrs), OnItemClickListener,View.OnClickListener{
    private var adapter: DateAdapter ?=null
    private var year:Int = 0
    private var month :Int = 0
    init {
        LayoutInflater.from(context).inflate(R.layout.date_view,this)
        date_recyclerView.layoutManager = NestScrollGridLayoutManager(context,7,false)


        val array = context.obtainStyledAttributes(attrs, R.styleable.DateView)

        val selectBackground = array.getResourceId(R.styleable.DateView_date_select_background,R.drawable.bg_black_0000_corners_30)
        val selectBackgroundNot = array.getResourceId(R.styleable.DateView_date_select_background_not,R.drawable.bg_transparent)
        val toDayBackGround = array.getResourceId(R.styleable.DateView_date_toDay_background,R.drawable.bg_transparent)
        val selectTextColor = array.getColor(R.styleable.DateView_date_select_textColor,AppUtil.getColor(context,R.color.white))
        val selectTextColorNot = array.getColor(R.styleable.DateView_date_select_textColor_not,AppUtil.getColor(context,R.color.gray_9999))
        val toDayTextColor = array.getColor(R.styleable.DateView_date_toDay_textColor,AppUtil.getColor(context,R.color.red_e32c))
        val commonTextColor = array.getColor(R.styleable.DateView_date_common_textColor,AppUtil.getColor(context,R.color.gray_3333))

        adapter = DateAdapter(selectBackground,selectBackgroundNot,toDayBackGround,selectTextColor,selectTextColorNot,toDayTextColor,commonTextColor)
        date_recyclerView.adapter = adapter
        adapter?.itemClickListener=this

        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH] + 1
        val nowDay = cal[Calendar.DAY_OF_MONTH]
        setNewData(year,month,nowDay)

        date_right_button.setOnClick(this)
        date_left_button.setOnClick(this)
    }

    override fun onItemClick(adapter: RecyclerBaseAdapter<*>, view: View, position: Int) {
        val data = adapter.dataController.getItem(position)
        if (data != null && data is DateMode) {
            if (data.nowMoth && adapter is DateAdapter){                 
                adapter?.changeSelectPosition(position)
            }else if (!data.nowMoth){
                jumpMonth(year, month,data.day, data.day>position)
            }
        }
    }

    /**
     * 跳动月份
     */
    private fun jumpMonth(nowYear:Int,nowMoth:Int,nowDay:Int,isLeft:Boolean){
        val selectDate = getJumpDate(nowYear,nowMoth,isLeft)
        setNewData(selectDate[0],selectDate[1],nowDay)
    }
    
    private fun setNewData(year: Int,month: Int,nowDay:Int){
        val maxDay =getMaxDay(year,month)
        val minWeek = getDayWeek(year,month,1)
        val maxWeek = getDayWeek(year,month,maxDay)

        val leftDate = getJumpDate(year,month,true)
        val leftMaxDay = getMaxDay(leftDate[0],leftDate[1])

        val nweData = ArrayList<DateMode>()
        for (index in(leftMaxDay-minWeek+2) ..leftMaxDay){
            val item = DateMode(year, month, index, false, false)
            adapter?.notifyChangeItem(nweData.size,item)
            nweData.add(item)
        }
        for (index in 1 .. maxDay){
            val cal = Calendar.getInstance()
            val nowYear = cal[Calendar.YEAR]
            val nowMonth = cal[Calendar.MONTH] + 1
            val item = DateMode(year, month, index, (nowYear == year && nowMonth == month && nowDay == index), nowDay == index)
            adapter?.notifyChangeItem(nweData.size,item)
            nweData.add(item)
        }
        for (index in 1..(7-maxWeek)){
            val item = DateMode(year, month, index, false,false)
            adapter?.notifyChangeItem(nweData.size,item)
            nweData.add(item)
        }
        date_title.text = "${year}年${if (month<10) "0$month" else "$month"}月"
        this.year = year
        this.month = month
        if (adapter?.dataController?.datas?.size?:0 <= 0)
            adapter?.dataController?.setNewData(nweData)
    }

    /**
     * 获取跳转的年月日
     */
    private fun getJumpDate(nowYear: Int,nowMoth: Int,isLeft: Boolean):ArrayList<Int>{
        var seletMoth = nowMoth
        var seletYear = nowYear
        if (isLeft && nowMoth > 1){
            seletMoth = nowMoth -1
            seletYear = nowYear
        }else if (isLeft && nowMoth <= 1){
            seletMoth = 12
            seletYear = nowYear-1
        }else if (!isLeft && nowMoth>=12){
            seletYear = nowYear+1
            seletMoth = 1
        }else if (!isLeft && nowMoth<12){
            seletYear = nowYear
            seletMoth = nowMoth +1
        }
        val data = ArrayList<Int>()
        data.add(seletYear)
        data.add(seletMoth)
        return data
    }


    /**
     * 获取某年某月最大天数
     */
    private fun getMaxDay(year:Int,month:Int):Int{
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month+1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取当前年月日的天数
     */
    private fun getDayWeek(year: Int,month: Int,day:Int):Int{
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month+1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        return (cal.get(Calendar.DAY_OF_WEEK) +1)%7
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.date_left_button->{
                jumpMonth(year,month,1,true)
            }
            R.id.date_right_button->{
                jumpMonth(year,month,1,false)
            }
        }
    }

    fun getSelectDate(): DateMode?{
        val adapter = this.adapter?:return null
        if (adapter.selectPosition in adapter.dataController.datas.indices)
            return adapter.dataController.datas[adapter.selectPosition]
        return null
    }
}